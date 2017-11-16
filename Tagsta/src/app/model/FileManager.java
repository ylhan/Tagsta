package app.model;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {
    private Path configPath;

    public FileManager() throws IOException{
        this.loadConfig();
    }

    public void saveFiles(ArrayList<ImageManager> imageManagers){
        this.storeConfig();
        this.storeImageManagers(imageManagers);
    }

    private Path getConfigFile() throws IOException{
        Path configPath = Paths.get("config.xml");
        File file = configPath.toFile();
        if (!file.exists()) {
            System.out.println("file does not exist");
            this.storeConfig();
        }
        return configPath;
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

    }

    private void loadConfig() throws IOException{
        this.configPath = this.getConfigFile();
    }

    public void moveImage(Path currentPath, Path newPath) {

    }
}
