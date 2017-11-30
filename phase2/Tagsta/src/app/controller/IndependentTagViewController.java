package app.controller;

import app.Tagsta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.HashSet;

public class IndependentTagViewController {
  @FXML private ListView<String> tagList;
  private Tagsta main;

      private void updateTagList() {
          ObservableList<String> uniqueTagList = FXCollections.observableArrayList(
                  new HashSet<>(main.getTagManager().getTagsList()));
          tagList.setItems(uniqueTagList.sorted());
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
        ObservableList<String> tags = tagList.getSelectionModel().getSelectedItems();
        for (String tag: tags) {
          main.getTagManager().deleteIndependentTag(tag);
          updateTagList();
        }
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
  }}
