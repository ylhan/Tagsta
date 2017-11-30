package app.controller;

import app.Tagsta;
import app.model.FileManager;
import app.model.ImageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/** Controller for the root element of the UI (menu bar and border pane) */
public class RootLayoutController {

  // Reference to the main application
  private Tagsta main;

  private Stage searchWindow;

  private final ImageView FOLDER_ICON = new ImageView(new Image("/resources/folderIcon.png"));
  private final ImageView PICTURE_ICON = new ImageView(new Image("/resources/pictureIcon.png"));

  @FXML private MenuItem openFolder;
  @FXML private MenuItem openImage;
  @FXML private RadioMenuItem darkTheme;
  @FXML private MenuItem showLog;
  @FXML private MenuItem showImageFolder;
  @FXML private RadioMenuItem loadLastSession;

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
        new FileChooser.ExtensionFilter(
            "Image Files",
            "*.jpg",
            "*.png",
            "*.jpeg",
            "*.gif",
            "*.bmp",
            "*.JPG",
            "*.PNG",
            "*.JPEG",
            "*.GIF",
            "*.BMP");
    fileChooser.getExtensionFilters().add(imageFilter);

    // Show open file dialog
    File file = fileChooser.showOpenDialog(main.getPrimaryStage());

    // Updates the image and the file view if the file isn't null
    if (file != null) {
      openSelectedImage(main.getTagManager().getImageManager(file));
    } else {
      // Show an error if the file is invalid
      ExceptionDialogPopup.createExceptionPopup("Image Invalid", "Could not open image.");
    }
  }

  /**
   * Opens the given image in the file view and the image view (held by the image manager)
   *
   * @param image the image held by the image manager
   */
  void openSelectedImage(ImageManager image) {
    main.updateImage(image);
    main.updateFileView(new TreeItem<>(image.getFile()));
    // Close the search window if it's opened
    if (searchWindow != null) {
      searchWindow.close();
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
      openSelectedFolder(getNodesForDirectory(choice));
    }
  }

  /**
   * Opens the selected folder in the directory view
   *
   * @param root the root tree item that holds the root folder
   */
  private void openSelectedFolder(TreeItem<File> root) {
    root.setExpanded(true);
    main.updateDirectoryView(root);
  }

  /** Pops up an information dialog when the user selects the about option in the menu */
  @FXML
  private void handleAbout() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    // Set the description for the project
    alert.setTitle("About");
    alert.setHeaderText("CSC207 PROJECT");
    String aboutInfo =
        "A program for managing and tagging images.\n\nCreated by YiLun (Allen) Han, Samin Khan, George Ly, & Amritpal Aujla\n";

    // Add the about dialog info to a text area
    TextArea textArea = new TextArea(aboutInfo);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    // Add the text area to the dialog and then display it
    alert.getDialogPane().setContent(textArea);
    alert.showAndWait();
  }

  /** Handle show log menu item */
  @FXML
  private void handleShowLog() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/app/view/LogView.fxml"));
      BorderPane showLog = loader.load();

      // Create the new window and show it
      Stage logWindow = new Stage();
      logWindow.setTitle("Program Log");
      logWindow.setScene(new Scene(showLog, 800, 600));
      logWindow.getIcons().add(Tagsta.getIcon());
      // Set the focus on this stage
      logWindow.initOwner(main.getPrimaryStage());
      logWindow.initModality(Modality.WINDOW_MODAL);

      logWindow.show();
      // Add the logs to be displayed
      LogViewController lvc = loader.getController();
      lvc.setLog(FileManager.getLog());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Handle the show search menu item */
  @FXML
  private void handleShowSearch() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/app/view/SearchView.fxml"));
      BorderPane showSearch = loader.load();

      // Create the new window and show it
      searchWindow = new Stage();
      searchWindow.setTitle("Search");
      searchWindow.setScene(new Scene(showSearch, 800, 600));
      searchWindow.getIcons().add(Tagsta.getIcon());
      // Set the focus on this stage
      searchWindow.initOwner(main.getPrimaryStage());
      searchWindow.initModality(Modality.WINDOW_MODAL);

      searchWindow.show();
      // Add image managers to the search window
      SearchViewController svc = loader.getController();
      svc.setImageList(main.getTagManager().getImageManagers());
      // Give the controller a reference to this controller
      svc.setRootLayoutController(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handle the menu item "Show Image Folder" which opens the current image's folder in the file
   * explorer
   */
  @FXML
  private void handleShowImageFolder() {
    FileManager.openInExplorer(
        main.getImageOverviewController().getImageManager().getFile().getParentFile());
  }

  /**
   * By default (i.e. start of the program) there is no image displayed so show image folder are
   * disabled. This method will enable the item.
   */
  void enableMenuItems() {
    showImageFolder.setDisable(false);
  }

  /** Sets the theme to a dark theme when the user selected the Dark theme option in the menu */
  @FXML
  private void setDarkTheme() {
    main.setDarkTheme();
  }

  /** Sets the theme to a light theme when the user selected the Light theme option in the menu */
  @FXML
  private void setLightTheme() {
    main.setLightTheme();
  }

  /**
   * Loads the given theme
   *
   * @param theme the theme to load
   */
  public void loadTheme(String theme) {
    if (theme != null) {
      if (theme.equals("light")) {
        setLightTheme();
      } else {
        setDarkTheme();
        // Toggle the selected theme
        darkTheme.setSelected(true);
      }
    }
  }

  /**
   * Loads the previous session of the program (previously opened image and previous opened
   * directory) if the option to do so is enabled in the settings
   */
  public void loadLastSession() {
    // Get the stored config of whether or not to load the previous session
    if (Boolean.valueOf(main.getTagManager().getConfigOption("OPEN_LAST_SESSION"))) {
      // Popup a confirmation to make sure the user wants to opened the last session
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Confirm Open Last Session");
      alert.setHeaderText("Open Last Session");
      alert.setContentText("Do you want to load the last session?");

      Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        loadLastSession.setSelected(true);
        String lastImagePath = main.getTagManager().getConfigOption("LAST_IMAGE_PATH");
        String lastDirectoryPath = main.getTagManager().getConfigOption("LAST_DIRECTORY_PATH");
        // Make sure there is an actual image path saved
        if (!lastImagePath.isEmpty()) {
          // Loads the previously opened image
          openSelectedImage(main.getTagManager().getImageManager(new File(lastImagePath)));
        }
        // Make sure there is an actual directory path saved
        if (!lastDirectoryPath.isEmpty()) {
          // Loads the previously opened directory
          openSelectedFolder(getNodesForDirectory(new File(lastDirectoryPath)));
        }
      }

    } else {
      loadLastSession.setSelected(false);
    }
  }

  /** Sets the config option of whether or not to load the last session */
  @FXML
  private void setLoadLastSession() {
    main.getTagManager()
        .setConfigOption("OPEN_LAST_SESSION", String.valueOf(loadLastSession.isSelected()));
  }

  /**
   * Creates a TreeItem representation of the given directory Modified
   * https://stackoverflow.com/questions/35070310/javafx-representing-directories
   *
   * @param directory the directory to be represented
   * @return a tree item representation of the given directory
   */
  private TreeItem<File> getNodesForDirectory(File directory) {
    TreeItem<File> root = new TreeItem<>(directory);
    // Recursively add all sub-files and sub-directory to the tree
    File[] directoryFiles = directory.listFiles();
    if (directoryFiles != null) {
      for (File f : directoryFiles) {
        if (f.isDirectory()) {
          // Recursively add sub-directory files
          root.getChildren().add(getNodesForDirectory(f));
        } else {
          // Only add image files
          final String[] validExtensions = {"jpg", "png", "jpeg", "gif", "bmp"};
          String ext = f.getName().substring(f.getName().lastIndexOf('.') + 1);
          if (Arrays.asList(validExtensions).indexOf(ext.toLowerCase()) != -1) {
            // Populate the independent tag list
            main.getTagManager().addIndependentTag(f);
            // Add the file to the tree
            root.getChildren().add(new TreeItem<>(f));
          }
        }
      }
    }
    return root;
  }

  /** Initializes the the menu bar */
  @FXML
  private void initialize() {
    // Set icons for the menu items
    FOLDER_ICON.setFitHeight(15);
    FOLDER_ICON.setFitWidth(15);
    openFolder.setGraphic(FOLDER_ICON);

    PICTURE_ICON.setFitHeight(15);
    PICTURE_ICON.setFitWidth(15);
    openImage.setGraphic(PICTURE_ICON);
  }

  /** Exit option in menu bar that will exit the program */
  @FXML
  private void exit() {
    System.exit(0);
  }
}
