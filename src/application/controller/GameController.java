package application.controller;

import application.Client;
import java.net.URL;
import java.util.ResourceBundle;

import application.Constant;
import javafx.application.Platform;
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
  private Pane base_square;
  @FXML
  private Rectangle game_panel;
  private Client client;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    game_panel.setOnMouseClicked(event -> {
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
    base_square.getChildren().add(circle);
    circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
    circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
    circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
    circle.setStroke(Color.RED);
    circle.setFill(Color.TRANSPARENT);
//    flag[i][j] = true;
  }

  private void drawLine(int i, int j) {
    Line line_a = new Line();
    Line line_b = new Line();
    base_square.getChildren().add(line_a);
    base_square.getChildren().add(line_b);
    line_a.setStartX(i * BOUND + OFFSET * 1.5);
    line_a.setStartY(j * BOUND + OFFSET * 1.5);
    line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
    line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
    line_a.setStroke(Color.BLUE);

    line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
    line_b.setStartY(j * BOUND + OFFSET * 1.5);
    line_b.setEndX(i * BOUND + OFFSET * 1.5);
    line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
    line_b.setStroke(Color.BLUE);
//    flag[i][j] = true;
  }

  public void setApp(Client client) {
    this.client = client;
  }

  public void backButtonClick() {
    client.enterView(Constant.HOME_VIEW_FXML);
  }

}
