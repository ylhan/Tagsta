package app.controller;

import app.model.ImageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/** Controller for the revert name window */
public class RevertNameViewController {

  @FXML private ListView<String> prevTags;

  private TagViewController tagViewController;
  private ImageManager imageManager;

  /**
   * Handles double-clicking of old name items to revert name of ImageManager or popup error if
   * reverting to the same name as the current name
   *
   * @param event the user's click
   */
  @FXML
  private void handleClick(MouseEvent event) {
    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
      if (prevTags.getSelectionModel().getSelectedItem() != null) {
        tryRevert(prevTags.getSelectionModel().getSelectedItem());
      }
    }
  }

  /**
   * Revert name currently selected in the ListView when the user presses Enter
   *
   * @param keyPressed the key that was pressed
   */
  @FXML
  private void handleKeyEnter(KeyEvent keyPressed) {
    if (keyPressed.getCode().equals(KeyCode.ENTER)) {
      tryRevert(prevTags.getSelectionModel().getSelectedItem());
    }
  }

  /**
   * Helper method that attempts to revert name based on item selected in ListView of old names
   *
   * @param listItem selected old name
   */
  private void tryRevert(String listItem) {
    int nameIndex = imageManager.getPrevNames().indexOf(listItem);
    if (imageManager
        .getTags()
        .equals(FXCollections.observableArrayList(imageManager.getPreviousTags().get(nameIndex)))) {
      ExceptionDialogPopup.createExceptionPopup(
          "Error reverting tags", "Image already has this name!");
    } else {
      tagViewController.revert(listItem);
    }
  }

  /** Sets TagViewController associated to this object */
  void setTagViewController(TagViewController tvc) {
    this.tagViewController = tvc;
  }

  /**
   * Sets the image manager (holds the current image and it's meta data
   *
   * @param im the image manager to be set
   */
  void setImageManager(ImageManager im) {
    this.imageManager = im;
  }

  /** Sets list of previous tags associated to this object's ImageManager */
  void setPrevTags(ObservableList<String> pt) {
    prevTags.setItems(pt);
    prevTags.refresh();
  }
}
