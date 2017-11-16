package app.model;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class TagManager {
    private boolean showExtensions;
    private boolean usesThumbnails;
    private ArrayList<ImageManager> listOfImages;
    private FileManager fileManager;
    private boolean isFirstTime;

    public TagManager() {
        this.fileManager = new FileManager();
        this.setConfigOptions();
        this.listOfImages = this.fileManager.loadImageManagers();
        this.isFirstTime = true;
    }

    private void setConfigOptions(){
        HashMap<String, String> configMap = this.fileManager.getConfigDetails();
        this.showExtensions = Boolean.parseBoolean(configMap.get("showExtensions"));
        this.usesThumbnails = Boolean.parseBoolean(configMap.get("usesThumbnails"));
    }

    public void startProgram(){

    }

    public void closeProgram() {

    }

    public ImageManager getImage(File file) {
        for (ImageManager imageManager : this.listOfImages){
            if (imageManager.returnPath().equals(file.getPath())){
                return imageManager;
            }
        }
        return null;
    }


}
