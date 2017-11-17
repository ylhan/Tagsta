package app.view;

import app.Tagsta;
import app.model.ImageManager;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
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

  @FXML private Button showHistory;

  @FXML private TextField tf;

  private TreeItem<File> selectedItemParent;
  private TreeItem<File> selectedItem;

  private Tagsta main;

  private ImageManager im;
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
    if (selectedItemParent != null) {
      int index = selectedItemParent.getChildren().indexOf(selectedItem);
      selectedItemParent.getChildren().remove(index);
      selectedItemParent.getChildren().add(index, item);
      directoryView.getSelectionModel().select(item);
      selectedItem = item;
      directoryView.refresh();
    }
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
    selectedItemParent = null;
  }

  /**
   * Updates the image
   *
   * @param im the updated images
   */
  public void updateImage(ImageManager im) {
    this.im = im;
    image.setImage(im.getImage());
    if (sp.getHeight() <= image.getImage().getHeight()
        || sp.getWidth() <= image.getImage().getWidth()) {
      image.setFitWidth(sp.getWidth());
      image.setFitHeight(sp.getHeight());
    } else {
      image.setFitWidth(image.getImage().getWidth());
      image.setFitHeight(image.getImage().getHeight());
    }
    newTagView(im.getTags());
  }

  private void newTagView(ObservableList<String> tags) {
    tagView.getChildren().clear();
    for (String tag : tags) {
      tagView.getChildren().add(createTag(tag));
    }
    main.getTagManager().saveProgram();
  }

  @FXML
  private void addTag() {
    if (im != null) {
      String tag = tf.getText();
      tag = tag.trim();
      if (tag != null && tag.length() >= 1) {
        tagView.getChildren().add(createTag(tag));
        im.addTag(tag);
        tf.clear();
        updateFileView(new TreeItem<>(im.getFile()));
        main.getTagManager().saveProgram();
      }
    }
  }

  @FXML
  private void addTagOnEnter(KeyEvent keyPressed) {
    if (keyPressed.getCode().equals(KeyCode.ENTER)) {
      addTag();
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
    if (im != null) {
        Stage historyWindow = new Stage();
        historyWindow.setTitle("History of names");
        Group root = new Group();
        historyWindow.setScene(new Scene(root, 800, 600));
        ListView<String> list = new ListView<String>();
        list.setItems(this.im.getPrevNames());
        list.setPrefSize(800, 600);
        root.getChildren().add(list);

        list.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton().equals(MouseButton.PRIMARY)) {
                            if (event.getClickCount() == 2) {
                                String listItem = list.getSelectionModel().getSelectedItem();
                                im.revert(listItem);
                                newTagView(im.getTags());
                                updateFileView(new TreeItem<>(im.getFile()));
                            }
                        }
                    }
                });
        historyWindow.show();
    }
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
      selectedItemParent = item.getParent();
      selectedItem = item;
      // Make sure the item isn't a directory
      if (!item.getValue().isDirectory()) {
        // Update the Image
        main.updateImage(main.getTagManager().getImageManager(item.getValue()));
        // Update the file view
        updateFileView(item);
      }
    }
  }

  /** Handles user selecting/pressing ENTER on keyboard for an item from a tree view */
  @FXML
  private void handleKeyEnterItem(KeyEvent keyPressed) {
    // Check for key ENTER
    if (keyPressed.getCode().equals(KeyCode.ENTER)) {
      // Get the selected item
      TreeItem<File> item = directoryView.getSelectionModel().getSelectedItem();
      // Make sure the item isn't a directory
      if (!item.getValue().isDirectory()) {
        // Update the Image
        main.updateImage(main.getTagManager().getImageManager(item.getValue()));
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
      t.handleDeleteTag(event -> removeTag(h, tagString));
      return h;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void removeTag(HBox tag, String tagString) {
    tagView.getChildren().remove(tag);
    im.removeTag(tagString);
    updateFileView(new TreeItem<>(im.getFile()));
    main.getTagManager().saveProgram();
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
