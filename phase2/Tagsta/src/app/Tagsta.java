package app;

import app.controller.ExceptionDialogPopup;
import app.controller.ImageOverviewController;
import app.controller.MenuController;
import app.model.FileManager;
import app.model.ImageManager;
import app.model.TagManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/** An Image Tagging application */
public class Tagsta extends Application {

  private Stage primaryStage;
  private MenuBar menuBar;
  private BorderPane rootLayout;
  private AnchorPane imageOverview;
  private ImageOverviewController imageOverviewController;
  private MenuController menuController;
  private TagManager tagManager;

  private final String DARK_THEME =
      Tagsta.class.getResource("/resources/materialDarkFX.css").toString();
  private final String LIGHT_THEME =
      Tagsta.class.getResource("/resources/materialLightFX.css").toString();
  private static final Image ICON =
      new Image(Tagsta.class.getResourceAsStream("/resources/icon.png"));

  @Override
  public void start(Stage primaryStage) throws Exception {
    // This will manage all the tags for each image
    tagManager = new TagManager();
    this.primaryStage = primaryStage;
    this.primaryStage.setTitle("Tagsta");
    this.primaryStage.setMaximized(true);
    this.primaryStage.getIcons().add(ICON);
    rootLayout = new BorderPane();
    // Show the scene containing the root layout.
    Scene scene = new Scene(rootLayout);
    primaryStage.setScene(scene);
    primaryStage.setMinWidth(900);
    primaryStage.setMinHeight(650);
    primaryStage.show();
    // Initializes the loadMenuBar bar of the application
    loadMenuBar();
    // Initializes and adds the image view to the root UI
    loadImageOverview();
    primaryStage.setOnCloseRequest(t -> FileManager.closeLogHandler());
  }

  /** Initializes the root layout (border pane, and loadMenuBar bar) */
  private void loadMenuBar() {
    try {
      // Load root layout from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("view/Menu.fxml"));
      menuBar = loader.load();
      rootLayout.setTop(menuBar);
      // Initialize the root controller and give it a reference to this app
      menuController = loader.getController();
      menuController.setMainApp(this);
    } catch (IOException e) {
      ExceptionDialogPopup.createExceptionPopup("The layout of the program could not be made",
              "The program could not be ran");
    }
  }

  /** Shows the Image overview inside the root layout. */
  private void loadImageOverview() {
    try {
      // Load person overview.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("view/ImageOverview.fxml"));
      imageOverview = loader.load();
      // Set person overview into the center of root layout.
      rootLayout.setCenter(imageOverview);
      // Initialize the directory controller and give it a reference to this app
      imageOverviewController = loader.getController();
      imageOverviewController.setMainApp(this);
      // Load in the previous theme
      menuController.loadTheme(tagManager.getConfigOption("THEME"));
      // Load the previous session
      menuController.loadLastSession();
    } catch (IOException e) {
      ExceptionDialogPopup.createExceptionPopup("The layout of the program could not be made",
              "The program could not be ran");
    }
  }

  /** Sets the theme to dark */
  public void setDarkTheme() {
    rootLayout.getStylesheets().clear();
    rootLayout.getStylesheets().add(DARK_THEME);
    imageOverview.getStylesheets().clear();
    imageOverview.getStylesheets().add(DARK_THEME);
    tagManager.setConfigOption("THEME", "dark");
  }

  /** Sets the theme to light */
  public void setLightTheme() {
    rootLayout.getStylesheets().clear();
    rootLayout.getStylesheets().add(LIGHT_THEME);
    imageOverview.getStylesheets().clear();
    imageOverview.getStylesheets().add(LIGHT_THEME);
    // Store the theme in the configs
    tagManager.setConfigOption("THEME", "light");
  }

  public static void main(String[] args) {
    launch(args);
  }

  /** @return the stage for this application */
  public Stage getPrimaryStage() {
    return primaryStage;
  }

  /**
   * Updates the directory view images
   *
   * @param im the updated image
   */
  public void updateImage(ImageManager im) {
    imageOverviewController.updateImage(im);
  }

  /**
   * Updates the directory view tree
   *
   * @param item the updated directory view
   */
  public void updateDirectoryView(TreeItem<File> item) {
    imageOverviewController.updateDirectoryView(item);
  }

  /**
   * Updates the file view
   *
   * @param item the updated file view
   */
  public void updateFileView(TreeItem<File> item) {
    imageOverviewController.updateFileView(item);
  }

  /** @return the tag manager for this program */
  public TagManager getTagManager() {
    return tagManager;
  }

  /** @return the image overview controller for this application */
  public ImageOverviewController getImageOverviewController() {
    return imageOverviewController;
  }

  /** @return the root layout controller for this application */
  public MenuController getMenuController() {
    return menuController;
  }

  /** @return this program's icon */
  public static Image getIcon() {
    return ICON;
  }
}
