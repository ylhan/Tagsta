package app.model;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TagManager {
    private boolean showExtensions;
    private boolean usesThumbnails;
    private ArrayList<ImageManager> listOfImageManagers;
    private boolean isFirstTime;

    public TagManager() {
        if (FileManager.isFirstTime()){
            this.showExtensions = false;
            this.usesThumbnails = false;
            this.listOfImageManagers = new ArrayList<>();
        }
        else{
            this.listOfImageManagers = FileManager.loadImageManagers();
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
        FileManager.saveFiles(this.listOfImageManagers, configMap);
    }

    public ImageManager getImageManager(File file) {
        for (ImageManager imageManager : this.listOfImageManagers){
            if (imageManager.returnPath().toString().equals(file.getPath())){
                return imageManager;
            }
        }
        ImageManager temp = new ImageManager(Paths.get(file.getPath()));
        this.listOfImageManagers.add(temp);
        return temp;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

}
