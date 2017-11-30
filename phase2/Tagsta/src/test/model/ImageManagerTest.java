package test.model;
import app.model.ImageManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ImageManagerTest {

  private ImageManager getImageManager() throws URISyntaxException{
    Path path = Paths.get(getClass().getResource("../resources/pic @arbitrary.png").toURI());
    return new ImageManager(path);
  }

  @Test
  void ImageManagerTagFromNameTest() throws URISyntaxException{
    ImageManager test = this.getImageManager();
    assertEquals("arbitrary", test.getTags().get(0));
  }

  @Test
  void ImageManagerPreviousNamesTest() throws URISyntaxException{
    ImageManager test = this.getImageManager();
    assertEquals(1, test.getPrevNames().size());
    assertEquals("pic.png", test.getPrevNames().get(0));
  }
}
