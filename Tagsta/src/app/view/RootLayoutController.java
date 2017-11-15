package app.view;


import app.Tagsta;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * Root element of the UI (menu bar and border pane)
 */
public class RootLayoutController {
    // Reference to the main application
    private Tagsta main;

    /**
     * Give directory viewer a reference to the main application
     * @param mainApp the main app
     */
    public void setMainApp(Tagsta mainApp) {
        this.main = mainApp;
    }

    /**
     * Opens a FileChooser to let the user select an image to load.
     */
    @FXML
    private void handleOpenImage() throws IOException {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter (only allow images)
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());

        // Updates the image and the file view if the file is valid
        if (file != null) {
            main.updateImage(new Image("file:" + file.getPath()));
            main.updateFileView(new TreeItem<>(file));
        } else {
            // Show an error if the file is invalid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IMAGE INVALID");
            alert.setContentText("COULD NOT OPEN IMAGE FILE");
        }
    }

    /**
     * Opens a DirectoryChooser to let the user select an folder to load.
     * Modified https://stackoverflow.com/questions/35070310/javafx-representing-directories
     */
    @FXML
    private void handleOpenFolder() {
        DirectoryChooser dc = new DirectoryChooser();
        // Default location
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        // Show directory chooser and get user's choice
        File choice = dc.showDialog(main.getPrimaryStage());
        // Alert if directory is not valid
        if (choice == null || !choice.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("DIRECTORY INVALID");
            alert.setContentText("COULD NOT OPEN DIRECTORY");
            alert.showAndWait();
        } else {
            // Update the directory tree view
            TreeItem<File> root = getNodesForDirectory(choice);
            root.setExpanded(true);
            main.updateDirectoryView(root);
        }
    }

    /**
     * Creates a TreeItem representation of the given directory
     * Modified https://stackoverflow.com/questions/35070310/javafx-representing-directories
     *
     * @param directory the directory to be represented
     * @return a tree item representation of the given directory
     */
    public TreeItem<File> getNodesForDirectory(File directory) {
        TreeItem<File> root = new TreeItem<>(directory);
        // Recursively add all sub-files and sub-directory to the tree
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                // Recursively add sub-directory files
                root.getChildren().add(getNodesForDirectory(f));
            } else {
                // Only add image files
                String ext = f.getName().substring(f.getName().lastIndexOf('.') + 1);
                if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png")) {
                    root.getChildren().add(new TreeItem<>(f));
                }
            }
        }
        return root;
    }
}