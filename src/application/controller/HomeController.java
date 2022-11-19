package application.controller;

import application.Client;
import application.Constant;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class HomeController implements Initializable, Controller {

  public AnchorPane home;
  public Button startButton;
  public Button settingButton;
  public Button logoutButton;
  public Button quitButton;
  public Text info;
  private Client client;

  public void setApp(Client client) {
    this.client = client;
    info.setText(String.format("WELCOME %s\nGROSS GAME:%d; WIN:%d; TIE:%d", client.userName,
        client.gross, client.win, client.tie));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    startButton.setPrefWidth(120);
    settingButton.setPrefWidth(120);
    logoutButton.setPrefWidth(120);
    quitButton.setPrefWidth(120);
  }

  public void startButtonClick() {
    client.sendCmd("start");
  }

  public void settingButtonClick() {

  }

  public void logoutButtonClick() {
    client.enterView(Constant.LOGIN_VIEW_FXML);
  }

  public void quitButtonClick() {
    client.close();
  }

}
