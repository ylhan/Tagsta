package app.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class RevertNameViewController {

  @FXML
  private ListView<String> prevTags;

  private ImageOverviewController ioc;

  /**
   * Sets ImageOverviewController associated to this object
   */
  void setImageOverviewController(ImageOverviewController ioc) {
    this.ioc = ioc;
  }

  /**
   * Sets list of previous tags associated to this object's ImageManager
   */
  void setPrevTags(ObservableList<String> pt) {
    prevTags.setItems(pt);
    prevTags.refresh();
  }

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
    int nameIndex = ioc.getImageManager().getPrevNames().indexOf(listItem);
    if (ioc.getImageManager().getTags().equals(FXCollections
        .observableArrayList(ioc.getImageManager().getPreviousTags().get(nameIndex)))) {
      ExceptionDialogPopup.createExceptionPopup(
          "Error reverting tags", "Image already has this name!");
    } else {
      ioc.revert(listItem);
    }
  }
}
