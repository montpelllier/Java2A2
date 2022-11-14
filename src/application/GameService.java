package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameService implements Runnable{

    List<Socket> playerList = new ArrayList<>();
    List<Thread> threadList = new ArrayList<>();

    Socket player1;
    Socket player2;
    private static final int EMPTY = 0;
    private static int[][] chessBoard;
    private static boolean TURN = false;

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
                throw new RuntimeException(e);
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
                    send("game start:2", out);
                } else {
                    send("wait", out);
                }
            } else if (cmd.equals("quit")) {
                if (waiting == player) waiting = null;
                return;
            } else if (cmd.startsWith("move:")) {
                String[] pos = cmd.substring(5).split(",");
                int x = Integer.getInteger(pos[0]);
                int y = Integer.getInteger(pos[1]);
                if (player == player1 && chessBoard[x][y] == EMPTY) {
                    chessBoard[x][y] = 1;
                    send(String.format("oppo:%d,%d", x, y), player2);
                } else if (player == player2 && chessBoard[x][y] == EMPTY) {
                    chessBoard[x][y] = 2;
                    send(String.format("oppo:%d,%d", x, y), player1);
                } else {
                    send("illegal position", player);
                }
            } else if (cmd.startsWith("login:")) {
                String[] logInfo = cmd.substring(6).split(",");
//                String accout = logInfo[0];
//                String psw = logInfo[1];
            } else {
                System.out.println("unknown command");
            }

        }
    }

    public void send(String msg, PrintWriter printWriter) {
        printWriter.println(msg);
        printWriter.flush();
    }

    public void send(String msg, Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(msg);
            out.println();
        }catch (Exception e) {
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
