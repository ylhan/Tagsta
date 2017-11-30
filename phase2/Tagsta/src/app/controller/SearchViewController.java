package app.controller;

import app.model.ImageManager;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/** The controller for the search view window (window where you can search for images and tags) */
public class SearchViewController {
  @FXML private ListView<ImageManager> listOfImages;
  @FXML private TextField search;

  private MenuController menuController;
  private FilteredList<ImageManager> filteredImagesList;

  /**
   * Sets the list of images that will be searched
   *
   * @param im the list of images (held by image managers)
   */
  void setImageList(ObservableList<ImageManager> im) {
    filteredImagesList = new FilteredList<>(im);
    listOfImages.setItems(filteredImagesList);
  }

  /**
   * When the user types in the search bar this method filters the list of images such that only the
   * relevant ones are shown
   * https://stackoverflow.com/questions/28448851/how-to-use-javafx-filteredlist-in-a-listview
   */
  @FXML
  private void filterImageList() {
    String filter = search.getText();
    if (filter == null || filter.length() == 0) {
      filteredImagesList.setPredicate(s -> true);
    } else {
      filteredImagesList.setPredicate(s -> s.getFile().getName().contains(filter));
    }
  }

  /**
   * When an image is double-clicked this method will open the selected image in the file and image
   * view of the program
   *
   * @param event the mouse click of the user
   */
  @FXML
  private void handleClick(MouseEvent event) {
    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
      ImageManager selectedImage = listOfImages.getSelectionModel().getSelectedItem();
      if (selectedImage != null) {
        menuController.openSelectedImage(selectedImage);
      }
    }
  }

  /**
   * When an image is selected and the enter key is pressed this method will open the selected image
   * in the file and image view of the program
   *
   * @param keyPressed the key that the user pressed
   */
  @FXML
  private void handleKeyEnter(KeyEvent keyPressed) {
    if (keyPressed.getCode().equals(KeyCode.ENTER)) {
      menuController.openSelectedImage(listOfImages.getSelectionModel().getSelectedItem());
    }
  }

  /**
   * Gives this controller a reference to the MenuController (this is required to open the
   * image when the user selects one from the list)
   *
   * @param rlc the reference to the controller
   */
  void setMenuController(MenuController rlc) {
    this.menuController = rlc;
  }
}
