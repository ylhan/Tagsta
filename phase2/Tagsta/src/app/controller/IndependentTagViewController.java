package app.controller;

import app.Tagsta;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.*;

/** Controller for the independent tag view list  */
public class IndependentTagViewController {
  // Reference to list of tags
  @FXML private ListView<String> tagList;
  // Reference text field entry for adding tags
  @FXML private TextField tagListTextField;

  // Reference to main application
  private Tagsta main;

  /** Updates items of tagList to list of tags from TagManager */
  private void updateTagList() {
    tagList.setItems(main.getTagManager().getTagsList());
  }

  /** Add a tag to the list */
  @FXML
  private void addTag() {
    // Get the text from the text-field
    String tag = tagListTextField.getText();
    // Trim off whitespace
    tag = tag.trim();
    // Ensure there is currently and image and the text field has text
    if (!tag.equals("")) {
      boolean added = tag.matches("^[a-zA-Z0-9]*$");
      if (!added) {
        ExceptionDialogPopup.createExceptionPopup(
            "Error adding tag", "That tag contains an illegal character");
      }
      // Checks whether or not the the tag has already been added to the current iteration of tags
      if (main.getTagManager().getTagsList().contains(tag)) {
        ExceptionDialogPopup.createExceptionPopup(
            "Error adding tag", "Image already contains this tag!");
        added = false;
      }
      // If tag is valid, update list of tags
      if (added) {
        main.getTagManager().addIndependentTag(tag);
        updateTagList();
      }
    }
    // Clear the text from the text-field
    tagListTextField.clear();
  }

  //      @FXML
  //      private void handleClick(MouseEvent event) {
  //        if (event.getButton().equals(MouseButton.SECONDARY))
  //          if (event.getClickCount() == 2) {
  //              String tagItem = tagListView.getSelectionModel().getSelectedItem();
  //              tagView.getChildren().add(createTag(tagItem));
  //              imageManager.addTag(tagItem);
  //              // updateFileName(new TreeItem<>(imageManager.getFile()));
  //          }
  //      }

  @FXML
  private void handleDelete() {
    System.out.println(tagList.getSelectionModel().getSelectedItems());
    ObservableList<String> tags = tagList.getSelectionModel().getSelectedItems();
    for (String tag : tags) main.getTagManager().deleteIndependentTag(tag);
    updateTagList();
  }

  void setIndependentTagList(ObservableList<String> tagList) {
    this.tagList.setItems(tagList.sorted());
  }

  /**
   * Gives this controller a reference to the main application
   *
   * @param mainApp the main application
   */
  void setMainApp(Tagsta mainApp) {
    this.main = mainApp;
  }

  /** Initializes controller with appropriate settings for ListView tagList */
  @FXML
  private void initialize() {
    // Enables option to select multiple items
    tagList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    // Define the appropriate action when dragging items from the tagList
    tagList.setOnDragDetected(
        (MouseEvent event) -> {
          Dragboard db = tagList.startDragAndDrop(TransferMode.MOVE);
          ClipboardContent content = new ClipboardContent();
          // Put the selected items into the clipboard. Using putFilesByPath because it's the only
          // way to add a list of string easily
          content.putFilesByPath(tagList.getSelectionModel().getSelectedItems());
          db.setContent(content);
          event.consume();
        });
  }
}
