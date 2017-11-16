package app.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ImageManager {
  private ObservableList<String> tags;
  private ObservableList<ObservableList<String>> previousTags;
  private ObservableList<String> previousNames;
  private String name;
  private Path imagePath;

  public ImageManager(Path path) {
    imagePath = path;
    previousNames = FXCollections.observableArrayList(new ArrayList<String>());
    previousTags = FXCollections.observableArrayList(new ArrayList<ObservableList<String>>());
    name = imagePath.getFileName().toString();
    name = name.substring(0, name.length() - 4);
    tags = FXCollections.observableArrayList(new ArrayList<String>());
  }
  /**
   * * Returns the file name of the image (currently without extension)
   *
   * @return the file name of the image without file extension.
   */
  public String getName() {
    return name;
  }

  /**
   * * Adds a tag to the file name of the image. Alters the name of the image in directory & adds
   * the tag to the list of tags. Adds the name before changes to the previous names list. Adds a
   * list of tags before changes to the previous tags List.
   *
   * @param tag String to be added to the file name of the image.
   */
  public void addTag(String tag) {
    previousNames.add(name);
    previousTags.add(tags);
    tags.add("@" + tag);
    String temp = imagePath.toString();
    int index = temp.lastIndexOf("\\");
    temp =
        temp.substring(0, index + name.length() + 1)
            + " @"
            + tag
            + temp.substring(temp.length() - 4);
    FileManager.moveImage(imagePath, Paths.get(temp));
    imagePath = Paths.get(temp);
    previousNames.add(name);
    name = name + " @" + tag;
  }

  /**
   * * Removes a tag from the file name of the image. Alters the name of the image in directory &
   * removes the tag from the list of tags. Adds the name before changes to the previous names list.
   * Adds a list of tags before changes to the previous tags List.
   *
   * @param tag String to be removed from the file name of the image.
   */
  public void removeTag(String tag) {
    previousTags.add(tags);
    previousNames.add(name);
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
    tags.remove(tagName.substring(1));
  }

  public Path returnPath() {
    return imagePath;
  }

  public ObservableList<String> getTags() {
    return tags;
  }

  public Image getImage() {
    return new Image("file:" + imagePath.toString());
  }
}
