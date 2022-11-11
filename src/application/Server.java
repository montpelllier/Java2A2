package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

  private static final Logger logger = Logger.getLogger(Client.class.getName());

  public static void main(String[] args) throws IOException {

    final int SBAP_PORT = 8888;
    ServerSocket server = new ServerSocket(SBAP_PORT);

    System.out.println("Waiting for clients to connect...");
    while (true) {
      Socket clientSocket1 = server.accept();
      System.out.println("player1 connected.");
      Socket clientSocket2 = server.accept();
      System.out.println("player2 connected.");

      GameService service = new GameService(clientSocket1, clientSocket2);
      Thread thread = new Thread(service);
      thread.start();
    }
  }

}
