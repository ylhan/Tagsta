package app.model;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.collections.*;
import javafx.scene.image.Image;
import java.io.File;
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

    public void rename(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void addTag(String tag) {
        previousNames.add(name);
        previousTags.add(tags);
        tags.add("@" + tag);
        String temp = imagePath.toString();
        int index = temp.lastIndexOf("\\");
        temp = temp.substring(0, index + name.length() + 1) + " @" + tag + temp.substring(temp.length() - 4);
        new File(imagePath.toString()).renameTo(new File(temp));
        File imageFile = new File(temp);
        imagePath = Paths.get(imageFile.getAbsolutePath());
        previousNames.add(name);
        name = name + " @" + tag;
    }

    public void removeTag(String tag) {
        previousTags.add(tags);
        previousNames.add(name);
        String tagName = " @" + tag;
        int index = name.indexOf(tagName);
        if(index + tagName.length() + 1 < name.length()) {
            name = name.substring(0, index) + name.substring(index + tagName.length());
        }
        else {
            name = name.substring(0, index);
        }
        String temp = imagePath.toString();
        index = temp.indexOf(tagName);
        temp = temp.substring(0, index) + temp.substring(index + tagName.length());
        new File(imagePath.toString()).renameTo(new File(temp));
        File imageFile = new File(temp);
        imagePath = Paths.get(imageFile.getAbsolutePath());
        tags.remove(tagName);
    }

    public Path returnPath() {
        return imagePath;
    }

    public ObservableList<String> getTags() {
        return tags;
    }

    public Image getImage() {
        return new Image(imagePath.toString());
    }
}
