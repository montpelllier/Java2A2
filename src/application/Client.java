package application;

import application.controller.Controller;
import application.resource.Constant;
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
            socket = new Socket("localhost", 8888);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            in = new Scanner(inputStream);
            out = new PrintWriter(outputStream);

//      String cmd = "test";
//      System.out.println(cmd);
//      out.println("send: "+cmd);
//      out.flush();
//
//      String response = in.nextLine();
//      System.out.println("get: "+response);


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
     * @throws Exception
     */
    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
//    InputStream in = Client.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(getClass().getClassLoader().getResource(fxml));
        try {
//      AnchorPane page = (AnchorPane) loader.load(in);

            Pane page = loader.load();
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "页面加载异常！" + e);
        }

        return loader.getController();
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
