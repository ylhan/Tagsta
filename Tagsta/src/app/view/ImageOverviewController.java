package app.view;

import app.Tagsta;
import app.model.FileManager;
import app.model.ImageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Controller for the image overview (contains the directory view, tag view, and image view).
 */
public class ImageOverviewController {
    @FXML
    private ImageView image;

    @FXML
    private TreeView<File> directoryView = new TreeView<>();

    @FXML
    private TreeView<File> fileView = new TreeView<>();

    @FXML
    private ScrollPane sp;

    @FXML
    private Button zoomIn;

    @FXML
    private Button zoomOut;

    @FXML
    private Button addTag;

    @FXML
    private Button showHistory;

    @FXML
    private FlowPane tagView;

    @FXML
    private TextField tf;

    // Stores the selected item & it's parent when the user selects an item from the directory view
    private TreeItem<File> selectedItemParent;
    private TreeItem<File> selectedItem;

    // Reference to the main application
    private Tagsta main;

    // A wrapper for the image and it's metadata (tag history, previous names, etc)
    private ImageManager im;

    private final ImageView ZOOM_IN_ICON = new ImageView(new Image("/resources/zoomIn.png"));
    private final ImageView ZOOM_OUT_ICON = new ImageView(new Image("/resources/zoomOut.png"));

    /**
     * Give image overview a reference to the main application
     *
     * @param mainApp the main application
     */
    public void setMainApp(Tagsta mainApp) {
        this.main = mainApp;
    }

    /**
     * Updates the file view
     *
     * @param item updated file view item
     */
    public void updateFileView(TreeItem<File> item) {
        /* This "syncs" the file view with the selected file from the directory view. This checks if the updated item
           is opened from the directory view, this is necessary since you can open images directly from the menu.
        */
        if (selectedItemParent != null) {
            // Updates the selected item's parent with the new item
            int index = selectedItemParent.getChildren().indexOf(selectedItem);
            selectedItemParent.getChildren().remove(index);
            selectedItemParent.getChildren().add(index, item);
            // Make the selected item highlighted
            directoryView.getSelectionModel().select(item);
            // Sets the new selected item and refreshes the directory view
            selectedItem = item;
            directoryView.refresh();
        }
        // Sets the new file in the file view and refreshes it
        this.fileView.setRoot(item);
        fileView.refresh();
    }

    /**
     * Updates the directory view
     *
     * @param item updated directory view item
     */
    public void updateDirectoryView(TreeItem<File> item) {
        directoryView.refresh();
        this.directoryView.setRoot(item);
        // When the directory view is updated nothing is selected
        selectedItem = null;
        selectedItemParent = null;
    }

    /**
     * Updates the image
     *
     * @param im the wrapper for the image
     */
    public void updateImage(ImageManager im) {
        this.im = im;
        // Gets the image from the wrapper and sets the image
        image.setImage(im.getImage());

        // This will resize the image to fit inside the image view if it is too big
        if (sp.getHeight() <= image.getImage().getHeight()
                || sp.getWidth() <= image.getImage().getWidth()) {
            image.setFitWidth(sp.getWidth());
            image.setFitHeight(sp.getHeight());
        } else {
            image.setFitWidth(image.getImage().getWidth());
            image.setFitHeight(image.getImage().getHeight());
        }
        // Get the tags to be displayed from the image manager
        newTagView(im.getTags());
        // Enable the controls (zoom, add tag, show history) now that there is an image
        enableImageControls();
    }

    /**
     * Used to enable button/text-fields that are disabled by default (only enable when there is an image)
     */
    private void enableImageControls() {
        // Enable all controls
        addTag.setDisable(false);
        showHistory.setDisable(false);
        tf.setDisable(false);
        zoomIn.setDisable(false);
        zoomOut.setDisable(false);
    }

    /**
     * Creates a new tag view (loads the tags of an image so they can be displayed
     * @param tags
     */
    private void newTagView(ObservableList<String> tags) {
        tagView.getChildren().clear();
        for (String tag : tags) {
            tagView.getChildren().add(createTag(tag));
        }
        main.getTagManager().saveProgram();
    }

