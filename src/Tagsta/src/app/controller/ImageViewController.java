package app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** Controller for the image view on the top right of the overview */
public class ImageViewController {

  @FXML private ImageView imageView;
  @FXML private ScrollPane scrollPane;
  @FXML private Button zoomIn;
  @FXML private Button zoomOut;

  private final ImageView ZOOM_IN_ICON =
      new ImageView(new Image(getClass().getResourceAsStream("/resources/zoomIn.png")));
  private final ImageView ZOOM_OUT_ICON =
      new ImageView(new Image(getClass().getResourceAsStream("/resources/zoomOut.png")));

  /**
   * Updates the image displayed
   *
   * @param img the image to display
   */
  void updateImage(Image img) {
    imageView.setImage(img);

    // This will resize the image to fit inside the image view if it is too big
    if (scrollPane.getHeight() <= imageView.getImage().getHeight()
        || scrollPane.getWidth() <= imageView.getImage().getWidth()) {
      imageView.setFitWidth(scrollPane.getWidth());
      imageView.setFitHeight(scrollPane.getHeight());
    } else {
      imageView.setFitWidth(imageView.getImage().getWidth());
      imageView.setFitHeight(imageView.getImage().getHeight());
    }
  }

  /** Zooms in to the image */
  @FXML
  private void handleZoomIn() {
    // Make sure the image doesn't get too big
    if (imageView.getFitHeight() < 10000 && imageView.getFitWidth() < 10000) {
      // Zoom by 10% each time
      imageView.setFitHeight(imageView.getFitHeight() * 1.1);
      imageView.setFitWidth(imageView.getFitWidth() * 1.1);
    }
  }

  /** Zooms out of the image */
  @FXML
  private void handleZoomOut() {
    // Make sure the image doesn't get too small
    if (imageView.getFitHeight() > 50 && imageView.getFitWidth() > 50) {
      // Zoom out by 10% each time
      imageView.setFitHeight(imageView.getFitHeight() * 0.9);
      imageView.setFitWidth(imageView.getFitWidth() * 0.9);
    }
  }

  /** Enables the zoom in & out buttons (disabled by default) */
  void enableControls() {
    zoomIn.setDisable(false);
    zoomOut.setDisable(false);
  }

  /** Initializes the image view controller */
  @FXML
  private void initialize() {
    // Sets icons for the zoom in and out buttons
    zoomIn.setGraphic(ZOOM_IN_ICON);
    zoomOut.setGraphic(ZOOM_OUT_ICON);
  }
}
