package no.ntnu.tools;

import javafx.scene.Scene;
import javafx.scene.control.Alert;

public class Error {
  public static void showAlert(Alert.AlertType type, String title, String header, String content, Scene scene) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.initOwner(scene.getWindow());
    alert.showAndWait();
  }
}
