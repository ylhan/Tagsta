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

/**
 * FileManager is responsible for the saving/loading of ImageManagers and the program's
 * configuration file. It is also responsive for moving images on the computer
 */
public class FileManager {

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
      configMap.put("theme", "light");
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
      Path path = Paths.get("imagemanagers");
      Files.createDirectories(path);
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
              //"The file could not be saved");
          ex.getMessage());
    }
  }

  /**
   * Finds the serialized ImageManager corresponding to the given file number and returns it. if
   * there is no such ImageManager, returns null
   *
   * @param fileNumber The number of the ImageManager to be gotten
   * @return The ImageManager corresponding to the given id and null if no such ImageManager exists
   */
  @SuppressWarnings("unchecked")
  private static ImageManager loadImageManager(long fileNumber) {
    ImageManager imageManager;
    try {
      InputStream file = new FileInputStream("imagemanagers" + File.separator +
          fileNumber + ".ser");
      InputStream buffer = new BufferedInputStream(file);
      ObjectInput input = new ObjectInputStream(buffer);

      imageManager = (ImageManager) input.readObject();
      input.close();
    } catch (IOException ex) {
      ExceptionDialogPopup.createExceptionPopup("An error occurred while loading Image data",
          "The desired image could not be found or loaded");
      return null;
    } catch (ClassNotFoundException ex) {
      ExceptionDialogPopup
          .createExceptionPopup("An error occurred while finding saved Image data",
              "The changes made to the image in the past could not be loaded");
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
    } catch (IOException ex) {
      ExceptionDialogPopup.createExceptionPopup("An error occurred while loading the independent tags list",
          "The changes made to the tags list in past sessions could not be loaded");
      listOfTags = new ArrayList<>();
      FileManager.storeTagsList(listOfTags);
    } catch (ClassNotFoundException ex) {
      ExceptionDialogPopup
          .createExceptionPopup("An error occurred while finding the saved list of independent tags",
              "The changes made to the tags list in past sessions could not be loaded");
      listOfTags = new ArrayList<>();
      FileManager.storeTagsList(listOfTags);
    }
    return listOfTags;
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
    File file = new File("imagemanagers");
    File[] fileList = file.listFiles();
    int numberOfFiles;
    if (fileList == null){
      numberOfFiles = 0;
    }
    else {
      numberOfFiles = fileList.length;
    }
    for(int i = 0; i < numberOfFiles; i++){
      imageManagers.add(FileManager.loadImageManager(i));
    }
    return imageManagers;
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
