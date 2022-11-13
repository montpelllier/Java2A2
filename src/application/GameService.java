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
    private static boolean[][] flag;
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
            String cmd = in.next();
            System.out.println(cmd);

            if (cmd.equals("start")) {
                if (startNewGame(player)) {
                    send("game start:2", out);
                } else {
                    send("wait", out);
                }
            } else if (cmd.equals("quit")) {
                return;
            } else if (cmd.startsWith("move:")) {
                String pos = cmd.substring(5);
            }

//            send(cmd, out);
        }
    }

    public void send(String msg, PrintWriter printWriter) {
        printWriter.println(msg);
        printWriter.flush();
    }

    public synchronized boolean startNewGame(Socket player) throws IOException {
        if (waiting == null) {
            waiting = player;
            return false;
        } else {
            chessBoard = new int[3][3];
            flag = new boolean[3][3];
            player1 = waiting;
            player2 = player;

            send("start game: 1", new PrintWriter(waiting.getOutputStream()));

            waiting = null;
            return true;
        }
    }
}
