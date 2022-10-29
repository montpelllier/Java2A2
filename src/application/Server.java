package application;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

  private static final Logger logger = Logger.getLogger(Client.class.getName());
  private static final int port = 8888;

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      while (true) {
        Socket socket = serverSocket.accept();
        InetAddress address = socket.getInetAddress();
        logger.log(Level.SEVERE,
            "Start a new thread for client at " + new Date() + "\naddress is: " + address);

        new Thread(new GameHandler(socket));
        Thread.sleep(100);
      }

    } catch (Exception e) {
      logger.log(Level.WARNING, e.toString());

    }

  }

}
