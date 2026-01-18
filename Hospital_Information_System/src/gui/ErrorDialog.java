package gui;

import javafx.scene.control.Alert;

public class ErrorDialog {
    public static void show(String title, String header, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(details);
        alert.showAndWait();
    }

    public static void showException(String title, String header, Exception ex) {
        show(title, header, ex.getMessage() == null ? ex.toString() : ex.getMessage());
    }
}
