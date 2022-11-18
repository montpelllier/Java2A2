package application;

import application.controller.Controller;
import application.controller.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Application {

    private FXMLLoader loader;
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    public String userName;
    public int gross;
    public int win;
    //first = 1, second = 2
    public int hand;
    public boolean isMyTurn = false;

    public final int[][] chessBoard = new int[3][3];

    private Stage stage;
    private Socket socket;
//    private Scanner in;
//    private PrintWriter out;

    public Client() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Tic Tac Toe");
        enterView(Constant.LOGIN_VIEW_FXML);
        stage.show();
        stage.setResizable(false);

        try {
            socket = new Socket();
            SocketAddress address = new InetSocketAddress("localhost", Constant.PORT);
            socket.connect(address, 500);
//            socket = new Socket("localhost", Constant.PORT);
//            socket.setSoTimeout(300);
//
//            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
//            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            Scanner in = new Scanner(inputStream);
//            out = new PrintWriter(outputStream);

            Thread thread = new Thread(this::connect);
            thread.start();

        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
        }

    }

    public void enterView(String viewPath) {
        try {
            Controller controller = (Controller) replaceSceneContent(viewPath);
            controller.setApp(this);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void sendCmd(String cmd) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(cmd);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换场景
     *
     * @param fxml
     * @return
     */
    private Initializable replaceSceneContent(String fxml) {
        loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(getClass().getClassLoader().getResource(fxml));

        try {
            Pane page = loader.load();
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "页面加载异常！" + e);
        }
        return loader.getController();
    }

    public void connect() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            while (true) {
                if (!in.hasNext()) {
                    return;
                }
                String receive = in.nextLine();
                System.out.println("from server: "+receive);

                if (receive.equals("waiting")) {
                    //todo: waiting window
                } else if (receive.startsWith("game start:")) {
                    hand = Integer.parseInt(receive.substring(11));
                    isMyTurn = hand == 1;
                    enterView(Constant.GAME_VIEW_FXML);
                } else if (receive.startsWith("oppo:")) {
                    String[] pos = receive.substring(5).split(",");
                    int x = Integer.parseInt(pos[0]);
                    int y = Integer.parseInt(pos[1]);
                    chessBoard[x][y] = -hand;
                    isMyTurn = true;
                    GameController controller = loader.getController();
                    Platform.runLater(controller::drawChess);
//                controller.drawChess();
                } else if (receive.startsWith("result:")){
                    String res = receive.substring(7);
                    System.out.println(res);
                    if (res.equals("win")) {
                        //todo
                    } else if (res.equals("lose")) {
                        //todo
                    }
                    hand = 0;
                    for (int i = 0; i < chessBoard.length; i++) {
                        for (int j = 0; j < chessBoard[0].length; j++) {
                            chessBoard[i][j] = 0;
                        }
                    }
                    isMyTurn = false;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            sendCmd("quit");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.stage.close();
        }
    }
}
