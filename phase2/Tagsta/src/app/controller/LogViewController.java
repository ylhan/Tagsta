package app.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;

/** Controller class for the log view window */
public class LogViewController {

  @FXML private ListView<String> log;
  private Stage stage;
  /**
   * Set the logs to be displayed
   *
   * @param log the logs to be displayed
   */
  void setLog(ObservableList<String> log) {
    this.log.setItems(log);
    this.log.refresh();
  }

  /**
   * Handles double-clicking of a log item. When a log item is double clicked it is copied.
   *
   * @param event the user's click
   */
  @FXML
  private void handleClick(MouseEvent event) {
    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
      // Get the user's selected item and then place it in the clipboard
      String selectedLogItem = log.getSelectionModel().getSelectedItems().get(0);
      if (selectedLogItem != null) {
        ClipboardContent content = new ClipboardContent();
        content.putString(selectedLogItem);
        Clipboard.getSystemClipboard().setContent(content);
        // Popup a notice telling the user that the item was copied
        popupCopiedNotice(event);
      }
    }
  }

  /**
   * Pops up a notice informing the user that a log item was copied
   * https://www.programcreek.com/java-api-examples/index.php?api=javafx.stage.Popup
   * @param event the event that selected the log item
   */
  private void popupCopiedNotice(MouseEvent event){
    Popup popup = new Popup();
    Label popupLabel = new Label("Copied to Clipboard");
    popupLabel.setStyle("-fx-background-color: white;");
    popup.setAutoHide(true);
    popup.setAutoFix(true);
    Node eventSource = (Node) event.getSource();
    Bounds sourceNodeBounds = eventSource.localToScreen(eventSource.getBoundsInLocal());
    popup.setX(sourceNodeBounds.getMinX() - 5.0);
    popup.setY(sourceNodeBounds.getMaxY() + 5.0);
    popup.getContent().addAll(popupLabel);
    popup.show(stage);
  }

  /**
   * Sets the stage that this window was created on
   * @param stage the stage for this window
   */
  void setStage(Stage stage) {
    this.stage = stage;
  }
}
