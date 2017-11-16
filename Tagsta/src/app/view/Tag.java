package app.view;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class Tag {
  @FXML private Label tag;

  @FXML private Label deleteTag;

  public void setTag(String tagString) {
    tag.setText(tagString);
  }

  public void handleDeleteTag(EventHandler<? super MouseEvent> action) {
      deleteTag.setOnMouseClicked(action);
  }
}
