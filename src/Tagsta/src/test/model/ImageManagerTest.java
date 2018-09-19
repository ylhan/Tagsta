package test.model;
import app.model.ImageManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageManagerTest {

  private ImageManager getImageManager() throws URISyntaxException{
    Path path = Paths.get(getClass().getResource("../resources/pic @arbitrary.png").toURI());
    return new ImageManager(path);
  }

  @Test
  void ImageManagerTagFromNameTest() throws URISyntaxException,FileNotFoundException{
    ImageManager test = this.getImageManager();
    assertEquals("arbitrary", test.getTags().get(0));
    File file = new File("imagemanagers" + File.separator +
        test.getFileNumber() + ".ser");
    file.delete();
  }

  @Test
  void ImageManagerPreviousNamesTest() throws URISyntaxException{
    ImageManager test = this.getImageManager();
    assertEquals(1, test.getPrevNames().size());
    assertEquals("pic @arbitrary.png", test.toString());
    File file = new File("imagemanagers" + File.separator +
        test.getFileNumber() + ".ser");
    file.delete();
  }

  @Test
  void ImageManagerAddTagsTest() throws URISyntaxException{
    ImageManager test = this.getImageManager();
    test.addTag("test");
    assertEquals(2, test.getPrevNames().size());
    assertEquals("pic @arbitrary @test.png", test.toString());
    test.removeTag("test");
    File file = new File("imagemanagers" + File.separator +
        test.getFileNumber() + ".ser");
    file.delete();
  }

  @Test
  void ImageManagerRemoveTagsTest() throws URISyntaxException{
    ImageManager test = this.getImageManager();
    test.removeTag("arbitrary");
    assertEquals(2, test.getPrevNames().size());
    assertEquals("pic.png", test.toString());
    test.addTag("arbitrary");
    File file = new File("imagemanagers" + File.separator +
        test.getFileNumber() + ".ser");
    file.delete();
  }

  @Test
  void ImageManagerRevertTest() throws URISyntaxException{
    ImageManager test = this.getImageManager();
    test.removeTag("arbitrary");
    test.revert(test.getPrevNames().get(0));
    assertEquals(3, test.getPrevNames().size());
    assertEquals("pic @arbitrary.png", test.toString());
    File file = new File("imagemanagers" + File.separator +
        test.getFileNumber() + ".ser");
    file.delete();
  }

  @Test
  void ImageManagerToStringTest() throws URISyntaxException{
    ImageManager test = this.getImageManager();
    assertEquals("pic @arbitrary.png", test.toString());
    File file = new File("imagemanagers" + File.separator +
        test.getFileNumber() + ".ser");
    file.delete();
  }




}
