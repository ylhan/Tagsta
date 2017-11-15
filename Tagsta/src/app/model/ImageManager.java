package app.model;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.collections.*;

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
        String temp = imagePath.getFileName().toString();
        int index = temp.lastIndexOf("\\");
        name = "@" + temp.substring(index + 1 ,temp.length() - 3);
        tags = FXCollections.observableArrayList(new ArrayList<String>());
        tags.add(name);
    }

    public void rename(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void addTag(String tag) {
        name = name + "@" + tag;
        tags.add(tag);
    }

    public void removeTag(String tag) {
        previousTags.add(tags);
        previousNames.add(name);
        String tagName = "@" + tag;
        int index = name.indexOf(tagName);
        name = name.substring(0, index) + name.substring(index + tagName.length() + 1);
        tags.remove(tagName);
    }

    public Path returnPath() {
        return imagePath;
    }
}
