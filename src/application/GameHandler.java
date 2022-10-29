package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameHandler implements Runnable {

  private static final Logger logger = Logger.getLogger(Client.class.getName());
  private final Socket socket;

  public GameHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    System.out.println("handler start working...");
    try {
      DataInputStream inputStream = new DataInputStream(socket.getInputStream());
      DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

      logger.log(Level.INFO, inputStream.readUTF());
      outputStream.writeUTF("hello!");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
