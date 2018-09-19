package app.controller;

import app.Tagsta;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/** A dialog that popups when an error occurs to inform the user */
public class ExceptionDialogPopup {

  /**
   * Creates an popup based on the given text describing the error/exception
   *
   * @param exceptionHeader the text to be displayed at the top of the exception
   * @param exceptionText the text to be displayed at the bottom of the exception
   */
  public static void createExceptionPopup(String exceptionHeader, String exceptionText) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    // Set the error/exception text and show it
    alert.setTitle("Exception Occurred");
    alert.setHeaderText(exceptionHeader);
    alert.setContentText(exceptionText);
    // Add the icon to the exception
    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(Tagsta.getIcon());
    alert.showAndWait();
  }
}
