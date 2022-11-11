package application;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameService implements Runnable{

    private final Socket player1;
    private final Socket player2;

    private Scanner in;
    private PrintWriter out;

    public GameService(Socket socket1, Socket socket2) {
        player1 = socket1;
        player2 = socket2;
    }

    @Override
    public void run() {
        try {
            try {
                in = new Scanner(player1.getInputStream());
                out = new PrintWriter(player1.getOutputStream());
                test();
            } finally {
                player1.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void test() {
        while (true) {
            if (!in.hasNext()) {
                return;
            }
            String cmd = in.next();
            if ("quit".equals(cmd)) {
                return;
            }
            out.println(cmd);
            out.flush();
        }
    }
}
