package app.model;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    logger.setUseParentHandlers(false);
    logger.addHandler(FileManager.getLogHandler());
    logger.setLevel(Level.INFO);
  }

  /**
   * Adds all tags from a given file name into the independent tags list
   * @param file The file name with tags to add
   */
  public void addIndependentTag(File file){
    String fileName = file.getPath();
    ObservableList<String> tagList;
    tagList = ImageManager.parseTags(fileName);
    for(String tag: tagList) {
      this.addIndependentTag(tag);
    }
  }

  /**
   * Adds a tag to the independent tags list, without repeating tags in the independent tags list
   * @param tag The file name with tags to add
   */
  public void addIndependentTag(String tag){
    if(!listOfTags.contains(tag)) {
        listOfTags.add(tag);
      }
    FileManager.storeTagsList(this.listOfTags);
  }

  /**
   * Returns the ImageManagers in TagManager
   * @return The list of ImageManagers
   */
  public ObservableList<ImageManager> getImageManagers() {
    return FXCollections.observableArrayList(listOfImageManagers);
  }

  public static Logger getLogger() {
    return logger;
  }

  /**
   * Deletes the given tag from the list of independent tags
   * @param tag The tag to delete
   */
  public void deleteIndependentTag(String tag) {
    listOfTags.remove(tag);
    FileManager.storeTagsList(this.listOfTags);
  }

  /**
   * Returns the list of independent tags as an ObservableList
   * @return The list of independent tags
   */
  public ObservableList<String> getTagsList(){
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
    for (String key : this.configOptions.keySet()) {
      if (key.equals(option)) {
        return this.configOptions.get(key);
      }
    }
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
    for (ImageManager imageManager : this.listOfImageManagers) {
      if (imageManager.returnPath().toString().equals(file.getPath())) {
        return imageManager;
      }
    }
    ImageManager temp = new ImageManager(Paths.get(file.getPath()));
    this.listOfImageManagers.add(temp);
    FileManager.storeImageManager(temp);
    return temp;
  }

}
