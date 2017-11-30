package app.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/** Controller class for the log view window */
public class LogViewController {

  @FXML private ListView<String> log;

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
      }
    }
  }
}
