package app.model;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileManager {

    public void saveFiles(ArrayList<ImageManager> imageManagers){
        this.storeConfig();
        this.storeImageManagers(imageManagers);
    }

    private File getConfigFile(){
        Path configPath = Paths.get("config.properties");
        File file = configPath.toFile();
        if (!file.exists()) {
            System.out.println("file does not exist");
            this.storeConfig();
        }
        return file;
    }

    private void storeImageManagers(ArrayList<ImageManager> imageManagers) {
        try{
            OutputStream file = new FileOutputStream("imageManagers");
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);

            output.writeObject(imageManagers);
            output.close();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public ArrayList<ImageManager> loadImageManagers() {
        ArrayList<ImageManager> imageManagers;
        try {
            InputStream file = new FileInputStream("imageManagers");
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            imageManagers = (ArrayList<ImageManager>) input.readObject();
            input.close();
        } catch (IOException ex) {
            System.out.println("io exception");
            imageManagers = new ArrayList<>();
            this.storeImageManagers(imageManagers);
            return imageManagers;
        }
        catch (ClassNotFoundException ex){
            System.out.println("classpath is broken");
            imageManagers = new ArrayList<>();
            this.storeImageManagers(imageManagers);
            return imageManagers;
        }
        return imageManagers;
    }

    private void storeConfig() {
        File configFile = new File("config.properties");
        try {
            Properties properties = new Properties();
            properties.setProperty("displayExtensions", "TagManager.displaysExtensions()");
            properties.setProperty("showsThumbnails", "TagManager.showsThumbnails()");
            properties.setProperty("sortsBy", "TagManager.sortType()");
            FileWriter writer = new FileWriter(configFile);
            properties.store(writer, "configuration settings");
            writer.close();
        }
        catch (IOException ex) {
            System.out.println("IO error");
        }
    }

    public HashMap<String, String> getConfigDetails(){
        HashMap<String, String> configMap = new HashMap<>();
        File configFile = this.getConfigFile();
        try {
            FileReader reader = new FileReader(configFile);
            Properties properties = new Properties();
            properties.load(reader);
            configMap.put("displayExtensions", properties.getProperty("displayExtensions"));
            configMap.put("showsThumbnails", properties.getProperty("showsThumbnails"));
            configMap.put("sortsBy", properties.getProperty("sortsBy"));
            reader.close();
        }
        catch (IOException ex) {
            System.out.println("IO error");
        }
        return configMap;
    }

    public void moveImage(Path currentPath, Path newPath) {
        try {
            Files.move(currentPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex){
            System.out.println("file could not be moved");
        }
    }
}
