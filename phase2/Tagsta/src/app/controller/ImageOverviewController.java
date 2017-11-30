package app.controller;

import app.Tagsta;
import app.model.ImageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;

/** Controller for the image overview (contains the directory view, tag view, and image view). */
public class ImageOverviewController {

  // Anchors to which we can attach the UI components
  @FXML private AnchorPane imageViewAnchor;
  @FXML private AnchorPane tagViewAnchor;
  @FXML private AnchorPane directoryViewAnchor;

  // Reference to the main application
  private Tagsta main;

  // A wrapper for the image and it's metadata (tag history, previous names, etc)
  private ImageManager imageManager;

  // Controllers for the UI components
  private ImageViewController imageViewController;
  private TagViewController tagViewController;
  private DirectoryViewController directoryViewController;

  /**
   * Used to enable button/text-fields that are disabled by default (only enable when there is an
   * image)
   */
  private void enableImageControls() {
    tagViewController.enableControls();
    imageViewController.enableControls();
    // Enable the menu items that are disabled by default
    main.getRootLayoutController().enableMenuItems();
  }

  /** Loads the image view of the program */
  private void loadImageView() {
    try {
      // Load image view from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/app/view/ImageView.fxml"));
      AnchorPane iv = loader.load();

      // Make the image view component anchor to the size of the pane
      AnchorPane.setBottomAnchor(iv, 0.0);
      AnchorPane.setTopAnchor(iv, 0.0);
      AnchorPane.setRightAnchor(iv, 0.0);
      AnchorPane.setLeftAnchor(iv, 0.0);

      // Attach the image view to it's anchor
      imageViewAnchor.getChildren().add(iv);

      // Load the controller
      imageViewController = loader.getController();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Loads the tag view of the program */
  private void loadTagView() {
    try {
      // Load tag view from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/app/view/TagView.fxml"));
      BorderPane tv = loader.load();

      // Make the tag view component anchor to the size of the pane
      AnchorPane.setBottomAnchor(tv, 0.0);
      AnchorPane.setTopAnchor(tv, 0.0);
      AnchorPane.setRightAnchor(tv, 0.0);
      AnchorPane.setLeftAnchor(tv, 0.0);

      // Attach the tag view to it's anchor
      tagViewAnchor.getChildren().add(tv);

      // Load the controller
      tagViewController = loader.getController();

      // Give the tag view's controller and reference to the directory view
      tagViewController.setDirectoryViewController(directoryViewController);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Loads the directory view of the program */
  private void loadDirectoryView() {
    try {
      // Load directory view from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/app/view/DirectoryView.fxml"));
      GridPane dv = loader.load();

      // Make the directory view component anchor to the size of the pane
      AnchorPane.setBottomAnchor(dv, 0.0);
      AnchorPane.setTopAnchor(dv, 0.0);
      AnchorPane.setRightAnchor(dv, 0.0);
      AnchorPane.setLeftAnchor(dv, 0.0);

      // Attach the directory view to the overview
      directoryViewAnchor.getChildren().add(dv);

      // Load the controller
      directoryViewController = loader.getController();

      // Give the directory view controller a reference to this controller
      directoryViewController.setImageOverviewController(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Shows the given image (held by the image manager)
   *
   * @param im the image manager that holds the image and it's metadata
   */
  public void updateImage(ImageManager im) {
    // Set the currently opened image manager
    this.imageManager = im;

    // Show the image in the image view
    imageViewController.updateImage(im.getImage());

    // Update the tag view with the new tags of this image
    tagViewController.newTagView(im);

    // Since there is an image now we can enable the buttons (e.g. zoom in & out, show log, revert
    // history)
    enableImageControls();
  }

  /**
   * Shows/updates the title of the program to show the absolute path of the currently opened image
   */
  void updateTitle(){
    // Show the file's absolute path at the top of the window
    main.getPrimaryStage().setTitle("Tagsta " + getImageManager().getFile().getAbsolutePath());
  }

  /** Initializes the image overview controller and it's components */
  @FXML
  private void initialize() {
    loadImageView();
    loadDirectoryView();
    loadTagView();
  }

  /**
   * Updates the directory view tree
   *
   * @param item the updated directory view
   */
  public void updateDirectoryView(TreeItem<File> item) {
    directoryViewController.updateDirectoryView(item);
    tagViewController.setIndependentTagView(main.getTagManager().getTagsList());
  }

  /**
   * Updates the file view
   *
   * @param item the updated file view
   */
  public void updateFileView(TreeItem<File> item) {
    directoryViewController.updateFileView(item);
  }

  /** @return the current image manager */
  ImageManager getImageManager() {
    return imageManager;
  }

  /**
   * Give image overview a reference to the main application
   *
   * @param mainApp the main application
   */
  public void setMainApp(Tagsta mainApp) {
    this.main = mainApp;
    // Give the controllers a reference to the main application as well
    tagViewController.setMainApp(main);
    directoryViewController.setTagManager(main.getTagManager());
    tagViewController.setIndependentTagView(main.getTagManager().getTagsList());
  }
}
