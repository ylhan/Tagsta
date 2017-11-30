package test.model;
import app.model.ImageManager;
import app.model.TagManager;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class TagManagerTest {
  private ImageManager getImageManager() throws URISyntaxException{
    Path path = Paths.get(getClass().getResource("../resources/pic @arbitrary.png").toURI());
    return new ImageManager(path);
  }

  private TagManager getTagManager() {
    return new TagManager();
  }

  @Test
  void ImageManagerCreationTest() throws URISyntaxException{
    TagManager test = getTagManager();
    File testFile = new File(getClass().getResource("../resources/pic @arbitrary.png").toURI());
    test.getImageManager(testFile);
    int size = test.getImageManagers().size();
    ImageManager temp = test.getImageManagers().get(size - 1);
    assertEquals(temp.toString(), "pic @arbitrary.png");
    test.getImageManager(testFile);
    assertEquals(test.getImageManagers().size(), size);
    File file = new File("imagemanagers" + File.separator +
        temp.getFileNumber() + ".ser");
    file.delete();
  }

  @Test
  void addingIndependentTagsTest(){
    TagManager test = getTagManager();
    test.addIndependentTag("sigh");
    ArrayList<String> tagsList = new ArrayList<>(test.getTagsList());
    assertEquals("sigh", tagsList.get(tagsList.size() - 1));
    int size = tagsList.size();
    test.addIndependentTag("boo");
    assertEquals(test.getTagsList().size(), size + 1);
    File file = new File("tags-list.ser");
    file.delete();

  }
  @Test
  void deletingIndependentTagsTest(){
    TagManager test = getTagManager();
    test.addIndependentTag("sigh");
    ArrayList<String> tagsList = new ArrayList<>(test.getTagsList());
    assertEquals("sigh", tagsList.get(tagsList.size() - 1));
    int size = tagsList.size();
    test.deleteIndependentTag("sigh");
    assertEquals(test.getTagsList().size(), size - 1);
    File file = new File("tags-list.ser");
    file.delete();

  }

}
