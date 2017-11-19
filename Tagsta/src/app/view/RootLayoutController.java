package app.view;

import app.Tagsta;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;

/** Controller for the root element of the UI (menu bar and border pane) */
public class RootLayoutController {
  // Reference to the main application
  private Tagsta main;

  private final ImageView FOLDER_ICON = new ImageView(new Image("/resources/folderIcon.png"));
  private final ImageView PICTURE_ICON = new ImageView(new Image("/resources/pictureIcon.png"));

  @FXML private MenuItem openFolder;
  @FXML private MenuItem openImage;
    
    /**
   * Give directory viewer a reference to the main application
   *
   * @param mainApp the main app
   */
  public void setMainApp(Tagsta mainApp) {
    this.main = mainApp;
  }

  /** Opens a FileChooser to let the user select an image to load. */
  @FXML
  private void handleOpenImage() throws IOException {
    FileChooser fileChooser = new FileChooser();

    // Set extension filter (only allow images)
    FileChooser.ExtensionFilter imageFilter =
        new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
    fileChooser.getExtensionFilters().add(imageFilter);

    // Show open file dialog
    File file = fileChooser.showOpenDialog(main.getPrimaryStage());

    // Updates the image and the file view if the file isn't null
    if (file != null) {
      main.updateImage(main.getTagManager().getImageManager(file));
      main.updateFileView(new TreeItem<>(file));
    } else {
      // Show an error if the file is invalid
      ExceptionDialogPopup.createExceptionPopup("Image Invalid", "Could not open image.");
    }
  }

  /**
   * Opens a DirectoryChooser to let the user select an folder to load. Modified
   * https://stackoverflow.com/questions/35070310/javafx-representing-directories
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
      ExceptionDialogPopup.createExceptionPopup("Directory Invalid", "Could not open directory.");
    } else {
      // Update the directory tree view
      TreeItem<File> root = getNodesForDirectory(choice);
      root.setExpanded(true);
      main.updateDirectoryView(root);
    }
  }

  /**
   * Pops up an information dialog when the user selects the about option in the menu
   */
  @FXML
  private void handleAbout(){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    // Set the description for the project
    alert.setTitle("About");
    alert.setHeaderText("CSC207 PROJECT");
    String aboutInfo = "A program for managing and tagging images.\n\nCreated by YiLun (Allen) Han, Samin Khan, George Ly, & Amritpal Aujla\n";

    // Add the about dialog info to a text area
    TextArea textArea = new TextArea(aboutInfo);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    // Add the text area to the dialog and then display it
    alert.getDialogPane().setContent(textArea);
    alert.showAndWait();
  }

  /**
   * Sets the theme to a dark theme when the user selected the Dark theme option in the menu
   */
  @FXML
  private void setDarkTheme() {
    main.setDarkTheme();
  }

  /**
   * Sets the theme to a light theme when the user selected the Light theme option in the menu
   */
  @FXML
  private void setLightTheme() {
    main.setLightTheme();
  }

  /**
   * Creates a TreeItem representation of the given directory Modified
   * https://stackoverflow.com/questions/35070310/javafx-representing-directories
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

  /**
   * Initializes the the menu bar
   */
  @FXML
  private void initialize(){
    // Set icons for the menu items
    FOLDER_ICON.setFitHeight(15);
    FOLDER_ICON.setFitWidth(15);
    openFolder.setGraphic(FOLDER_ICON);

    PICTURE_ICON.setFitHeight(15);
    PICTURE_ICON.setFitWidth(15);
    openImage.setGraphic(PICTURE_ICON);
  }

  /**
   * Exits option in menu bar that will exit the program
   */
  @FXML
    private void exit() {
      System.exit(0);
  }
}
