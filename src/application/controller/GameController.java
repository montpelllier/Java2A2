package application.controller;

import application.Client;
import application.Constant;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GameController implements Initializable, Controller {

  private static final int EMPTY = 0;
  private static final int BOUND = 90;
  private static final int OFFSET = 15;

  public Button backButton;
  @FXML
  private Pane baseSquare;
  @FXML
  private Rectangle gamePanel;
  private Client client;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    gamePanel.setOnMouseClicked(event -> {
      int x = (int) (event.getX() / BOUND);
      int y = (int) (event.getY() / BOUND);
      if (refreshBoard(x, y)) {
        client.sendCmd(String.format("move:%d,%d", x, y));
        client.isMyTurn = false;
      }
    });
  }

  private boolean refreshBoard(int x, int y) {
    if (client.chessBoard[x][y] == EMPTY && client.isMyTurn) {
      client.chessBoard[x][y] = client.hand;
      drawChess();
      return true;
    }
    return false;
  }

  public void drawChess() {
    for (int i = 0; i < client.chessBoard.length; i++) {
      for (int j = 0; j < client.chessBoard[0].length; j++) {
        switch (client.chessBoard[i][j]) {
          case 1:
            drawCircle(i, j);
            break;
          case -1:
            drawLine(i, j);
            break;
          case EMPTY:
            // do nothing
            break;
          default:
            System.err.println("Invalid value!");
        }
      }
    }
  }

  private void drawCircle(int i, int j) {
    Circle circle = new Circle();
    baseSquare.getChildren().add(circle);
    circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
    circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
    circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
    circle.setStroke(Color.RED);
    circle.setFill(Color.TRANSPARENT);
  }

  private void drawLine(int i, int j) {
    Line lineA = new Line();
    Line lineB = new Line();
    baseSquare.getChildren().add(lineA);
    baseSquare.getChildren().add(lineB);
    lineA.setStartX(i * BOUND + OFFSET * 1.5);
    lineA.setStartY(j * BOUND + OFFSET * 1.5);
    lineA.setEndX((i + 1) * BOUND + OFFSET * 0.5);
    lineA.setEndY((j + 1) * BOUND + OFFSET * 0.5);
    lineA.setStroke(Color.BLUE);

    lineB.setStartX((i + 1) * BOUND + OFFSET * 0.5);
    lineB.setStartY(j * BOUND + OFFSET * 1.5);
    lineB.setEndX(i * BOUND + OFFSET * 1.5);
    lineB.setEndY((j + 1) * BOUND + OFFSET * 0.5);
    lineB.setStroke(Color.BLUE);
  }

  public void setApp(Client client) {
    this.client = client;
  }

  public void backButtonClick() {
    client.enterView(Constant.HOME_VIEW_FXML);
  }

}
