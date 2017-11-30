package app.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class IndependentTagViewController {
  @FXML private ListView<String> tagList;

  //    public void updateTagList() {
  //        ObservableList<String> uniqueTagList = FXCollections.observableArrayList(
  //                new HashSet<>(main.getTagManager().getTagsList()));
  //        tagListView.setItems(uniqueTagList.sorted());
  //    }
  //
  //    private void addTagList(String tag) {
  //        if (!tagListView.getItems().contains(tag)) {
  //            ObservableList<String> tempList = tagListView.getItems();
  //            tempList.add(tag);
  //            tagListView.setItems(tempList.sorted());
  //        }
  //    }
  //
  //    @FXML
  //    private void handleTagListClick(MouseEvent event) {
  //        if (event.getClickCount() == 2) {
  //            String tagItem = tagListView.getSelectionModel().getSelectedItem();
  //            tagView.getChildren().add(createTag(tagItem));
  //            imageManager.addTag(tagItem);
  //            // updateFileName(new TreeItem<>(imageManager.getFile()));
  //        }
  //    }
  void setIndependentTagList(ObservableList<String> tagList) {
    this.tagList.setItems(tagList.sorted());
  }
}
