package app.model;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

  /**
   * Creates a TagManager. First checks whether this is the first time a TagManager has been created
   * or this program has been run. If so, it sets the configuration settings to some defaults and
   * makes an empty list of ImageManagers and tags. If not, it gets the configuration details,
   * ImageManagers, and tags list from storage using FileManager
   */
  public TagManager() {
    this.configOptions = FileManager.getConfigDetails();
    if (FileManager.isFirstTime()) {
      this.listOfImageManagers = new ArrayList<>();
      this.listOfTags = new ArrayList<>();
      FileManager.saveFiles(this.listOfImageManagers, this.configOptions, this.listOfTags);
    } else {
      this.listOfImageManagers = FileManager.loadImageManagers();
      this.listOfTags = FileManager.loadTagsList();
    }
  }

  /**
   * Adds a tag to the independent tags list
   * @param tag The tag to add
   */
  public void addIndependentTag(String tag){
    this.listOfTags.add(tag);
  }

  /**
   * Returns the list of independent tags as an ObservableList
   * @return The list of independent tags
   */
  public ObservableList<String> getTagsList(){
    return FXCollections.observableArrayList(this.listOfTags);
  }

  /**
   * Saves the configuration details, the tags list, and ImageManagers to disk using FileManager
   */
  public void saveProgram() {
    FileManager.saveFiles(this.listOfImageManagers, this.configOptions,
                          this.listOfTags);
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
    this.saveProgram();
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
        this.saveProgram();
        return imageManager;
      }
    }
    ImageManager temp = new ImageManager(Paths.get(file.getPath()));
    this.listOfImageManagers.add(temp);
    this.saveProgram();
    return temp;
  }
}
