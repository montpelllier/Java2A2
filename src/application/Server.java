package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {

  private static final Logger logger = Logger.getLogger(Client.class.getName());

  public static void main(String[] args) throws IOException {

    ServerSocket server = new ServerSocket(Constant.PORT);
    GameService service = new GameService();
    Thread thread = new Thread(service);
    thread.start();

    System.out.println("Waiting for clients to connect...");
    while (true) {
      Socket clientSocket = server.accept();
      System.out.printf("player %s connected.\n", clientSocket.getPort());
      service.addPlayer(clientSocket);

    }
  }

}
