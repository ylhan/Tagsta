package app.model;

import app.controller.ExceptionDialogPopup;

import java.awt.Desktop;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * FileManager is responsible for the saving/loading of ImageManagers and the program's
 * configuration file. It is also responsive for moving images on the computer
 */
public class FileManager {

  private static FileHandler logHandler;

  /**
   * Creates a log handler that writes a log to a text file
   */
  public static void createLogHandler(){
    try{
      FileManager.logHandler = new FileHandler("log.txt",true);
      FileManager.logHandler.setLevel(Level.ALL);
      FileManager.logHandler.setFormatter(new SimpleFormatter());
    }
    catch (IOException ex){
      ExceptionDialogPopup.createExceptionPopup("The log could not be made",
              "Changes to images" + "will not be logged");
    }
  }

  /**
   * Returns the log as an observable list of strings from the log text file, and null if the file
   * cannot be found
   * @return The log, or null if its not found
   */
  public static ObservableList<String> getLog(){
    try{
      return FXCollections.observableArrayList(Files.readAllLines(Paths.get("log.txt")));
    }
    catch(IOException ex){
      ExceptionDialogPopup.createExceptionPopup("The log could not be loaded",
      "The log file could not be found");
      return null;
    }
  }

  /**
   * Returns the FileManager's logHandler
   * @return the logHandler
   */
  public static FileHandler getLogHandler() {
    return logHandler;
  }

  /**
   * Closes the log handler
   */
  public static void closeLogHandler(){
    FileManager.logHandler.close();
  }

  /**
   * Finds and returns the configuration file from the working directory
   *
   * @return The config file
   */
  private static File getConfigFile() {
    Path configPath = Paths
        .get("config.properties");
    File file = configPath.toFile();
    if (!file.exists()) {
      HashMap<String, String> configMap = new HashMap<>();
      configMap.put("THEME", "light");
      configMap.put("OPEN_LAST_SESSION", "false");
      configMap.put("LAST_IMAGE_PATH", "");
      configMap.put("LAST_DIRECTORY_PATH", "");
      FileManager.storeConfig(configMap);
    }
    return file;
  }

  /**
   * Stores the given ImageManager by serializing them in a folder in the working directory
   *
   * @param imageManager The ImageManagers to be saved
   */
  static void storeImageManager(ImageManager imageManager){
    try{
      FileManager.createImageManagersFolder();
      OutputStream file = new FileOutputStream("imagemanagers" + File.separator +
                                                imageManager.getFileNumber() + ".ser");
      OutputStream buffer = new BufferedOutputStream(file);
      ObjectOutput output = new ObjectOutputStream(buffer);

      output.writeObject(imageManager);
      output.close();
    }
    catch (IOException ex){
      ExceptionDialogPopup
          .createExceptionPopup("An error occurred while saving Image File data",
              "The file could not be saved");
    }
  }

  /**
   * Finds the serialized ImageManager corresponding to the given file number and returns it. if
   * there is no such ImageManager, returns null
   *
   * @param serFile The number of the ImageManager to be gotten
   * @return The ImageManager corresponding to the given id and null if no such ImageManager exists
   */
  @SuppressWarnings("unchecked")
  private static ImageManager loadImageManager(File serFile) {
    ImageManager imageManager;
    try {
      InputStream file = new FileInputStream(serFile);
      InputStream buffer = new BufferedInputStream(file);
      ObjectInput input = new ObjectInputStream(buffer);

      imageManager = (ImageManager) input.readObject();
      input.close();
    } catch (IOException ex) {
      ExceptionDialogPopup.createExceptionPopup("An error occurred while loading Image data",
          "Image " + serFile + " could not be found or loaded");
      return null;
    } catch (ClassNotFoundException ex) {
      ExceptionDialogPopup
          .createExceptionPopup("An error occurred while finding saved Image data",
              "The changes made to the image " + serFile + " in the past could not be loaded");
      return null;
    }
    return imageManager;
  }

  /**
   * Stores the list of independent tags from TagManager by serializing them in the working directory
   *
   * @param listOfTags The list of tags to be saved
   */
  static void storeTagsList(ArrayList<String> listOfTags) {
    try {
      OutputStream file = new FileOutputStream("tags-list.ser");
      OutputStream buffer = new BufferedOutputStream(file);
      ObjectOutput output = new ObjectOutputStream(buffer);

      output.writeObject(listOfTags);
      output.close();
    } catch (IOException ex) {
      ExceptionDialogPopup
          .createExceptionPopup("An error occurred while saving the independent tags list",
              "The file listOfTags.ser could not be saved");
    }
  }

