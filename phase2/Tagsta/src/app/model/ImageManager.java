package app.model;

import app.controller.ExceptionDialogPopup;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ImageManager implements Serializable {

  private ObservableList<String> tags;
  private ObservableList<String> previousNames;
  private long fileNumber;
  private static final long serialVersionUID = 123456789;
  private String name;
  private Path imagePath;

  public ImageManager(Path path) {
    //Gets the current number of ImageManagers in order to create the correct serializable file
    this.fileNumber = FileManager.getNumberOfImageManagers() + 1;
    //Initializes instance variables
    imagePath = path;
    previousNames = FXCollections.observableArrayList(new ArrayList<String>());
    name = imagePath.getFileName().toString();
    //Searches for the last index of the '.' character in order to remove the file extension from the name
    int periodIndex = name.indexOf(".");
    name = name.substring(0, periodIndex);
    //Stores the current name in the log of naming history
    previousNames.add(getDateAndTime() + ", " + name);
    //Parses the tags out of the current name in order to place all the tags into the current tag list
    tags = parseTags(name);
    TagManager.getLogger().log(Level.INFO, "Started the logging of " + name + ".");
  }

  /**
   * * Changes the name of the file to a previous name in previousNames.
   *
   * @param revertedName The String to which the name of the file will be changed to.
   */
  public void revert(String revertedName) {
    //Parses out the tags from the name to be reverted to
    tags = parseTags(revertedName);
    //Parses out the date from the formatting of the name as when this method is called the name is returned with the date formatting
    String parsedRevertedName = revertedName.substring(revertedName.indexOf("M") + 3);
    //Retrieves the total file path and stores it to temp in order to move the file
    Path newPath = getTotalPath(parsedRevertedName);
    FileManager.moveImage(imagePath, newPath);
    imagePath = newPath;
    TagManager.getLogger()
        .log(Level.INFO, "Reverted from the name: " + parsedRevertedName + " to " + name);
    name = parsedRevertedName;
    //Stores name and the date for history storing purposes
    String nameAndDate = getDateAndTime() + ", " + name;
    previousNames.add(nameAndDate);
    FileManager.storeImageManager(this);
  }

  /**
   * * Adds a tag to the file name of the image. Alters the name of the image in directory & adds
   * the tag to the list of tags. Adds the name before changes to the previous names list. Adds a
   * list of tags before changes to the previous tags List.
   *
   * @param tag String to be added to the file name of the image.
   */
  public void addTag(String tag) {
    String tempTag = tag;
    //Trims the tag to remove spaces
    tempTag = tempTag.trim();
    boolean added;
    //Doesn't do anything if the tag only contained spaces
    if (!tempTag.isEmpty()) {
      //Only adds the tag if the tag contains only non special characters
      added = TagManager.isValidTag(new ArrayList<>(this.tags), tempTag);
      //Actions to take if the tag is determined to be added
      if (added) {
        tags.add(tag);
        String newName = name + " @" + tag;
        Path newPath = getTotalPath(newName);
        FileManager.moveImage(imagePath, newPath);
        imagePath = newPath;
        TagManager.getLogger().log(
            Level.INFO,
            "Changed name from: " + name + " to: " + name + " @" + tag + ". By adding tag: " + tag);
        name = newName;
        String nameAndDate = getDateAndTime() + ", " + name;
        previousNames.add(nameAndDate);
      }
    }
    FileManager.storeImageManager(this);
  }

  /**
   * * Removes a tag from the file name of the image. Alters the name of the image in directory &
   * removes the tag from the list of tags. Adds the name before changes to the previous names list.
   * Adds a list of tags before changes to the previous tags List.
   *
   * @param tag String to be removed from the file name of the image.
   */
  public void removeTag(String tag) {
    String tagName = " @" + tag;
    //Stores the current name of the ImageManager for logging purposes
    String previousName = name;
    //Parses out the tag from the name of the ImageManager
    int index = name.indexOf(tagName);
    if (index + tagName.length() + 1 < name.length()) {
      name = name.substring(0, index) + name.substring(index + tagName.length());
    } else {
      name = name.substring(0, index);
    }
    TagManager.getLogger().log(Level.INFO,
        "Changed name from: " + previousName + " to: " + name + ". By removing tag: " + tag);
    String temp = imagePath.toString();
    index = temp.indexOf(tagName);
    temp = temp.substring(0, index) + temp.substring(index + tagName.length());
    FileManager.moveImage(imagePath, Paths.get(temp));
    imagePath = Paths.get(temp);
    tags.remove(tag);
    String nameAndDate = getDateAndTime() + ", " + name;
    previousNames.add(nameAndDate);
    FileManager.storeImageManager(this);
  }

  /**
   * * Returns the path of the file this ImageManager is holding.
   *
   * @return A path to to the image for this ImageManager.
   */
  Path returnPath() {
    return imagePath;
  }

  /**
   * * Returns a list of current tags of this ImageManager.
   *
   * @return The list of current tags.
   */
  public ObservableList<String> getTags() {
    return tags;
  }

  /**
   * * Returns a list of all previous tags of this ImageManager based on the previousNames list.
   *
   * @return A list of all previous tags. (2D array)
   */
  public ObservableList<ObservableList<String>> getPreviousTags() {
    ArrayList<ObservableList<String>> returnList = new ArrayList<>();
    for (String s : previousNames) {
      returnList.add(parseTags(s));
    }
    return FXCollections.observableArrayList(returnList);
  }

  /**
   * * Returns the image for this ImageManager.
   *
   * @return Image of this ImageManager.
   */
  public Image getImage() {
    return new Image("file:" + imagePath.toString());
  }

  /**
   * * Returns the File class representation of the image stored in this ImageManager.
   *
   * @return File object representation of this image.
   */
  public File getFile() {
    return new File(imagePath.toString());
  }

  /**
   * Returns the number that this imageManager will have if turned into a file
   *
   * @return The number of this imageManager
   */
  long getFileNumber() {
    return this.fileNumber;
  }

  /**
   * * Returns a list of previous names of this image.
   *
   * @return List of previous names.
   */
  public ObservableList<String> getPrevNames() {
    return previousNames;
  }

  /**
   * * Writes each variable to be saved by copying their data into a serializable format or writing
   * them as-is
   *
   * @param stream The stream to write to
   * @throws IOException Thrown when the stream is not written to properly
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.writeObject(new ArrayList<>(this.tags));
    stream.writeObject(new ArrayList<>(this.previousNames));
    stream.writeObject(this.name);
    stream.writeObject(this.imagePath.toString());
  }

  /**
   * * Reads each variable in a serialized format and converts it to the class' instance variables
   *
   * @param stream The stream to read from
   * @throws IOException Thrown when the stream is not written to properly
   * @throws ClassNotFoundException Thrown when the class is not found
   */
  @SuppressWarnings("unchecked")
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    tags = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
    previousNames = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
    name = (String) stream.readObject();
    imagePath = Paths.get((String) stream.readObject());
  }

  /**
   * * Helper method used to generate a list of tags from a name String.
   *
   * @param s The string to be parsed.
   * @return A list of tags from a name String.
   */
  static ObservableList<String> parseTags(String s) {
    int index = s.lastIndexOf(File.separator);
    String fileName = s.substring(index + 1);
    int tagIndex = fileName.indexOf(" @");
    ArrayList<String> tempList = new ArrayList<>();
    while (tagIndex != -1) {
      int blankSpaceIndex = fileName.substring(tagIndex + 2).indexOf(" @");
      if (blankSpaceIndex == -1) {
        tempList.add(fileName.substring(tagIndex + 2));
        tagIndex = -1;
        continue;
      }
      tempList.add(fileName.substring(tagIndex + 2, blankSpaceIndex + tagIndex + 2));
      tagIndex = fileName.substring(tagIndex + 2).indexOf(" @") + tagIndex + 2;
    }
    return FXCollections.observableArrayList(tempList);
  }

  /***
   * *Changes the file path of this ImageManager to a new one.
   *
   * @param path The target path of this ImageManager.
   */
  public void updateDirectory(Path path) {
    imagePath = path;
    FileManager.storeImageManager(this);
  }

  /**
   * Returns the name of this ImageManager
   *
   * @return the name of this ImageManager
   */
  @Override
  public String toString() {
    File file = new File(imagePath.toString());
    return file.getName();
  }

  /***
   * Helper method used to gather the new file path with the new name of the file.
   * @param newName The name which is going to replace the current name of the this ImageManager
   * @return The new path of this ImageManager.
   */
  private Path getTotalPath(String newName) {
    String temp = imagePath.toString();
    int separatorIndex = temp.lastIndexOf(File.separator);
    int periodIndex = temp.indexOf(".");
    temp = temp.substring(0, separatorIndex + 1) + newName + temp.substring(periodIndex);
    return Paths.get(temp);

  }

  /***
   * Helper method used to get the current Date and Time.
   * @return The string format of the current date and time.
   */
  private String getDateAndTime() {
    LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();
    return converter.toString(LocalDateTime.now());
  }

}
