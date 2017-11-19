package app;

import app.model.ImageManager;
import app.model.TagManager;
import app.view.ImageOverviewController;
import app.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
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
  private ImageOverviewController imc;
  private TagManager tm;

  private final String DARK_THEME = Tagsta.class.getResource("view/materialDarkFX.css").toString();
  private final String LIGHT_THEME = Tagsta.class.getResource("view/materialLightFX.css").toString();

  @Override
  public void start(Stage primaryStage) throws Exception {
    // This will manage all the tags for each image
    tm = new TagManager();
    this.primaryStage = primaryStage;
    this.primaryStage.setTitle("Tagsta");
    this.primaryStage.setMaximized(true);

    // Initializes the root elements of the application (border pane, and menu bar)
    initRootLayout();
    // Initializes and adds the image view to the root UI
    showImageOverview();
  }

  /** Initializes the root layout (border pane, and menu bar) */
  public void initRootLayout() {
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
      RootLayoutController rls = loader.getController();
      rls.setMainApp(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Shows the Image overview inside the root layout. */
  public void showImageOverview() {
    try {
      // Load person overview.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("view/ImageOverview.fxml"));
      imageOverview = loader.load();
      // Set person overview into the center of root layout.
      rootLayout.setCenter(imageOverview);
      // Initialize the directory controller and give it a reference to this app
      imc = loader.getController();
      imc.setMainApp(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the theme to dark
   */
  public void setDarkTheme(){
    rootLayout.getStylesheets().clear();
    rootLayout.getStylesheets().add(DARK_THEME);
    imageOverview.getStylesheets().clear();
    imageOverview.getStylesheets().add(DARK_THEME);
  }

  /**
   * Sets the theme to light
   */
  public void setLightTheme(){
    rootLayout.getStylesheets().clear();
    rootLayout.getStylesheets().add(LIGHT_THEME);
    imageOverview.getStylesheets().clear();
    imageOverview.getStylesheets().add(LIGHT_THEME);
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
    imc.updateImage(im);
  }

  /**
   * Updates the directory view tree
   *
   * @param item the updated directory view
   */
  public void updateDirectoryView(TreeItem<File> item) {
    imc.updateDirectoryView(item);
  }

  /**
   * Updates the file view
   *
   * @param item the updated file view
   */
  public void updateFileView(TreeItem<File> item) {
    imc.updateFileView(item);
  }

  /** @return the tag manager for this program */
  public TagManager getTagManager() {
    return tm;
  }
}