  /**
   * Finds the serialized independent tags list in the working directory, and creates an
   * empty serialized list if there is none, and returns it
   *
   * @return The list of independent tags stored
   */
  @SuppressWarnings("unchecked")
  static ArrayList<String> loadTagsList() {
    ArrayList<String> listOfTags;
    try {
      InputStream file = new FileInputStream("tags-list.ser");
      InputStream buffer = new BufferedInputStream(file);
      ObjectInput input = new ObjectInputStream(buffer);

      listOfTags = (ArrayList<String>) input.readObject();
      input.close();
    } catch (IOException | ClassNotFoundException ex) {
      listOfTags = new ArrayList<>();
      FileManager.storeTagsList(listOfTags);
    }
    return listOfTags;
  }

  private static void createImageManagersFolder(){
    try{
      Files.createDirectories(Paths.get("imagemanagers"));
    }
    catch (IOException ex){
      ExceptionDialogPopup.createExceptionPopup("The image files could not loaded",
          "The imagemanagers folder could not be created");
    }
  }

  /**
   * Finds the serialized ImageManagers in the working directory, and creates an empty serialized
   * list if there are none, and returns it
   *
   * @return The list of ImageManagers stored
   */
  @SuppressWarnings("unchecked")
  static ArrayList<ImageManager> loadImageManagers() {
    ArrayList<ImageManager> imageManagers = new ArrayList<>();
    File folder = new File("imagemanagers");
    if(!folder.exists()){
      FileManager.createImageManagersFolder();
    }
    if (folder.listFiles() != null) {
      for (File file : folder.listFiles()) {
        imageManagers.add(FileManager.loadImageManager(file));
      }
    }
    return imageManagers;
  }

  /**
   * Gets the number of saved ImageManagers
   * @return The number of saved ImageManagers
   */
  public static long getNumberOfImageManagers(){
    File file = new File("imagemanagers");
    File[] fileList = file.listFiles();
    if (fileList == null){
      return 0;
    }
    else {
      return fileList.length;
    }
  }

  /**
   * Stores the config file into the working directory
   */
  static void storeConfig(HashMap<String, String> configMap) {
    File configFile = new File("config.properties");
    try {
      Properties properties = new Properties();
      for (String key : configMap.keySet()) {
        properties.setProperty(key, configMap.get(key));
      }
      FileWriter writer = new FileWriter(configFile);
      properties.store(writer, "configuration settings");
      writer.close();
    } catch (IOException ex) {
      ExceptionDialogPopup.createExceptionPopup("An error occurred while saving user settings",
          "The changes made to user settings could not be saved");
    }
  }

  /**
   * Loads the config file from the system and returns a HashMap of its contents
   *
   * @return The settings, each corresponding to a boolean value in String form
   */
  static HashMap<String, String> getConfigDetails() {
    HashMap<String, String> configMap = new HashMap<>();
    File configFile = FileManager.getConfigFile();
    try {
      FileReader reader = new FileReader(configFile);
      Properties properties = new Properties();
      properties.load(reader);
      for (String key : properties.stringPropertyNames()) {
        configMap.put(key, properties.getProperty(key));
      }
      reader.close();
    } catch (IOException ex) {
      ExceptionDialogPopup.createExceptionPopup("An error occurred while loading user settings",
          "All configuration details are set to default values");
    }
    return configMap;
  }

  /**
   * Moves an image from the current location to a new location. If there is a file at the new path
   * already, it gives the user an error
   *
   * @param currentPath The current path of the image
   * @param newPath The new path to move the image to
   */
  public static void moveImage(Path currentPath, Path newPath) {
    try {
      if (!newPath.toFile().exists()) {
        Files.move(currentPath, newPath, StandardCopyOption.REPLACE_EXISTING);
      } else {
        ExceptionDialogPopup.createExceptionPopup("An error occurred while moving the image",
            "There is already an image of the same name in the new directory");
      }
    } catch (IOException ex) {
      ExceptionDialogPopup.createExceptionPopup("An error occurred while moving the image",
          "The image could not be moved");
    }
  }

  /**
   * Opens the folder of an image in the Operating System's default file explorer and gives an error
   * if the actions are not able to be done
   * http://www.rgagnon.com/javadetails/java-open-default-os-file-explorer.html
   *
   * @param file The folder to open
   */
  public static void openInExplorer(File file){
    if(Desktop.isDesktopSupported()){
      new Thread(() -> {
        try {
          Desktop.getDesktop().open(file);
        } catch (IOException e1) {
          ExceptionDialogPopup.createExceptionPopup("An error occurred while opening the folder",
                  "The folder could not be found");
        }
      }).start();
    }
    else{
      ExceptionDialogPopup.createExceptionPopup("An error occurred while opening the folder",
              "The file explorer could not be opened on this OS");
    }
  }
}
