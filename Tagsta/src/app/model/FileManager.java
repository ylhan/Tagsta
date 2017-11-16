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

/**
 * FileManager is responsible for the saving/loading of ImageManagers and the program's configuration file. It is
 * also responsive for moving images on the computer
 */
public class FileManager {

    /**
     * Saves the the list of given ImageManagers and the program's configuration files to the working directory
     * @param imageManagers The list of ImageManagers from TagManager that are to be saved
     */
    public void saveFiles(ArrayList<ImageManager> imageManagers){
        this.storeConfig();
        this.storeImageManagers(imageManagers);
    }

    /**
     * Finds and returns the configuration file from the working directory
     * @return The config file
     */
    private File getConfigFile(){
        Path configPath = Paths.get("config.properties");
        File file = configPath.toFile();
        if (!file.exists()) {
            System.out.println("file does not exist");
            this.storeConfig();
        }
        return file;
    }

    /**
     * Stores the ImageManagers from TagManager by serializing them in the working directory
     * @param imageManagers The list of ImageManagers to be saved
     */
    private void storeImageManagers(ArrayList<ImageManager> imageManagers) {
        try{
            OutputStream file = new FileOutputStream("imageManagers");
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);

            output.writeObject(imageManagers);
            output.close();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Finds the serialized ImageManagers into the working directory, and creates an empty serialized list if there
     * are none, and returns them
     * @return The list of ImageManagers stored
     */
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
            ex.printStackTrace();
            imageManagers = new ArrayList<>();
            this.storeImageManagers(imageManagers);
            return imageManagers;
        }
        catch (ClassNotFoundException ex){
            System.out.println("classpath is broken");
            ex.printStackTrace();
            imageManagers = new ArrayList<>();
            this.storeImageManagers(imageManagers);
            return imageManagers;
        }
        return imageManagers;
    }

    /**
     * Stores the config file into the working directory
     */
    private void storeConfig() {
        File configFile = new File("config.properties");
        try {
            Properties properties = new Properties();
            properties.setProperty("displayExtensions", "TagManager.displaysExtensions()");
            properties.setProperty("showsThumbnails", "TagManager.showsThumbnails()");
            FileWriter writer = new FileWriter(configFile);
            properties.store(writer, "configuration settings");
            writer.close();
        }
        catch (IOException ex) {
            System.out.println("IO error");
            ex.printStackTrace();
        }
    }

    /**
     * Loads the config file from the system and returns a HashMap of its contents
     * @return The settings, each corresponding to a boolean value in String form
     */
    public HashMap<String, String> getConfigDetails(){
        HashMap<String, String> configMap = new HashMap<>();
        File configFile = this.getConfigFile();
        try {
            FileReader reader = new FileReader(configFile);
            Properties properties = new Properties();
            properties.load(reader);
            configMap.put("displayExtensions", properties.getProperty("displayExtensions"));
            configMap.put("showsThumbnails", properties.getProperty("showsThumbnails"));
            reader.close();
        }
        catch (IOException ex) {
            System.out.println("IO error");
            ex.printStackTrace();
        }
        return configMap;
    }

    /**
     * Moves an image from the current location to a new location, replacing a file if one is already there in the
     * nnew location
     * @param currentPath The current path of the image
     * @param newPath The new path to move the image to
     */
    public void moveImage(Path currentPath, Path newPath) {
        try {
            Files.move(currentPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex){
            ex.printStackTrace();
            System.out.println("file could not be moved");
        }
    }
}
