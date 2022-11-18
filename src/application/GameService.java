package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GameService implements Runnable {

    private static final int EMPTY = 0;
    private static int[][] chessBoard;
    private static final boolean TURN = false;
    List<Socket> playerList = new ArrayList<>();
    List<Thread> threadList = new ArrayList<>();
    Socket player1;
    Socket player2;
    Socket waiting = null;

    public void addPlayer(Socket player) {
        playerList.add(player);
        Thread thread = new Thread(() -> {
            try {
                test(player);
                player.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        threadList.add(thread);
    }

    @Override
    public void run() {
        while (true) {
            threadList.forEach(Thread::start);
            threadList.clear();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void test(Socket player) throws IOException {
        Scanner in = new Scanner(player.getInputStream());
        PrintWriter out = new PrintWriter(player.getOutputStream());

        while (true) {
            if (!in.hasNext()) {
                return;
            }
            String cmd = in.nextLine();
            System.out.printf("from player %d: %s\n", player.getPort(), cmd);

            if (cmd.equals("start")) {
                if (startNewGame(player)) {
                    send("game start:-1", out);
                } else {
                    send("wait", out);
                }
            } else if (cmd.equals("quit")) {
                if (waiting == player) waiting = null;
                return;
            } else if (cmd.startsWith("move:")) {
                String[] pos = cmd.substring(5).split(",");
                int x = Integer.parseInt(pos[0]);
                int y = Integer.parseInt(pos[1]);
                String backInfo = String.format("oppo:%d,%d", x, y);
                if (player == player1 || player == player2 && chessBoard[x][y] == EMPTY) {
                    chessBoard[x][y] = player == player1 ? 1 : -1;
                    checkGameOver();
                    send(backInfo, player == player2 ? player1 : player2);
                } else {
                    send("illegal position", player);
                }
            } else if (cmd.startsWith("login:")) {
                //todo
                String[] logInfo = cmd.substring(6).split(",");
                String accout = logInfo[0];
                String psw = logInfo[1];
            } else {
                System.out.println("unknown command");
            }

        }
    }

    private void checkGameOver() {
        boolean draw = true;
        for (int i = 0; i < 3; i++) {
            //检查行
            int sum = Arrays.stream(chessBoard[i]).sum();
            if (checkResult(sum)) {
                //
            }
            //检查列
            sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += chessBoard[j][i];
                if (chessBoard[i][j] == EMPTY) {
                    draw = false;
                }
            }
            if (checkResult(sum)) {
                //return true;
            }
        }
        //检查对角
        for (int i = 0, sum = 0; i<3;i++) {
            sum += chessBoard[i][i];
            if (checkResult(sum)) {
                //return true;
            }
        }
        for (int i = 0, sum = 0; i<3;i++) {
            sum += chessBoard[2 - i][i];
            if (checkResult(sum)) {
                //return true;
            }
        }
        //检查平局
        if (draw) {
            send("result:draw", player1);
            send("result:draw", player2);
            //return true;
        }
        //return false;
    }

    private boolean checkResult(int num) {
        if (num == 3) {
            send("result:win", player1);
            send("result:lose", player2);
        } else if (num == -3) {
            send("result:lose", player1);
            send("result:win", player2);
        } else {
            return false;
        }
        return true;
    }

    public void send(String msg, PrintWriter printWriter) {
        printWriter.println(msg);
        printWriter.flush();
    }

    public void send(String msg, Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(msg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean startNewGame(Socket player) {
        if (waiting == null) {
            waiting = player;
            return false;
        } else {
            chessBoard = new int[3][3];
            player1 = waiting;
            player2 = player;

            send("game start:1", waiting);

            waiting = null;
            return true;
        }
    }
}
