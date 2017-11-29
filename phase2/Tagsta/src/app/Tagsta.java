package app;

import app.controller.ImageOverviewController;
import app.controller.RootLayoutController;
import app.model.ImageManager;
import app.model.TagManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
  private BorderPane rootLayout;
  private AnchorPane imageOverview;
  private ImageOverviewController imageOverviewController;
  private RootLayoutController rootLayoutController;
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

    // Initializes the root elements of the application (border pane, and menu bar)
    initRootLayout();
    // Initializes and adds the image view to the root UI
    showImageOverview();
    primaryStage.setOnCloseRequest(t -> Platform.exit());
  }

  /** Initializes the root layout (border pane, and menu bar) */
  private void initRootLayout() {
    try {
      // Load root layout from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
      rootLayout = loader.load();

      // Show the scene containing the root layout.
      Scene scene = new Scene(rootLayout);
      primaryStage.setScene(scene);
      primaryStage.setMinWidth(900);
      primaryStage.setMinHeight(650);
      primaryStage.show();

      // Initialize the root controller and give it a reference to this app
      rootLayoutController = loader.getController();
      rootLayoutController.setMainApp(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Shows the Image overview inside the root layout. */
  private void showImageOverview() {
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
      rootLayoutController.loadTheme(tagManager.getConfigOption("THEME"));
      // Load the previous session
      rootLayoutController.loadLastSession();
    } catch (IOException e) {
      e.printStackTrace();
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
  public RootLayoutController getRootLayoutController() {
    return rootLayoutController;
  }

  /** @return this program's icon */
  public static Image getIcon() {
    return ICON;
  }
}
