package application.controller;

import application.Client;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import application.resource.Constant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;


public class HomeController implements Initializable {

  private static final Logger logger = Logger.getLogger(HomeController.class.getName());
  public AnchorPane home;
  public Button startButton;
  public Button settingButton;
  public Button logoutButton;
  public Button quitButton;
  public Text info;
  private Client client;

  @FXML
  private TreeView<String> main_treeview;

  @FXML
  private ScrollPane main_scroll_pane;

  @FXML
  private AnchorPane main_pane_under_scroll;

  public void setApp(Client client) {
    this.client = client;
    info.setText("WELCOME " + client.userName);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
//    setTreeView();
    startButton.setPrefWidth(120);
    settingButton.setPrefWidth(120);
    logoutButton.setPrefWidth(120);
    quitButton.setPrefWidth(120);
  }

  public void startButtonClick() {
    //
    client.enterGamePage();
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
