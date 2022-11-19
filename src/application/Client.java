package application;

import application.controller.Controller;
import application.controller.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Application {
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    public final int[][] chessBoard = new int[3][3];
    public String userName;
    public int gross;
    public int win;
    public int tie;
    public int hand;
    public boolean isMyTurn = false;
    private FXMLLoader loader;
    private Stage stage;
    private Socket socket;
    private Date startTime = new Date();

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
        stage.setOnCloseRequest(windowEvent -> {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
//            socket = new Socket();
//            SocketAddress address = new InetSocketAddress("localhost", Constant.PORT);
//            socket.connect(address, 500);
            socket = new Socket("localhost", Constant.PORT);
            socket.setSoTimeout(Constant.CLIENT_WAIT_TIME);

            Thread thread = new Thread(this::handle);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
            stage.close();
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
            startTime = new Date();
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

    public void handle() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            String receive;
            while (true) {
                if (new Date().getTime() - startTime.getTime() > Constant.CLIENT_WAIT_TIME) {
                    System.out.println("time out");
                    Platform.runLater(this::close);
                    return;
                }

                try {
                    receive = in.nextLine();
                } catch (Exception e) {
                    Thread.sleep(500);
                    continue;
                }

                System.out.println("from server: " + receive);

                if (receive.equals("waiting")) {
                    //todo: waiting window
                } else if (receive.startsWith("login:")) {
                    String[] backInfo = receive.substring(6).split(",");
                    userName = backInfo[0];
                    gross = Integer.parseInt(backInfo[1]);
                    win = Integer.parseInt(backInfo[2]);
                    tie = Integer.parseInt(backInfo[3]);
                    logger.info(String.format("name:%s, gross:%d, win:%d, tie:%d", userName, gross, win, tie));
                    Platform.runLater(() -> enterView(Constant.HOME_VIEW_FXML));
                } else if (receive.startsWith("game start:")) {
                    hand = Integer.parseInt(receive.substring(11));
                    isMyTurn = hand == 1;
                    Platform.runLater(() -> enterView(Constant.GAME_VIEW_FXML));
                } else if (receive.startsWith("oppo:")) {
                    String[] pos = receive.substring(5).split(",");
                    int x = Integer.parseInt(pos[0]);
                    int y = Integer.parseInt(pos[1]);
                    chessBoard[x][y] = -hand;
                    isMyTurn = true;
                    GameController controller = loader.getController();
                    Platform.runLater(controller::drawChess);
                } else if (receive.startsWith("result:")) {
                    String res = receive.substring(7);
//                    System.out.println(res);
                    gross++;
                    if (res.equals("win")) {
                        //todo
                        win++;
                    } else if (res.equals("tie")) {
                        //todo
                        tie++;
                    }
                    hand = 0;
                    for (int i = 0; i < chessBoard.length; i++) {
                        for (int j = 0; j < chessBoard[0].length; j++) {
                            chessBoard[i][j] = 0;
                        }
                    }
                    isMyTurn = false;
                } else if (receive.startsWith("error:")) {
                    String info = receive.substring(6);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText(info);
                        alert.showAndWait();
                    });
                }
            }
        } catch (IOException | InterruptedException e) {
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
