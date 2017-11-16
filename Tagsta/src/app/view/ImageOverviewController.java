package app.view;

import app.Tagsta;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;

/** Controller for the directory viewer (file/directory tree) */
public class ImageOverviewController {
  @FXML private ImageView image;

  @FXML private TreeView<File> directoryView = new TreeView<>();

  @FXML private TreeView<File> fileView = new TreeView<>();

  @FXML private ScrollPane sp;

  @FXML private Button zoomIn;

  @FXML private Button zoomOut;

  @FXML private FlowPane tagView;

  private Tagsta main;

  /**
   * Give directory viewer a reference to the main application
   *
   * @param mainApp the main app
   */
  public void setMainApp(Tagsta mainApp) {
    this.main = mainApp;
  }

  /**
   * Updates the file view
   *
   * @param item updated file view
   */
  public void updateFileView(TreeItem<File> item) {
    fileView.refresh();
    this.fileView.setRoot(item);
  }

  /**
   * Updates the directory view
   *
   * @param item updated directory view
   */
  public void updateDirectoryView(TreeItem<File> item) {
    directoryView.refresh();
    this.directoryView.setRoot(item);
  }

  /**
   * Updates the image
   *
   * @param img the updated images
   */
  public void updateImage(Image img) {
    image.setImage(img);
    if (sp.getHeight() <= image.getImage().getHeight()
        || sp.getWidth() <= image.getImage().getWidth()) {
      image.setFitWidth(sp.getWidth());
      image.setFitHeight(sp.getHeight());
    } else {
      image.setFitWidth(image.getImage().getWidth());
      image.setFitHeight(image.getImage().getHeight());
    }
  }

  /** Zooms in to the image */
  @FXML
  private void handleZoomIn() {
    if (image.getFitHeight() < 10000 && image.getFitWidth() < 10000) {
      image.setFitHeight(image.getFitHeight() * 1.1);
      image.setFitWidth(image.getFitWidth() * 1.1);
    }
  }

  /** Zooms out of the image */
  @FXML
  private void handleZoomOut() {
    if (image.getFitHeight() > 50 && image.getFitWidth() > 50) {
      image.setFitHeight(image.getFitHeight() * 0.9);
      image.setFitWidth(image.getFitWidth() * 0.9);
    }
  }

  @FXML
  private void handleShowHistory() {
    Stage historyWindow = new Stage();
  }

  /**
   * Handles user selecting/double clicking an item from a tree view
   * https://stackoverflow.com/questions/17348357/how-to-trigger-event-when-double-click-on-a-tree-node
   *
   * @param mouseEvent the user's click
   */
  @FXML
  private void handleDoubleClickItem(MouseEvent mouseEvent) {
    // Check for double click
    if (mouseEvent.getClickCount() == 2) {
      // Get the selected item
      TreeItem<File> item = directoryView.getSelectionModel().getSelectedItem();

      // Make sure the item isn't a directory
      if (!item.getValue().isDirectory()) {
        // Update the Image
        main.updateImage(main.getTagManager().getImageManager(item.getValue()).getImage());
        // Update the file view
        updateFileView(item);
      }
    }
  }

  private HBox createTag(String tagString) {
    try {
      // Load root layout from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("Tag.fxml"));
      HBox h = loader.load();
      Tag t = loader.getController();
      t.setTag(tagString);
      t.handleDeleteTag(event -> tagView.getChildren().remove(h));
      return h;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  /** Initializes the directory view controller */
  @FXML
  private void initialize() {
    tagView.setVgap(10);
    tagView.setHgap(10);
    // https://stackoverflow.com/questions/44210453/how-to-display-only-the-filename-in-a-javafx-treeview
    // Display only the directory name (folder2 instead of /home/user/folder2)
    directoryView.setCellFactory(
        new Callback<TreeView<File>, TreeCell<File>>() {

          public TreeCell<File> call(TreeView<File> tv) {
            return new TreeCell<File>() {

              @Override
              protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                setText((empty || item == null) ? "" : item.getName());
              }
            };
          }
        });

    fileView.setCellFactory(
        new Callback<TreeView<File>, TreeCell<File>>() {

          public TreeCell<File> call(TreeView<File> tv) {
            return new TreeCell<File>() {

              @Override
              protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                setText((empty || item == null) ? "" : item.getName());
              }
            };
          }
        });
  }
}
