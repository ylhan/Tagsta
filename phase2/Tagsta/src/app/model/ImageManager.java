package app.model;

import app.controller.ExceptionDialogPopup;
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
  private ObservableList<String> log;
  private static long fileTrack;
  private long fileNumber;
  private static final long serialVersionUID = 123456789;
  private String name;
  private Path imagePath;

  ImageManager(Path path) {
    fileNumber = fileTrack++;
    System.out.println("File number" + fileNumber);
    System.out.println(fileTrack);
    imagePath = path;
    previousNames = FXCollections.observableArrayList(new ArrayList<String>());
    name = imagePath.getFileName().toString();
    int periodIndex = name.indexOf(".");
    name = name.substring(0, periodIndex);
    LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();
    String current = converter.toString(LocalDateTime.now());
    previousNames.add(current + ", " + name);
    this.log = FXCollections.observableArrayList(new ArrayList<>());
    tags = parseTags(name);
    if (!tags.isEmpty()) {
      String logMessage = "Detected the following tags: ";
      StringBuilder builder = new StringBuilder(logMessage);
      boolean firstTime = true;
      for (String s : tags) {
        if (!firstTime) {
          builder.append(", ");
        }
        builder.append(s);
        if (firstTime) {
          firstTime = false;
        }
      }
      this.log.add(builder.toString());
    }
    FileManager.saveImageManager(this);
  }

  /**
   * * Changes the name of the file to a previous name in previousNames.
   *
   * @param revertedName The String to which the name of the file will be changed to.
   */
  public void revert(String revertedName) {
    tags = parseTags(revertedName);
    String parsedRevertedName = revertedName.substring(revertedName.indexOf("M") + 3);
    String temp = imagePath.toString();
    int index = temp.lastIndexOf(File.separator);
    int periodIndex = temp.indexOf(".");
    temp = temp.substring(0, index + 1) + parsedRevertedName + temp.substring(periodIndex);
    FileManager.moveImage(imagePath, Paths.get(temp));
    imagePath = Paths.get(temp);
    LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();
    String current = converter.toString(LocalDateTime.now());
    this.log.add("Reverted to name: " + parsedRevertedName + ", from: " + name + ", at " + current);
    name = parsedRevertedName;
    String nameAndDate = current + ", " + name;

    previousNames.add(nameAndDate);
    FileManager.saveImageManager(this);
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
    tempTag = tempTag.trim();
    if (!tempTag.isEmpty()) {
      String invalidChar = "/\\:*?|<>\"@";
      boolean add = true;
      for (char c : invalidChar.toCharArray()) {
        if (tempTag.contains(Character.toString(c))) {
          add = false;
          ExceptionDialogPopup.createExceptionPopup("Error adding tag",
              "TagController cannot contain the following characters: \n / \\ : * ? | < > \" @");
          break;
        }
      }
      if (tags.contains(tempTag)) {
        ExceptionDialogPopup
            .createExceptionPopup("Error adding tag", "Image already contains this tag!");
        add = false;
      }
      if (add) {
        tags.add(tag);
        String temp = imagePath.toString();
        int index = temp.lastIndexOf(File.separator);
        temp =
            temp.substring(0, index + name.length() + 1)
                + " @"
                + tag
                + temp.substring(temp.length() - 4);
        FileManager.moveImage(imagePath, Paths.get(temp));
        imagePath = Paths.get(temp);
        name = name + " @" + tag;
        LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();
        String current = converter.toString(LocalDateTime.now());
        String nameAndDate = current + ", " + name;
        this.log.add("Added the tag: " + tag + " at " + current);
        previousNames.add(nameAndDate);
      }
    }
    FileManager.saveImageManager(this);
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
    int index = name.indexOf(tagName);
    if (index + tagName.length() + 1 < name.length()) {
      name = name.substring(0, index) + name.substring(index + tagName.length());
    } else {
      name = name.substring(0, index);
    }
    String temp = imagePath.toString();
    index = temp.indexOf(tagName);
    temp = temp.substring(0, index) + temp.substring(index + tagName.length());
    FileManager.moveImage(imagePath, Paths.get(temp));
    imagePath = Paths.get(temp);
    tags.remove(tag);
    LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();
    String current = converter.toString(LocalDateTime.now());
    String nameAndDate = current + ", " + name;
    this.log.add("Removed the tag: " + tag + " at " + current);
    previousNames.add(nameAndDate);
    FileManager.saveImageManager(this);
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
   * @return The number of this imageManager
   */
  long getFileNumber(){
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
    stream.writeObject(new ArrayList<>(this.log));
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
    log = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
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
    FileManager.saveImageManager(this);
  }

  /***
   * *Returns the log of changes of this ImageManager.
   *
   * @return The log of changes being returned.
   */
  public ObservableList<String> getLog() {
    return log;
  }

  /**
   * Returns the name of this ImageManager
   * @return the name of this ImageManager
   */
  @Override
  public String toString(){
    String s = imagePath.toString();
    int index = s.lastIndexOf(".");
    return name + s.substring(index + 1);
  }
}
