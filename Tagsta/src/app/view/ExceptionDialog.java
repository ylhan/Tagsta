package app.view;

import javafx.scene.control.Alert;

public class ExceptionDialog {
    public static void createExceptionPopup(String exceptionHeader, String exceptionText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Occurred");
        alert.setHeaderText(exceptionHeader);
        alert.setContentText(exceptionText);
        alert.showAndWait();
    }
}
