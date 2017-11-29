package app.controller;

import app.Tagsta;
import app.model.ImageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/** Controller for the tag view at the bottom right of the overview */
public class TagViewController {
  @FXML private Button addTag;
  @FXML private Button revertName;
  @FXML private FlowPane tagView;
  @FXML private TextField addTagTextField;

  @FXML private ListView<String> tagListView;


  private Stage revertWindow;
  private Tagsta main;
  private ImageManager imageManager;
  private DirectoryViewController directoryViewController;

  public void updateTagList() {
    ObservableList<String> uniqueTagList = FXCollections.observableArrayList(
            new HashSet<>(main.getTagManager().getTagsList()));
    tagListView.setItems(uniqueTagList.sorted());
  }

  private void addTagList(String tag) {
    if (!tagListView.getItems().contains(tag)) {
      ObservableList<String> tempList = tagListView.getItems();
      tempList.add(tag);
      tagListView.setItems(tempList.sorted());
    }
  }

  @FXML
  private void handleTagListClick(MouseEvent event) {
    if (event.getClickCount() == 2) {
      String tagItem = tagListView.getSelectionModel().getSelectedItem();
      tagView.getChildren().add(createTag(tagItem));
      imageManager.addTag(tagItem);
      // updateFileName(new TreeItem<>(imageManager.getFile()));
    }
  }

  /**
   * Loads new tags into the tag view given the image manager of the image
   *
   * @param im the image manager which contains the tags
   */
  void newTagView(ImageManager im) {
    this.imageManager = im;
    tagView.getChildren().clear();
    // Loop through the tag string list to create UI tags and then add them to the tag list
    for (String tag : im.getTags()) {
      tagView.getChildren().add(createTag(tag));
    }
  }

  /**
   * Add the tag currently in the text-field when the user presses Enter
   *
   * @param keyPressed the key that was pressed
   */
  @FXML
  private void addTagOnEnter(KeyEvent keyPressed) {
    if (keyPressed.getCode().equals(KeyCode.ENTER)) {
      addTag();
    }
  }

  /**
   * Creates a tag element with the given String. A "tag" is really a styled HBox with labels
   * inside.
   *
   * @param tagString the string the tag will display
   * @return the new tag
   */
  private HBox createTag(String tagString) {
    try {
      // Load tag from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/app/view/Tag.fxml"));
      HBox tag = loader.load();

      // Get the controller for the tag
      TagController t = loader.getController();
      // Set the string for the tag
      t.setTag(tagString);
      // Define what happens when the tag is closed
      t.handleDeleteTag(event -> removeTag(tag, tagString));
      return tag;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** Add a tag to the image */
  @FXML
  private void addTag() {
    // Ensure there is currently and image and the text field has text
    if (imageManager != null && !addTagTextField.getText().equals("")) {
      File oldFile = imageManager.getFile();
      // Get the text from the text-field
      String tag = addTagTextField.getText();
      // Trim off whitespace
      tag = tag.trim();
      // Add the tag to the image manager and reload the tags in the tag view
      imageManager.addTag(tag);
      newTagView(imageManager);
      // Clear the text from the text-field
      addTagTextField.clear();
      // Update the directory and file view and save the tags
      directoryViewController.updateFileName(oldFile, imageManager.getFile());
    }
  }

  /**
   * Removes a tag from the tag view
   *
   * @param tag the tag to be removed
   * @param tagString the string of the tag
   */
  private void removeTag(HBox tag, String tagString) {
    File oldFile = imageManager.getFile();
    // Remove the tag from the tag view
    tagView.getChildren().remove(tag);
    // Remove the tag from the image's manager
    imageManager.removeTag(tagString);

    // Update view of the file and save it
    directoryViewController.updateFileName(oldFile, imageManager.getFile());
  }

  /** Enables the add tag, revert name, and text field (disabled by default) */
  void enableControls() {
    // Enable all controls
    addTag.setDisable(false);
    revertName.setDisable(false);
    addTagTextField.setDisable(false);
  }

  /**
   * Handles clicking revertName Button by opening window that is controlled by
   * RevertNameViewController
   */
  @FXML
  private void handleRevertName() {
    if (imageManager != null) {
      try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/app/view/RevertNameView.fxml"));
        BorderPane revertNamePane = loader.load();

        revertWindow = new Stage();
        revertWindow.setTitle("History of names");
        revertWindow.setScene(new Scene(revertNamePane, 800, 600));
        revertWindow.getIcons().add(Tagsta.getIcon());
        // Set the focus on this stage
        revertWindow.initOwner(main.getPrimaryStage());
        revertWindow.initModality(Modality.WINDOW_MODAL);
        revertWindow.show();

        // Set the previous tags for the list
        RevertNameViewController revertNameViewController = loader.getController();
        revertNameViewController.setPrevTags(imageManager.getPrevNames());
        // Give the controller references to the tag view and the image manager
        revertNameViewController.setTagViewController(this);
        revertNameViewController.setImageManager(imageManager);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /** Reverts name of ImageManager and updates TagView */
  void revert(String oldName) {
    File oldFile = imageManager.getFile();
    imageManager.revert(oldName);
    newTagView(imageManager);
    // Updates the directory and file view
    directoryViewController.updateFileName(oldFile, imageManager.getFile());
    revertWindow.close();
  }

  /**
   * Gives this controller a reference to the main application
   *
   * @param mainApp the main application
   */
  void setMainApp(Tagsta mainApp) {
    this.main = mainApp;
  }

  /**
   * Gives this controller a reference to the directory view controller
   *
   * @param dvc the directory view's controller
   */
  void setDirectoryViewController(DirectoryViewController dvc) {
    this.directoryViewController = dvc;
  }
}
