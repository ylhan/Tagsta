package app.view;

import app.Tagsta;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.File;

/** Controller for the directory viewer (file/directory tree) */
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
        image.setFitHeight(sp.getHeight());
        image.setFitWidth(sp.getWidth());
    }

    /**
     * Zooms in to the image
     */
    @FXML
    private void handleZoomIn() {
        if (image.getFitHeight() < 10000 && image.getFitWidth() < 10000) {
            image.setFitHeight(image.getFitHeight() * 1.1);
            image.setFitWidth(image.getFitWidth() * 1.1);
        }
    }

    /**
     * Zooms out of the image
     */
    @FXML
    private void handleZoomOut() {
        System.out.println(image.getFitHeight());
        if (image.getFitHeight() > 50 && image.getFitWidth() > 50) {
            image.setFitHeight(image.getFitHeight() * 0.9);
            image.setFitWidth(image.getFitWidth() * 0.9);
        }
    }
    /**
     * Handles user selecting/double clicking an item from a tree view
     * https://stackoverflow.com/questions/17348357/how-to-trigger-event-when-double-click-on-a-tree-node
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
                updateImage(new Image("file:" + item.getValue().getPath()));
                // Update the file view
                updateFileView(item);
            }
        }
    }

    /**
     * Initializes the directory view controller
     */
    @FXML
    private void initialize() {

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