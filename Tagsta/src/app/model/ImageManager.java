package app.model;

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
    private String name;
    private Path imagePath;

    public ImageManager(Path path) {
        imagePath = path;
        previousNames = FXCollections.observableArrayList(new ArrayList<String>());
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

    public void revert(String revertedName) {
        tags = parseTags(revertedName);
        String parsedRevertedName = revertedName.substring(revertedName.indexOf("M") + 3);
        String temp = imagePath.toString();
        int index = temp.lastIndexOf(File.separator);
        temp = temp.substring(0, index + 1) + parsedRevertedName + temp.substring(temp.length() - 4);
        FileManager.moveImage(imagePath, Paths.get(temp));
        imagePath = Paths.get(temp);
        LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();
        String current = converter.toString(LocalDateTime.now());
        name = parsedRevertedName;
        String nameAndDate = current + ", " + name;
        previousNames.add(nameAndDate);
    }

    /**
     * * Adds a tag to the file name of the image. Alters the name of the image in directory & adds
     * the tag to the list of tags. Adds the name before changes to the previous names list. Adds a
     * list of tags before changes to the previous tags List.
     *
     * @param tag String to be added to the file name of the image.
     */
    public void addTag(String tag) {
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
        previousNames.add(nameAndDate);
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
        previousNames.add(nameAndDate);
    }

    public Path returnPath() {
        return imagePath;
    }

    public ObservableList<String> getTags() {
        return tags;
    }

    public ObservableList<ObservableList<String>> getPreviousTags() {
        ArrayList<ObservableList<String>> returnList = new ArrayList<ObservableList<String>>();
        for(String s: previousNames) {
            returnList.add(parseTags(s));
        }
        return FXCollections.observableArrayList(returnList);
    }

    public Image getImage() {
        return new Image("file:" + imagePath.toString());
    }

    public File getFile() {
        return new File(imagePath.toString());
    }

    public ObservableList<String> getPrevNames() {
        return previousNames;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(new ArrayList<String>(this.tags));
        stream.writeObject(new ArrayList<String>(this.previousNames));
        stream.writeObject(this.name);
        stream.writeObject(this.imagePath.toString());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        tags = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
        previousNames = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
        name = (String) stream.readObject();
        imagePath = Paths.get((String) stream.readObject());
    }

    private ObservableList<String> parseTags(String s) {
        int index = s.lastIndexOf(File.separator);
        String fileName = s.substring(index + 1);
        int tagIndex = fileName.indexOf(" @");
        ArrayList<String> tempList = new ArrayList<String>();
        while(tagIndex != -1) {
            int blankSpaceIndex = fileName.substring(tagIndex + 2).indexOf(" ");
            if(blankSpaceIndex == -1) {
                tempList.add(fileName.substring(tagIndex + 2));
                tagIndex = -1;
                continue;
            }
            tempList.add(fileName.substring(tagIndex + 2, blankSpaceIndex + tagIndex + 2));
            tagIndex = fileName.substring(tagIndex + 2).indexOf(" @") + tagIndex + 2;
        }
        return FXCollections.observableArrayList(tempList);
    }
}
