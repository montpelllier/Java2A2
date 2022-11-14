package application;

import application.controller.Controller;
import javafx.application.Application;
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
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Application {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    public String userName;
    public int gross;
    public int win;
    //first = 1, second = 2
    public int hand;
    public boolean isMyTurn = false;
    private Stage stage;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    public Client() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        stage.setTitle("Tic Tac Toe");
        enterView(Constant.LOGIN_VIEW_FXML);
        stage.show();
        stage.setResizable(false);


        try {
            socket = new Socket("localhost", Constant.PORT);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            in = new Scanner(inputStream);
            out = new PrintWriter(outputStream);

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
        out.println(cmd);
        out.flush();
    }

    /**
     * 替换场景
     *
     * @param fxml
     * @return
     */
    private Initializable replaceSceneContent(String fxml) {
        FXMLLoader loader = new FXMLLoader();
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
        while (true) {
            if (!in.hasNext()) {
                return;
            }
            String receive = in.nextLine();
            System.out.println("from server: "+receive);

            if (receive.equals("waiting")) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //todo: waiting window
            } else if (receive.startsWith("game start:")) {
                hand = Character.getNumericValue(receive.charAt(11));
                isMyTurn = hand == 1;
                System.out.println(hand);
            } else if (receive.startsWith("oppo:")) {
                String pos = receive.substring(5);

            }

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
