package app.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/** Controller class for the log view window */
public class LogViewController {

  @FXML private ListView<String> log;

  /**
   * Set the logs to be displayed
   *
   * @param log the logs to be displayed
   */
  void setLog(ObservableList<String> log) {
    this.log.setItems(log);
    this.log.refresh();
  }
}
