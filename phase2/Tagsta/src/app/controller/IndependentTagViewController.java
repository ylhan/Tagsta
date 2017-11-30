package app.controller;

import app.Tagsta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.xml.soap.Text;
import java.beans.EventHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class IndependentTagViewController {
  @FXML private ListView<String> tagList;
  @FXML private TextField tagListTextField;

  private Tagsta main;

  private void updateTagList() {
    ObservableList<String> uniqueTagList = FXCollections.observableArrayList(
            new HashSet<>(main.getTagManager().getTagsList()));
    tagList.setItems(uniqueTagList.sorted());
  }

    /** Add a tag to the image */
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
          ExceptionDialogPopup
                  .createExceptionPopup("Error adding tag", "That tag contains an illegal character");
        }
        //Checks whether or not the the tag has already been added to the current iteration of tags
        if (main.getTagManager().getTagsList().contains(tag)) {
          ExceptionDialogPopup
                  .createExceptionPopup("Error adding tag",
                          "Image already contains this tag!");
          added = false;
        }
        if (added) {
          main.getTagManager().addIndependentTag(tag);
          updateTagList();
        }
      }
      // Clear the text from the text-field
      tagListTextField.clear();
    }

  //
  //    private void addTagList(String tag) {
  //        if (!tagListView.getItems().contains(tag)) {
  //            ObservableList<String> tempList = tagListView.getItems();
  //            tempList.add(tag);
  //            tagListView.setItems(tempList.sorted());
  //        }
  //    }

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
    for (String tag : tags)
      main.getTagManager().deleteIndependentTag(tag);
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

  @FXML
  private void initialize() {
    tagList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
  }
}
