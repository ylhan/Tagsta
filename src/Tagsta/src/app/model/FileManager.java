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
 * FileManager is responsible for the saving/loading of files like the ImageManagers, the config,
 * and the independent tags list. It is also responsive for moving images on the computer and handling
 * the log's FileHandler.
 */
public class FileManager {

  private static FileHandler logHandler;

  /**
   * Creates a log handler that writes a log to a text file with some default properties
   */
  static void createLogHandler(){
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
   * Returns the logger's handler
   * @return the logger's handler
   */
  static FileHandler getLogHandler() {
    return logHandler;
  }

  /**
   * Closes the logger's handler
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

    //Makes the file with some default properties if it does not exist
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
   * @param imageManager The ImageManager to be saved
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
   * Finds the serialized ImageManager corresponding to the given file and returns it. if
   * there is no such ImageManager, returns null
   *
   * @param serFile The file of the ImageManager to be returned
   * @return The ImageManager corresponding to the given file and null if no such file exists
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
    }
    catch (IOException ex) {
      ExceptionDialogPopup.createExceptionPopup("An error occurred while loading Image data",
          "Image " + serFile + " could not be found or loaded");
      return null;
    }
    catch (ClassNotFoundException ex) {
      ExceptionDialogPopup
          .createExceptionPopup("An error occurred while finding saved Image data",
              "The changes made to the image " + serFile + " in the past could not be loaded");
      return null;
    }
    return imageManager;
  }

  /**
   * Stores the given list of independent tags by serializing them in the working directory
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
              "The file could not be saved");
    }
  }

  /**
   * Finds and returns the serialized independent tags list from the working directory, and
   * serializes and returns an empty list if there is no such file
   *
   * @return The list of independent tags stored, or null if it is not found
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

  /**
   * Creates the imagemanagers folder in the working directory
   */
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
   * Finds the serialized ImageManagers in the imagemanagers folder, creating an empty serialized
   * list if there are none, and returns all of the ImageManagers
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
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles != null) {
      for (File file : listOfFiles) {
        imageManagers.add(FileManager.loadImageManager(file));
      }
    }
    return imageManagers;
  }

  /**
   * Gets the number of saved ImageManagers
   * @return The number of saved ImageManagers
   */
  static long getNumberOfImageManagers(){
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
   *
   * @param configMap The map with the settings to be saved in 'key = setting, value = choice' format
   */
  static void storeConfig(HashMap<String, String> configMap) {
    File configFile = new File("config.properties");
    try {
      Properties properties = new Properties();

      //Takes the settings from the config map and puts them in the properties
      for (String key : configMap.keySet()) {
        properties.setProperty(key, configMap.get(key));
      }

      //Writes the files
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
   * @return The settings as a map in the format 'key: setting, value: choice'
   */
  static HashMap<String, String> getConfigDetails() {
    HashMap<String, String> configMap = new HashMap<>();
    File configFile = FileManager.getConfigFile();
    try {
      FileReader reader = new FileReader(configFile);
      Properties properties = new Properties();
      properties.load(reader);

      //Puts the read properties into the map to be returned
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
   * if it cannot be done
   * https://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
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
