package app.controller;

import app.Tagsta;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.List;

/** Controller for the independent tag view list */
public class IndependentTagViewController {
  // Reference to list of tags
  @FXML private ListView<String> tagList;
  // Reference text field entry for adding tags
  @FXML private TextField tagListTextField;

  // Reference to main application
  private Tagsta main;

  // Reference to tag view controller of currently image
  private TagViewController tagViewController;

  /** Add a tag to the list */
  @FXML
  private void addTag() {
    // Get the text from the text-field
    String tag = tagListTextField.getText();

        main.getTagManager().addIndependentTag(tag);
        setIndependentTagList(main.getTagManager().getTagsList());
 // Clear the text from the text-field
    tagListTextField.clear();
  }

  /**
   * Handles clicking of tagsList items and attempts to add a tag if double-clicked
   *
   * @param event the mouse click
   */
  @FXML
  private void handleClick(MouseEvent event) {
    if (this.tagViewController.getImageManager() != null && event.getButton().equals(MouseButton.PRIMARY))
      if (event.getClickCount() == 2) {
        List<String> tagItem = new ArrayList<>();
        tagItem.add(tagList.getSelectionModel().getSelectedItem());
        this.tagViewController.addTags(tagItem);
      }
  }

  /**
   * Handles key presses when tagsList items are selected and attempts to add one or more tags if
   * ENTER is pressed
   *
   * @param keyPressed the key pressed on the keyboard
   */
  @FXML
  private void handleKeyEnter(KeyEvent keyPressed) {
    if (this.tagViewController.getImageManager() != null && keyPressed.getCode().equals(KeyCode.ENTER)) {
      List<String> tagItem = new ArrayList<>();
      tagItem.addAll(tagList.getSelectionModel().getSelectedItems());
      this.tagViewController.addTags(tagItem);
      tagList.getSelectionModel().clearSelection();
    }
  }

  /** Deletes tags from tagsList */
  @FXML
  private void handleDelete() {
    ObservableList<String> tags = tagList.getSelectionModel().getSelectedItems();
    for (String tag : tags) main.getTagManager().deleteIndependentTag(tag);
    setIndependentTagList(main.getTagManager().getTagsList());
  }

  /** Sets tagList with listOfTags */
  void setIndependentTagList(ObservableList<String> listOfTags) {
    this.tagList.setItems(listOfTags.sorted());
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

  /** Sets access to a tag view controller */
  void setTagViewController(TagViewController tagViewController) {
    this.tagViewController = tagViewController;
  }
}
