package app.model;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * TagManager has a collection of ImageManagers and uses FileManager to do the various functions related to those
 * ImageManagers. It stores configuration setting details that a view can use and can give the view a ImageManager it
 * needs
 */
public class TagManager {
    private boolean showExtensions;
    private boolean usesThumbnails;
    private ArrayList<ImageManager> listOfImageManagers;
    private boolean isFirstTime;

    /**
     * Creates a TagManager. First checks whether this is the first time a TagManager has been created or this
     * program has been run. If so, it sets the configuration settings to some defaults and makes an empty list of
     * ImageManagers. If not, it gets the configuration details and ImageManagers from storage using FileManager
     */
    public TagManager() {
        if (FileManager.isFirstTime()){
            this.showExtensions = false;
            this.usesThumbnails = false;
            this.listOfImageManagers = new ArrayList<>();
            FileManager.saveFiles(this.listOfImageManagers, this.getConfigMap());
        }
        else{
            this.listOfImageManagers = FileManager.loadImageManagers();
            this.setConfigOptions();
        }
    }

    /**
     * Sets the various configuration settings the TagManager has using details gained from FileManager
     */
    private void setConfigOptions(){
        HashMap<String, String> configMap = FileManager.getConfigDetails();
        this.showExtensions = Boolean.parseBoolean(configMap.get("showExtensions"));
        this.usesThumbnails = Boolean.parseBoolean(configMap.get("usesThumbnails"));
    }

    /**
     * Saves the configuration details and ImageManagers to disk using FileManager
     */
    public void saveProgram() {
        FileManager.saveFiles(this.listOfImageManagers, this.getConfigMap());
    }

    /**
     * Returns the map based on the configuration settings set in this TagManager
     * @return The configuration settings in a map with keys as settings and values as the settings' values
     */
    private HashMap<String, String> getConfigMap(){
        HashMap<String, String> configMap= new HashMap<>();
        configMap.put("showExtensions", ((Boolean)this.showExtensions).toString());
        configMap.put("usesThumbnails", ((Boolean)this.usesThumbnails).toString());
        return configMap;
    }

    /**
     * Returns the ImageManager object corresponding to the path of the given file. If such an ImageManager does not
     * exist, it is created and returned
     * @param file The file whose path corresponds to an ImageManager
     * @return The ImageManager that corresponds to the given file's path
     */
    public ImageManager getImageManager(File file) {
        for (ImageManager imageManager : this.listOfImageManagers){
            if (imageManager.returnPath().toString().equals(file.getPath())){
                return imageManager;
            }
        }
        ImageManager temp = new ImageManager(Paths.get(file.getPath()));
        this.listOfImageManagers.add(temp);
        this.saveProgram();
        return temp;
    }

    /**
     * Returns whether this is the first time this program is being run
     * @return Whether this is the first time this program is being run
     */
    public boolean isFirstTime() {
        return isFirstTime;
    }

    /**
     * Returns whether the extensions should be in the name of the ImageManager when one is displayed
     * @return Whether the extensions should be in the name of the ImageManager when one is displayed
     */
    public boolean isShowExtensions() {
        return showExtensions;
    }

    /**
     * Sets whether the extensions should be in the name of the ImageManager when one is displayed
     * @param showExtensions Whether the extensions should be in the name of the ImageManager when one is displayed
     */
    public void setShowExtensions(boolean showExtensions) {
        this.showExtensions = showExtensions;
    }

    /**
     * Returns whether the view displays images in a directory in a classic thumbnail style
     * @return Whether the view displays images in a directory in a classic thumbnail style
     */
    public boolean isUsesThumbnails() {
        return usesThumbnails;
    }

    /**
     * Sets whether the view displays images in a directory in a classic thumbnail style
     * @param usesThumbnails Whether the view displays images in a directory in a classic thumbnail style
     */
    public void setUsesThumbnails(boolean usesThumbnails) {
        this.usesThumbnails = usesThumbnails;
    }
}
