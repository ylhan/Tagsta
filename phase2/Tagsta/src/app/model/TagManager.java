package app.model;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.controller.ExceptionDialogPopup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * TagManager has a collection of ImageManagers and uses FileManager to do the various functions
 * related to those ImageManagers. It stores configuration setting details that a view can use and
 * can give the view a ImageManager it needs. It also has a list of independent tags not associated
 * to any one image.
 */
public class TagManager {

  private HashMap<String, String> configOptions;
  private ArrayList<ImageManager> listOfImageManagers;
  private ArrayList<String> listOfTags;
  private static Logger logger = Logger.getLogger("Tagsta");

  /**
   * Creates a TagManager. First checks whether this is the first time a TagManager has been created
   * or this program has been run. If so, it sets the configuration settings to some defaults and
   * makes an empty list of ImageManagers and tags. If not, it gets the configuration details,
   * ImageManagers, and tags list from storage using FileManager
   */
  public TagManager() {
    this.configOptions = FileManager.getConfigDetails();
    this.listOfImageManagers = FileManager.loadImageManagers();
    this.listOfTags = FileManager.loadTagsList();
    FileManager.createLogHandler();
    //Sets the default config options.
    logger.setUseParentHandlers(false);
    logger.addHandler(FileManager.getLogHandler());
    logger.setLevel(Level.INFO);
  }

  /**
   * Adds all tags from a given file name into the independent tags list
   * @param file The file name with tags to add
   */
  public void addIndependentTag(File file){
    //Removes the file extension from the file name
    String fileName = file.getPath();
    fileName = fileName.substring(0, fileName.lastIndexOf("."));
    ObservableList<String> tagList;
    //Calls helper method to parse the tags
    tagList = ImageManager.parseTags(fileName);

    //If the tags are already in tagList then do not add them to the list
    for(String tag: tagList) {
      if(!this.listOfTags.contains(tag))
        this.addIndependentTag(tag);
    }
  }

  /**
   * Adds a tag to the independent tags list, without repeating tags in the independent tags list
   * @param tag The file name with tags to add
   */
  public void addIndependentTag(String tag){
    //Trims the tag to remove spaces
    tag = tag.trim();
    if(TagManager.isValidTag(this.listOfTags, tag)) {
        listOfTags.add(tag);
      }
    FileManager.storeTagsList(this.listOfTags);
  }

  /**
   * Returns the ImageManagers in TagManager
   * @return The list of ImageManagers
   */
  public ObservableList<ImageManager> getImageManagers() {
    //JavaFX requires observable list to display
    return FXCollections.observableArrayList(listOfImageManagers);
  }

  static Logger getLogger() {
    return logger;
  }

  /**
   * Deletes the given tag from the list of independent tags
   * @param tag The tag to delete
   */
  public void deleteIndependentTag(String tag) {
    listOfTags.remove(tag);
    //Updates the list of tags save file
    FileManager.storeTagsList(this.listOfTags);
  }

  /**
   * Returns the list of independent tags as an ObservableList
   * @return The list of independent tags
   */
  public ObservableList<String> getTagsList(){
    //JavaFX requires observableLists for displaying.
    return FXCollections.observableArrayList(this.listOfTags);
  }

  /**
   * Gets the value to the given config option in TagManager and null if there is no such option in
   * the config settings
   *
   * @param option The config option whose value is desired
   * @return The value of the config option, and null if there is no such option in the config
   * settings
   */
  public String getConfigOption(String option) {
    //Gets all config options.
    for (String key : this.configOptions.keySet()) {
      if (key.equals(option)) {
        return this.configOptions.get(key);
      }
    }
    //If they do not exist yet return null so the program knows to create them.
    return null;
  }

  /**
   * Sets the value to the given config option in TagManager and does nothing if there is no such
   * option in the config settings
   *
   * @param option The config option whose value is to be set
   * @param value The value to be set
   */
  public void setConfigOption(String option, String value) {
    //Stores all config options.
    for (String key : this.configOptions.keySet()) {
      if (key.equals(option)) {
        this.configOptions.put(key, value);
      }
    }
    FileManager.storeConfig(this.configOptions);
  }

  /**
   * Returns the ImageManager object corresponding to the path of the given file. If such an
   * ImageManager does not exist, it is created and returned
   *
   * @param file The file whose path corresponds to an ImageManager
   * @return The ImageManager that corresponds to the given file's path
   */
  public ImageManager getImageManager(File file) {
    //If the imageManager is already in the list of ImageMangers return it instead of creating new one.
    for (ImageManager imageManager : this.listOfImageManagers) {
      if (imageManager.returnPath().toString().equals(file.getPath())) {
        return imageManager;
      }
    }
    //If imageManager doesn't exist yet, create new one.
    ImageManager temp = new ImageManager(Paths.get(file.getPath()));
    this.listOfImageManagers.add(temp);
    FileManager.storeImageManager(temp);
    return temp;
  }

  static boolean isValidTag(ArrayList<String> list, String tag){
    //Only accepts non special characters for file name.
    boolean valid = tag.matches("^[a-zA-Z0-9_ ]*$");
    if (!valid) {
      ExceptionDialogPopup
              .createExceptionPopup("Error adding tag", "That tag contains an illegal character");
    }
    //Checks whether or not the the tag has already been added to the current iteration of tags
    if (list.contains(tag) ) {
      ExceptionDialogPopup
              .createExceptionPopup("Error adding tag",
                      "The list of current tags already contains the tag: " + tag);
      valid = false;
    }
    return valid;
  }
}
