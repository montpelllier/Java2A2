package application.controller;

import application.Client;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class RegisterController implements Initializable {

  public Button conformButton;
  public Button backButton;
  private Client client;

  public void setApp(Client client) {
    this.client = client;
  }

  public void conformButtonClick() {
    //TODO
    client.enterHome();
  }

  public void backButtonClick(ActionEvent actionEvent) {
    client.enterLogin();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}
