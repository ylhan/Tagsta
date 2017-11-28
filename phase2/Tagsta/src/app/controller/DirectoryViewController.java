package app.controller;

import app.model.FileManager;
import app.model.TagManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/** Controller for the directory & file view on the left sidebar */
public class DirectoryViewController {

  @FXML private TreeView<File> directoryView = new TreeView<>();
  @FXML private TreeView<File> fileView = new TreeView<>();

  private ImageOverviewController imageOverviewController;
  private TagManager tagManager;

  /**
   * Updates the file view
   *
   * @param item updated file view item
   */
  void updateFileView(TreeItem<File> item) {
    this.fileView.setRoot(item);
    fileView.refresh();
  }

  /**
   * Updates the directory view
   *
   * @param item updated directory view item
   */
  void updateDirectoryView(TreeItem<File> item) {
    directoryView.setRoot(item);
    directoryView.refresh();
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
      // Make sure the item isn't a directory (directory is not an image)
      if (item != null && !item.getValue().isDirectory()) {
        // Update the Image and the file view
        imageOverviewController.updateImage(tagManager.getImageManager(item.getValue()));
        updateFileView(item);
      }
    }
  }

  /**
   * Updates an old file with a new file in the directory & file view
   *
   * @param oldFile the old file to be updated
   * @param newFile the new file that will take the place of the old file
   */
  void updateFileName(File oldFile, File newFile) {
    // Try to find the oldItem in the directory view tree
    TreeItem<File> oldItem = findTreeItem(directoryView.getRoot(), oldFile);
    // Create a new tree item for the new file
    TreeItem<File> newItem = new TreeItem<>(newFile);
    // If we can find the old item in the tree then replace it with the new one
    if (oldItem != null) {
      TreeItem<File> parentDirectory = oldItem.getParent();
      int oldItemIndex = parentDirectory.getChildren().indexOf(oldItem);
      // Remove the old item
      parentDirectory.getChildren().remove(oldItemIndex);
      // Insert the new item/file
      parentDirectory.getChildren().add(oldItemIndex, newItem);
      // Make the new file selected in the directory
      directoryView.getSelectionModel().select(newItem);
    }
    // Replace the old file in the file view
    updateFileView(newItem);
  }

  /**
   * A recursive method to find a file given the root of a tree
   *
   * @param root the root of the tree to be searched
   * @param file the file to look for
   * @return the file tree item if found else null is returned
   */
  private TreeItem<File> findTreeItem(TreeItem<File> root, File file) {
    // Make sure the root isn't null
    if (root != null) {
      // Check if the root is the item we're looking for
      if (root.getValue().getPath().equals(file.getPath())) {
        return root;
      } else if (root.getValue().isDirectory()) {
        // Check each of the root's children recursively if the root is a directory
        ObservableList<TreeItem<File>> children = root.getChildren();
        for (TreeItem<File> f : children) {
          TreeItem<File> foundItem = findTreeItem(f, file);
          // Return the item if we find it
          if (foundItem != null) {
            return foundItem;
          }
        }
      }
    }
    return null;
  }

  /** Initializes the image overview controller and it's elements */
  @FXML
  private void initialize() {
    /*
    This initializes the the directory view to enable the following:
    - Display only the directory name (folder instead of /home/user/folder)
    - Enables the dragging and dropping of files inside the directory view.
    The following is necessary since TreeItem does not have event listeners. So we have to override/implement
    the following methods to enable the features above.

    I used the following as references:
    https://stackoverflow.com/questions/44210453/how-to-display-only-the-filename-in-a-javafx-treeview
    http://d.hatena.ne.jp/tomoTaka/20131205/1386199115
    */
    directoryView.setCellFactory(
        new Callback<TreeView<File>, TreeCell<File>>() {
          @Override
          public TreeCell<File> call(TreeView<File> stringTreeView) {
            TreeCell<File> treeCell =
                new TreeCell<File>() {
                  private final ImageView FOLDER_ICON =
                      new ImageView(
                          new Image(getClass().getResourceAsStream("/resources/folderIcon.png")));
                  private final ImageView PICTURE_ICON =
                      new ImageView(
                          new Image(getClass().getResourceAsStream("/resources/pictureIcon.png")));

                  /**
                   * Change how the treecell is updated so that it display only the name of the file
                   * (folder instead of /home/user/folder)
                   *
                   * @param item the file that is currently held
                   * @param empty if the cell is empty or not
                   */
                  protected void updateItem(File item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty && item != null) {
                      setText(item.getName());
                      // Set an appropriate icon
                      if (item.isDirectory()) {
                        setGraphic(FOLDER_ICON);
                      } else {
                        setGraphic(PICTURE_ICON);
                      }
                    } else {
                      setText(null);
                      setGraphic(null);
                    }
                  }
                };

            treeCell.setOnDragDetected(
                event -> {
                  TreeItem<File> item = treeCell.getTreeItem();
                  // Only drag and drop files not directories
                  if (item != null && !item.getValue().isDirectory()) {
                    // Add the dragged file to the clip board
                    Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putFiles(Collections.singletonList(item.getValue()));
                    db.setContent(content);
                    event.consume();
                  }
                });

            treeCell.setOnDragOver(
                event -> {
                  TreeItem<File> item = treeCell.getTreeItem();
                  // Only allow for dragging over directories
                  if ((item != null && item.getValue().isDirectory())
                      && event.getGestureSource() != treeCell
                      && event.getDragboard().hasFiles()) {
                    // Set the drag over item's accepted mode of transfer (Move)
                    Path targetPath = treeCell.getTreeItem().getValue().toPath();
                    @SuppressWarnings("unchecked")
                    TreeCell<File> sourceCell = (TreeCell<File>) event.getGestureSource();
                    final Path sourceParentPath =
                        sourceCell.getTreeItem().getValue().getParentFile().toPath();
                    if (sourceParentPath.compareTo(targetPath) != 0) {
                      event.acceptTransferModes(TransferMode.ANY);
                    }
                  }
                  event.consume();
                });

            treeCell.setOnDragEntered(
                event -> {
                  TreeItem<File> item = treeCell.getTreeItem();
                  // Only highlight directories
                  if ((item != null && item.getValue().isDirectory())
                      && event.getGestureSource() != treeCell
                      && event.getDragboard().hasFiles()) {
                    // Highlight the directory that the item is dragged on top of
                    Path targetPath = treeCell.getTreeItem().getValue().toPath();
                    @SuppressWarnings("unchecked")
                    TreeCell<File> sourceCell = (TreeCell<File>) event.getGestureSource();
                    final Path sourceParentPath =
                        sourceCell.getTreeItem().getValue().getParentFile().toPath();
                    if (sourceParentPath.compareTo(targetPath) != 0) {
                      treeCell.updateSelected(true);
                    }
                  }
                  event.consume();
                });

            treeCell.setOnDragExited(
                event -> {
                  // When the drag is exited unhighlight the tree cell
                  TreeItem<File> item = treeCell.getTreeItem();
                  if ((item != null && item.getValue().isDirectory())
                      && event.getGestureSource() != treeCell
                      && event.getDragboard().hasFiles()) {
                    Path targetPath = treeCell.getTreeItem().getValue().toPath();
                    @SuppressWarnings("unchecked")
                    TreeCell<File> sourceCell = (TreeCell<File>) event.getGestureSource();
                    final Path sourceParentPath =
                        sourceCell.getTreeItem().getValue().getParentFile().toPath();
                    if (sourceParentPath.compareTo(targetPath) != 0) {
                      treeCell.updateSelected(false);
                    }
                  }
                  event.consume();
                });

            treeCell.setOnDragDropped(
                event -> {
                  Dragboard db = event.getDragboard();
                  boolean success = false;
                  // Make sure there are file to move
                  if (db.hasFiles()) {
                    final Path source = db.getFiles().get(0).toPath();
                    final Path target =
                        Paths.get(
                            treeCell.getTreeItem().getValue().getPath()
                                + File.separator
                                + source.getFileName());
                    // Move the file to the target (where the file is dropped onto)
                    FileManager.moveImage(source, target);
                    @SuppressWarnings("unchecked")
                    TreeCell<File> t = ((TreeCell<File>) event.getGestureSource());
                    t.getTreeItem().getParent().getChildren().remove(t.getTreeItem());
                    TreeItem<File> newTreeItem = new TreeItem<>(new File(target.toString()));
                    // Update directory view
                    treeCell.getTreeItem().getChildren().add(0, newTreeItem);
                    directoryView.getSelectionModel().select(newTreeItem);
                    success = true;
                    // Update the image manager on the new location of the file
                    tagManager.getImageManager(db.getFiles().get(0)).updateDirectory(target);
                    directoryView.refresh();
                  }
                  event.setDropCompleted(success);
                  event.consume();
                });
            return treeCell;
          }
        });

    // Initializes the file view to display only the directory name (folder instead of
    // /home/user/folder)
    fileView.setCellFactory(
        new Callback<TreeView<File>, TreeCell<File>>() {
          @Override
          public TreeCell<File> call(TreeView<File> tv) {
            return new TreeCell<File>() {

              private final ImageView PICTURE_ICON =
                  new ImageView(
                      new Image(getClass().getResourceAsStream("/resources/pictureIcon.png")));

              /**
               * Change how the tree cell is updated so that it display only the name of the file
               * (folder instead of /home/user/folder)
               *
               * @param item the file that is currently held
               * @param empty if the cell is empty or not
               */
              protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty && item != null) {
                  setText(item.getName());
                  setGraphic(PICTURE_ICON);
                } else {
                  setText(null);
                  setGraphic(null);
                }
              }
            };
          }
        });
  }

  /**
   * Sets the ImageOverviewController so that this controller can interact with the other
   * controllers
   *
   * @param ioc the ImageOverviewController for this program
   */
  void setImageOverviewController(ImageOverviewController ioc) {
    this.imageOverviewController = ioc;
  }

  /**
   * Sets the TagManager so that this controller can get ImageManagers for selected files
   *
   * @param tm the TagManager for this program
   */
  void setTagManager(TagManager tm) {
    this.tagManager = tm;
  }
}
