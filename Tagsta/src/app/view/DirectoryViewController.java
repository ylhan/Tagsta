package app.view;

import app.Tagsta;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.File;

/**
 * Controller for the directory viewer (file/directory tree)
 */
public class DirectoryViewController {
    @FXML
    private ImageView image;

    @FXML
    private TreeView<File> directoryView = new TreeView<>();

    @FXML
    private TreeView<File> fileView = new TreeView<>();

    private Tagsta main;

    /**
     * Give directory viewer a reference to the main application
     * @param mainApp the main app
     */
    public void setMainApp(Tagsta mainApp) {
        this.main = mainApp;

    }

    /**
     * Updates the file view
     * @param item updated file view
     */
    public void updateFileView(TreeItem<File> item) {
        fileView.refresh();
        this.fileView.setRoot(item);
    }

    /**
     * Updates the directory view
     * @param item updated directory view
     */
    public void updateDirectoryView(TreeItem<File> item) {
        directoryView.refresh();
        this.directoryView.setRoot(item);
    }

    /**
     * Updates the image
     * @param img the updated images
     */
    public void updateImage(Image img) {
        image.setImage(img);
    }

    /**
     * Initializes the directory view controller
     */
    @FXML
    private void initialize() {
        // https://stackoverflow.com/questions/44210453/how-to-display-only-the-filename-in-a-javafx-treeview
        // Display only the directory name (folder2 instead of /home/user/folder2)
        directoryView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {

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

        fileView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {

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