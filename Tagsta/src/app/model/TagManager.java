package app.model;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TagManager {
    private boolean showExtensions;
    private boolean usesThumbnails;
    private ArrayList<ImageManager> listOfImages;
    private boolean isFirstTime;

    public TagManager() {
        if (FileManager.isFirstTime()){
            this.showExtensions = false;
            this.usesThumbnails = false;
            this.listOfImages = new ArrayList<>();
        }
        else{
            this.listOfImages = FileManager.loadImageManagers();
            this.setConfigOptions();
        }
    }

    private void setConfigOptions(){
        HashMap<String, String> configMap = FileManager.getConfigDetails();
        this.showExtensions = Boolean.parseBoolean(configMap.get("showExtensions"));
        this.usesThumbnails = Boolean.parseBoolean(configMap.get("usesThumbnails"));
    }

    public void closeProgram() {
        HashMap<String, String> configMap= new HashMap<>();
        configMap.put("showExtensions", ((Boolean)this.showExtensions).toString());
        configMap.put("usesThumbnails", ((Boolean)this.usesThumbnails).toString());
        FileManager.saveFiles(this.listOfImages, configMap);
    }

    public ImageManager getImage(File file) {
        for (ImageManager imageManager : this.listOfImages){
            if (imageManager.returnPath().toString().equals(file.getPath())){
                return imageManager;
            }
        }
        ImageManager temp = new ImageManager(Paths.get(file.getPath()));
        this.listOfImages.add(temp);
        return temp;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

}
