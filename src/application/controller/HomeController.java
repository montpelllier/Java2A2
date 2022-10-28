package application.controller;

import application.Client;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    client.enterLogin();
  }

  public void quitButtonClick() {
    client.close();
  }

  /**
   * 设置TreeView
   */
  /*
  @SuppressWarnings("unchecked")
  public void setTreeView() {
    TreeItem<String> root = new TreeItem<>(StaticResourcesConfig.MAIN_TREE_HEADER);
    root.setExpanded(true);
    root.getChildren().addAll(new TreeItem<>(StaticResourcesConfig.MAIN_TREE_HEADER_ITEM1),
        new TreeItem<>(StaticResourcesConfig.MAIN_TREE_HEADER_ITEM2),
        new TreeItem<>(StaticResourcesConfig.MAIN_TREE_HEADER_ITEM3),
        new TreeItem<>(StaticResourcesConfig.MAIN_TREE_HEADER_ITEM4),
        new TreeItem<>(StaticResourcesConfig.MAIN_TREE_HEADER_ITEM5));
    main_treeview.setRoot(root);
  }

  /**
   * TreeView 点击事件
   *
   * @throws IOException
   */
  /*
  public void mainTreeViewClick() throws IOException {
    logger.log(Level.INFO, "点击TreeView");
    // 获取鼠标当前点击的Item
    TreeItem<String> selectedItem = main_treeview.getSelectionModel().getSelectedItem();
    logger.log(Level.INFO, selectedItem.getValue());

    String pagePath = "";
//    switch (selectedItem.getValue()) {
//      case StaticResourcesConfig.MAIN_TREE_HEADER:
//        pagePath = StaticResourcesConfig.DEFAULT_VIEW_PATH;
//        break;
//      case StaticResourcesConfig.MAIN_TREE_HEADER_ITEM1:
//        pagePath = StaticResourcesConfig.NOTE_VIEW_PATH;
//        break;
//      case StaticResourcesConfig.MAIN_TREE_HEADER_ITEM2:
//        pagePath = StaticResourcesConfig.CLIP_VIEW_PATH;
//        break;
//      case StaticResourcesConfig.MAIN_TREE_HEADER_ITEM3:
//        pagePath = StaticResourcesConfig.USER_VIEW_PATH;
//        break;
//      case StaticResourcesConfig.MAIN_TREE_HEADER_ITEM4:
//        pagePath = StaticResourcesConfig.DATA_VIEW_PATH;
//        break;
//      case StaticResourcesConfig.MAIN_TREE_HEADER_ITEM5:
//        pagePath = StaticResourcesConfig.LANGUAGE_VIEW_PATH;
//        break;
//    }

    skipView(pagePath);
  }


//  /**
//   * 改变右侧scroll的界面
//   *
//   * @param pagePath
//   * @throws IOException
//   */
//  private void skipView(String pagePath) throws IOException {
//    logger.info("显示剪切板界面");
//    ObservableList<Node> scrollChildren = main_pane_under_scroll.getChildren();
//    scrollChildren.clear();
//    scrollChildren.add(FXMLLoader.load(getClass().getResource(pagePath)));
//  }
}
