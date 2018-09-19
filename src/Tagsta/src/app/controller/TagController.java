package app.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/** Controller for a Tag */
public class TagController {

  @FXML private Label tag;

  @FXML private Label deleteTag;

  /**
   * Sets the tag to be displayed in the tag view
   *
   * @param tagString the string (tag) to be displayed
   */
  void setTag(String tagString) {
    tag.setText(tagString);
  }

  /**
   * Handles the deletion of the tag.
   *
   * @param action the mouse event that triggers the deletion of the tag
   */
  void handleDeleteTag(EventHandler<? super MouseEvent> action) {
    deleteTag.setOnMouseClicked(action);
  }
}
