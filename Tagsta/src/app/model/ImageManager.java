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
    private ObservableList<String> log;
    private static final long serialVersionUID = 123456789;
    private String name;
    private Path imagePath;

    public ImageManager(Path path) {
        imagePath = path;
        previousNames = FXCollections.observableArrayList(new ArrayList<String>());
        name = imagePath.getFileName().toString();
        name = name.substring(0, name.length() - 4);
        LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();
        String current = converter.toString(LocalDateTime.now());
        previousNames.add(current + ", " + name);
        this.log = FXCollections.observableArrayList(new ArrayList<>());
        tags = parseTags(name);
    }

    /***
     * Changes the name of the file to a previous name in previousNames.
     * @param revertedName The String to which the name of the file will be changed to.
     */
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
        this.log.add("Reverted to name" + " " + revertedName + " at " + current);
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
        this.log.add("Added the tag " + tag + " at " + current);
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
        this.log.add("Removed the tag " + tag + " at " + current);
        previousNames.add(nameAndDate);
    }

    /***
     * Returns the path of the file this ImageManager is holding.
     * @return A path to to the image for this ImageManager.
     */
    public Path returnPath() {
        return imagePath;
    }


    /***
     * Returns a list of current tags of this ImageManager.
     * @return The list of current tags.
     */
    public ObservableList<String> getTags() {
        return tags;
    }


    /***
     * Returns a list of all previous tags of this ImageManager based on the previousNames list.
     * @return A list of all previous tags. (2D array)
     */
    public ObservableList<ObservableList<String>> getPreviousTags() {
        ArrayList<ObservableList<String>> returnList = new ArrayList<ObservableList<String>>();
        for (String s : previousNames) {
            returnList.add(parseTags(s));
        }
        return FXCollections.observableArrayList(returnList);
    }


    /***
     * Returns the image for this ImageManager.
     * @return Image of this ImageManager.
     */
    public Image getImage() {
        return new Image("file:" + imagePath.toString());
    }


    /***
     * Returns the File class representation of the image stored in this ImageManager.
     * @return File object representation of this image.
     */
    public File getFile() {
        return new File(imagePath.toString());
    }


    /***
     * Returns a list of previous names of this image.
     * @return List of previous names.
     */
    public ObservableList<String> getPrevNames() {
        return previousNames;
    }


    /***
     * Helper method used to save data.
     * @param stream
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(new ArrayList<String>(this.tags));
        stream.writeObject(new ArrayList<String>(this.previousNames));
        stream.writeObject(this.name);
        stream.writeObject(this.imagePath.toString());
        stream.writeObject(new ArrayList<String>(this.log));
    }


    /***
     * Helper method used to load data.
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        tags = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
        previousNames = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
        name = (String) stream.readObject();
        imagePath = Paths.get((String) stream.readObject());
        log = FXCollections.observableArrayList((ArrayList<String>) stream.readObject());
    }

    /***
     * Helper method used to generate a list of tags from a name String.
     * @param s The string to be parsed.
     * @return A list of tags from a name String.
     */
    private ObservableList<String> parseTags(String s) {
        int index = s.lastIndexOf(File.separator);
        String fileName = s.substring(index + 1);
        int tagIndex = fileName.indexOf(" @");
        ArrayList<String> tempList = new ArrayList<String>();
        while (tagIndex != -1) {
            int blankSpaceIndex = fileName.substring(tagIndex + 2).indexOf(" ");
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

    public void updateDirectory(Path path) {
        imagePath = path;
    }

    public ObservableList<String> getLog() {
        return log;
    }
}
