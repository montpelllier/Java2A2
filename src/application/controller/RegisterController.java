package application.controller;

import application.Client;
import java.net.URL;
import java.util.ResourceBundle;

import application.Constant;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class RegisterController implements Initializable, Controller {

  public Button conformButton;
  public Button backButton;
  private Client client;

  public void setApp(Client client) {
    this.client = client;
  }

  public void conformButtonClick() {
    //TODO
    client.enterView( Constant.HOME_VIEW_FXML);
  }

  public void backButtonClick(ActionEvent actionEvent) {
    client.enterView(Constant.LOGIN_VIEW_FXML);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}
