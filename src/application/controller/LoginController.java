package application.controller;

import application.Client;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.resource.Constant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Javadoc.
 */
public class LoginController implements Initializable {

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

  /**
   * Javadoc.
   */
  public void loginButtonClick() {
    logger.log(Level.INFO, "输入用户名为：" + loginUsername.getText());
    logger.log(Level.INFO, "输入密码为：" + loginPassword.getText());
    //TODO: connect with server
//    if ("admin".equalsIgnoreCase(loginUsername.getText())
//        && "123456".equalsIgnoreCase(loginPassword.getText())) {
    logger.log(Level.INFO, "登录成功！");
    client.userName = loginUsername.getText();
    client.enterView( Constant.HOME_VIEW_FXML);
//    } else {
//
//      logger.log(Level.WARNING, "用户名或密码错误！");
//    }
  }

  public void registerButtonClick() {
    client.enterView(Constant.REGISTER_VIEW_FXML);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void setApp(Client client) {
    this.client = client;
  }
}
