package application.controller;

import application.Client;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Javadoc.
 */
public class LoginController implements Initializable, Controller {

  private static final Logger logger = Logger.getLogger(LoginController.class.getName());
  public AnchorPane login;
  public Button registerButton;
  @FXML
  private Button loginButton;
  @FXML
  private TextField loginUsername;
  @FXML
  private TextField loginPassword;
  private Client client;

  public void loginButtonClick() {
    String account = loginUsername.getText();
    String psw = loginPassword.getText();
    logger.log(Level.INFO, "USER NAME: " + account + "; PASSWORD: " + psw);
    if (account.equals("") || psw.equals("")) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Warning");
      alert.setHeaderText(null);
      alert.setContentText("EMPTY USERNAME OR PASSWORD!");
      alert.showAndWait();
    } else {
      client.sendCmd(String.format("login:%s,%s", account, psw));
    }
  }

  public void registerButtonClick() {
    String account = loginUsername.getText();
    String psw = loginPassword.getText();
    client.sendCmd(String.format("reg:%s,%s", account, psw));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void setApp(Client client) {
    this.client = client;
  }
}
