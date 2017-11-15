package app.view;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import app.Tagsta;

import java.io.File;
import java.io.IOException;

/**
 * Root element of the UI (menu bar and border pane)
 */
public class RootLayoutController {
    // Reference to the main application
    private Tagsta main;

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

        if (file != null) {
            // TODO: display image
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("IMAGE INVALID");
            alert.setContentText("COULD NOT OPEN IMAGE FILE");
        }
    }

    /**
     * Opens a DirectoryChooser to let the user select an folder to load.
     */
    @FXML
    private void handleOpenFolder() {
    }
}