package application;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class GameService implements Runnable {

  private static final int EMPTY = 0;
  private static int[][] chessBoard;
  Logger logger = Logger.getLogger(this.getClass().getName());
  List<Socket> playerList = new ArrayList<>();
  List<Thread> threadList = new ArrayList<>();
  Map<Socket, Date> startTime = new HashMap<>();

  Map<Socket, String> nameMap = new HashMap<>();
  Socket player1;
  Socket player2;
  Socket waiting = null;

  public void addPlayer(Socket player) {
    playerList.add(player);
    Thread thread = new Thread(() -> {
      try {
        handle(player);
        player.close();
      } catch (Exception e) {
        e.printStackTrace();
        if (player == player1) {
          send("result:win", player2);
          clean();
        } else if (player == player2) {
          send("result:win", player1);
          clean();
        }
        playerList.remove(player);
      }
    });
    threadList.add(thread);
    startTime.put(player, new Date());
  }

  @Override
  public void run() {
    while (true) {
      threadList.forEach(Thread::start);
      threadList.clear();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void handle(Socket player) {
    try {
      Scanner in = new Scanner(player.getInputStream());
      String cmd;
      while (true) {
        if (new Date().getTime() - startTime.get(player).getTime() > Constant.SERVER_WAIT_TIME) {
          logger.warning(String.format("player %d time out\n", player.getPort()));

          if (player == player1 || player == player2) {
            String userName1 = nameMap.get(player);
            String userName2 = nameMap.get(player == player2 ? player1 : player2);

            JSONObject jsonObject = readJson(Constant.ACCOUNT_JSON);
            JSONArray jsonArray = jsonObject.getJSONArray("accounts");
            for (Object account : jsonArray) {
              JSONObject accountJsonObject = (JSONObject) account;
              String name = (String) (accountJsonObject.get("user_name"));
              Integer gross = (Integer) accountJsonObject.get("gross_game");
              Integer win = (Integer) accountJsonObject.get("win");

              if (name.equals(userName1)) {
                accountJsonObject.put("gross_game", gross + 1);
              }
              if (name.equals(userName2)) {
                accountJsonObject.put("gross_game", gross + 1);
                accountJsonObject.put("win", win + 1);
              }
            }
            jsonObject.put("accounts", jsonArray);
            writeJson(Constant.ACCOUNT_JSON, jsonObject);

            send("result:win", player == player2 ? player1 : player2);
          }
          playerList.remove(player);
          nameMap.remove(player);
          return;
        }
        try {
          cmd = in.nextLine();
        } catch (Exception e) {
          Thread.sleep(500);
          continue;
        }

        logger.info(String.format("from player %d: %s\n", player.getPort(), cmd));
        if (cmd.equals("start")) {
          if (startNewGame(player)) {
            send("game start:-1", player);
          } else {
            send("wait", player);
          }
        } else if (cmd.equals("quit")) {
          if (waiting == player) {
            waiting = null;
          }
          return;
        } else if (cmd.startsWith("move:")) {
          String[] pos = cmd.substring(5).split(",");
          int x = Integer.parseInt(pos[0]);
          int y = Integer.parseInt(pos[1]);
          String backInfo = String.format("oppo:%d,%d", x, y);
          if ((player == player1 || player == player2) && chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = player == player1 ? 1 : -1;
            checkGameOver();
            send(backInfo, player == player2 ? player1 : player2);
          } else {
            send("illegal position", player);
          }
        } else if (cmd.startsWith("login:")) {
          String[] logInfo = cmd.substring(6).split(",");
          if (logInfo.length != 2) {
            logger.warning("error account info");
            continue;
          }
          String userName = logInfo[0];
          String psw = logInfo[1];
          boolean accountExist = false;

          JSONObject jsonObject = readJson(Constant.ACCOUNT_JSON);
          JSONArray jsonArray = jsonObject.getJSONArray("accounts");
          for (Object account : jsonArray) {
            String name = (String) ((JSONObject) account).get("user_name");
            String password = (String) ((JSONObject) account).get("password");
            if (name.equals(userName) && password.equals(psw)) {
              Integer gross = (Integer) ((JSONObject) account).get("gross_game");
              Integer win = (Integer) ((JSONObject) account).get("win");
              Integer tie = (Integer) ((JSONObject) account).get("tie");
              send(String.format("login:%s,%d,%d,%d", name, gross, win, tie), player);
              accountExist = true;
              nameMap.put(player, userName);
              break;
            }
          }
          if (!accountExist) {
            send("error:incorrect user name or password!", player);
          }

        } else if (cmd.startsWith("reg:")) {
          String[] regInfo = cmd.substring(4).split(",");
          if (regInfo.length != 2) {
            logger.warning("error account info");
            continue;
          }
          String userName = regInfo[0];
          String psw = regInfo[1];
          boolean accountExist = false;

          JSONObject jsonObject = readJson(Constant.ACCOUNT_JSON);
          JSONArray jsonArray = jsonObject.getJSONArray("accounts");
          for (Object account : jsonArray) {
            String name = (String) ((JSONObject) account).get("user_name");
            if (name.equals(userName)) {
              send("error:account already existed!", player);
              accountExist = true;
              break;
            }
          }
          if (!accountExist) {
            JSONObject account = new JSONObject();
            account.put("user_name", userName);
            account.put("password", psw);
            account.put("gross_game", 0);
            account.put("win", 0);
            account.put("tie", 0);
            //写入JSON文件
            jsonArray.add(account);
            jsonObject.put("accounts", jsonArray);
            writeJson(Constant.ACCOUNT_JSON, jsonObject);

            nameMap.put(player, userName);
            send(String.format("login:%s,%d,%d,%d", userName, 0, 0, 0), player);
          }
        } else {
          System.out.println("unknown command");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private synchronized void checkGameOver() {
    boolean isTie = true;
    for (int i = 0; i < 3; i++) {
      //检查行
      int sum = Arrays.stream(chessBoard[i]).sum();
      if (checkResult(sum)) {
        return;
      }
      //检查列
      sum = 0;
      for (int j = 0; j < 3; j++) {
        sum += chessBoard[j][i];
        if (chessBoard[i][j] == EMPTY) {
          isTie = false;
        }
      }
      if (checkResult(sum)) {
        return;
      }
    }
    //检查对角
    for (int i = 0, sum = 0; i < 3; i++) {
      sum += chessBoard[i][i];
      if (checkResult(sum)) {
        return;
      }
    }
    for (int i = 0, sum = 0; i < 3; i++) {
      sum += chessBoard[2 - i][i];
      if (checkResult(sum)) {
        return;
      }
    }
    //检查平局
    if (isTie) {
      String userName1 = nameMap.get(player1);
      String userName2 = nameMap.get(player2);

      JSONObject jsonObject = readJson(Constant.ACCOUNT_JSON);
      JSONArray jsonArray = jsonObject.getJSONArray("accounts");
      for (Object account : jsonArray) {
        JSONObject accountJsonObject = (JSONObject) account;
        String name = (String) (accountJsonObject.get("user_name"));
        Integer gross = (Integer) accountJsonObject.get("gross_game");
        Integer tie = (Integer) accountJsonObject.get("tie");

        if (name.equals(userName1)) {
          accountJsonObject.put("gross_game", gross + 1);
          accountJsonObject.put("tie", tie + 1);
        }
        if (name.equals(userName2)) {
          accountJsonObject.put("gross_game", gross + 1);
          accountJsonObject.put("tie", tie + 1);
        }
      }
      jsonObject.put("accounts", jsonArray);
      writeJson(Constant.ACCOUNT_JSON, jsonObject);

      send("result:tie", player1);
      send("result:tie", player2);
    }
  }

  private synchronized boolean checkResult(int num) {
    Socket winner = null, loser = null;
    if (num == 3) {
      winner = player1;
      loser = player2;
    } else if (num == -3) {
      winner = player2;
      loser = player1;
    }

    if (winner != null) {
      String userName1 = nameMap.get(winner);
      String userName2 = nameMap.get(loser);

      JSONObject jsonObject = readJson(Constant.ACCOUNT_JSON);
      JSONArray jsonArray = jsonObject.getJSONArray("accounts");
      for (Object account : jsonArray) {
        JSONObject accountJsonObject = (JSONObject) account;
        String name = (String) (accountJsonObject.get("user_name"));
        Integer gross = (Integer) accountJsonObject.get("gross_game");
        Integer win = (Integer) accountJsonObject.get("win");

        if (name.equals(userName1)) {
          accountJsonObject.put("gross_game", gross + 1);
          accountJsonObject.put("win", win + 1);
        }
        if (name.equals(userName2)) {
          accountJsonObject.put("gross_game", gross + 1);
        }
      }
      jsonObject.put("accounts", jsonArray);
      writeJson(Constant.ACCOUNT_JSON, jsonObject);

      send("result:win", winner);
      send("result:lose", loser);
      return true;
    }
    return false;
  }

  public synchronized void send(String msg, Socket socket) {
    try {
      PrintWriter out = new PrintWriter(socket.getOutputStream());
      out.println(msg);
      out.flush();
      startTime.put(socket, new Date());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private synchronized void clean() {
    player1 = null;
    player2 = null;
  }

  public synchronized boolean startNewGame(Socket player) {
    if (waiting == null) {
      waiting = player;
      return false;
    } else if (waiting != player) {
      chessBoard = new int[3][3];
      player1 = waiting;
      player2 = player;

      send("game start:1", waiting);

      waiting = null;
      return true;
    }
    return false;
  }

  public synchronized JSONObject readJson(String filePath) {
    StringBuffer sb = new StringBuffer();
    try {
      File file = new File(filePath);
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      fr.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return JSON.parseObject(sb.toString());
  }

  public synchronized void writeJson(String filePath, JSONObject jsonObject) {
    try {
      File file = new File(filePath);
      FileWriter fw = new FileWriter(file);
//    System.out.println(jsonObject.toJSONString());
      fw.write(jsonObject.toJSONString());
      fw.flush();

      fw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