    /**
     * Add a tag to the image
     */
    @FXML
    private void addTag() {
        if (im != null) {
            // Get the text from the text-field
            String tag = tf.getText();
            // Trim off whitespace
            tag = tag.trim();
            // Add the tag to the image manager and reload the tags in the tag view
            im.addTag(tag);
            newTagView(im.getTags());
            // Clear the text from the text-field
            tf.clear();

            // Update the file view and save the tags
            updateFileView(new TreeItem<>(im.getFile()));
            main.getTagManager().saveProgram();
        }
    }


    /**
     * Add the tag currently in the text-field when the user presses Enter
     * @param keyPressed the key that was pressed
     */
    @FXML
    private void addTagOnEnter(KeyEvent keyPressed) {
        if (keyPressed.getCode().equals(KeyCode.ENTER)) {
            addTag();
        }
    }

    /**
     * Zooms in to the image
     */
    @FXML
    private void handleZoomIn() {
        // Make sure the image doesn't get too big
        if (image.getFitHeight() < 10000 && image.getFitWidth() < 10000) {
            // Zoom by 10% each time
            image.setFitHeight(image.getFitHeight() * 1.1);
            image.setFitWidth(image.getFitWidth() * 1.1);
        }
    }

    /**
     * Zooms out of the image
     */
    @FXML
    private void handleZoomOut() {
        // Make sure the image doesn't get too small
        if (image.getFitHeight() > 50 && image.getFitWidth() > 50) {
            // Zoom out by 10% each time
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
                                    int nameIndex = im.getPrevNames().indexOf(listItem);
                                    if (im.getTags().equals(FXCollections.observableArrayList(im.getPreviousTags().get(nameIndex)))) {
                                        ExceptionDialogPopup.createExceptionPopup("Error reverting tags", "Image already has this name!");
                                    } else {
                                        im.revert(listItem);
                                        newTagView(im.getTags());
                                        updateFileView(new TreeItem<>(im.getFile()));
                                        historyWindow.close();
                                    }
                                }
                            }
                        }
                    });
            historyWindow.show();
        }
    }

    /**
     * Handles user selecting/double clicking an item from a tree view
     * Modified https://stackoverflow.com/questions/17348357/how-to-trigger-event-when-double-click-on-a-tree-node
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
            if (!item.getValue().isDirectory()) {
                // Update the selected item/parent
                selectedItemParent = item.getParent();
                selectedItem = item;
                // Update the Image and the file view
                main.updateImage(main.getTagManager().getImageManager(item.getValue()));
                updateFileView(item);
            }
        }
    }

    /**
     * Creates a tag element with the given String. A "tag" is really a styled HBox with labels inside.
     * @param tagString the string the tag will display
     * @return the new tag
     */
    private HBox createTag(String tagString) {
        try {
            // Load tag from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Tag.fxml"));
            HBox h = loader.load();

            // Get the controller for the tag
            TagController t = loader.getController();
            // Set the string for the tag
            t.setTag(tagString);
            // Define what happens when the tag is closed
            t.handleDeleteTag(event -> removeTag(h, tagString));
            return h;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes a tag from the tag view
     * @param tag the tag to be removed
     * @param tagString  the string of the tag
     */
    private void removeTag(HBox tag, String tagString) {
        // Remove the tag from the tag view
        tagView.getChildren().remove(tag);
        // Remove the tag from the image's manager
        im.removeTag(tagString);

        // Update view of the file and save it
        updateFileView(new TreeItem<>(im.getFile()));
        main.getTagManager().saveProgram();
    }

    /**
     * Initializes the image overview controller and it's elements
     */
    @FXML
    private void initialize() {
        // Sets icons for the zoom in and out buttons
        ZOOM_IN_ICON.setFitHeight(20);
        ZOOM_IN_ICON.setFitWidth(20);
        zoomIn.setGraphic(ZOOM_IN_ICON);
        ZOOM_OUT_ICON.setFitHeight(20);
        ZOOM_OUT_ICON.setFitWidth(20);
        zoomOut.setGraphic(ZOOM_OUT_ICON);

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
        directoryView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            @Override
            public TreeCell<File> call(TreeView<File> stringTreeView) {
                TreeCell<File> treeCell = new TreeCell<File>() {
                    private final ImageView FOLDER_ICON = new ImageView(new Image(getClass().getResourceAsStream("/resources/folderIcon.png")));
                    private final ImageView PICTURE_ICON = new ImageView(new Image(getClass().getResourceAsStream("/resources/pictureIcon.png")));

                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty && item != null) {
                            setText(item.getName());
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

                treeCell.setOnDragDetected(event -> {
                    TreeItem<File> item = treeCell.getTreeItem();
                    // Only drag and drop files
                    if (item != null && !item.getValue().isDirectory()) {
                        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putFiles(Arrays.asList(item.getValue()));
                        db.setContent(content);
                        event.consume();
                    }
                });

                treeCell.setOnDragOver(event -> {
                    TreeItem<File> item = treeCell.getTreeItem();
                    if ((item != null && item.getValue().isDirectory()) &&
                            event.getGestureSource() != treeCell &&
                            event.getDragboard().hasFiles()) {
                        Path targetPath = treeCell.getTreeItem().getValue().toPath();
                        TreeCell<File> sourceCell = (TreeCell<File>) event.getGestureSource();
                        final Path sourceParentPath = sourceCell.getTreeItem().getValue().getParentFile().toPath();
                        if (sourceParentPath.compareTo(targetPath) != 0) {
                            event.acceptTransferModes(TransferMode.ANY);
                        }
                    }
                    event.consume();
                });

                treeCell.setOnDragEntered(event -> {
                    TreeItem<File> item = treeCell.getTreeItem();
                    if ((item != null && item.getValue().isDirectory()) &&
                            event.getGestureSource() != treeCell &&
                            event.getDragboard().hasFiles()) {
                        Path targetPath = treeCell.getTreeItem().getValue().toPath();
                        TreeCell<File> sourceCell = (TreeCell<File>) event.getGestureSource();
                        final Path sourceParentPath = sourceCell.getTreeItem().getValue().getParentFile().toPath();
                        if (sourceParentPath.compareTo(targetPath) != 0) {
                            treeCell.updateSelected(true);
                        }
                    }
                    event.consume();
                });

                treeCell.setOnDragExited(event -> {
                    TreeItem<File> item = treeCell.getTreeItem();
                    if ((item != null && item.getValue().isDirectory()) &&
                            event.getGestureSource() != treeCell &&
                            event.getDragboard().hasFiles()) {
                        Path targetPath = treeCell.getTreeItem().getValue().toPath();
                        TreeCell<File> sourceCell = (TreeCell<File>) event.getGestureSource();
                        final Path sourceParentPath = sourceCell.getTreeItem().getValue().getParentFile().toPath();
                        if (sourceParentPath.compareTo(targetPath) != 0) {
                            treeCell.updateSelected(false);
                        }
                    }
                    event.consume();
                });

                treeCell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasFiles()) {
                        final Path source = db.getFiles().get(0).toPath();
                        final Path target = Paths.get(treeCell.getTreeItem().getValue().getPath() + File.separator + source.getFileName());
                        FileManager.moveImage(source, target);
                        // update directory view
                        TreeCell<File> t = ((TreeCell<File>) event.getGestureSource());
                        t.getTreeItem().getParent().getChildren().remove(t.getTreeItem());
                        TreeItem<File> newTreeItem = new TreeItem<>(new File(target.toString()));
                        treeCell.getTreeItem().getChildren().add(0, newTreeItem);
                        directoryView.getSelectionModel().select(newTreeItem);
                        selectedItem = newTreeItem;
                        selectedItemParent = newTreeItem.getParent();
                        success = true;
                        im.updateDirectory(target);
                        directoryView.refresh();
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });


                return treeCell;
            }
        });

        // Initializes the file view to display only the directory name (folder instead of /home/user/folder)
        fileView.setCellFactory(
                new Callback<TreeView<File>, TreeCell<File>>() {
                    @Override
                    public TreeCell<File> call(TreeView<File> tv) {
                        return new TreeCell<File>() {

                            private final ImageView PICTURE_ICON = new ImageView(new Image(getClass().getResourceAsStream("/resources/pictureIcon.png")));

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
}
