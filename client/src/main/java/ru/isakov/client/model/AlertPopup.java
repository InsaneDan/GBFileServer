package ru.isakov.client.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class AlertPopup {

    public static void show (Alert.AlertType alertType, String alertTitle, String alertHeader, String alertDetails) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertTitle);
        alert.setHeaderText(alertHeader);
        alert.setContentText(alertDetails);
        alert.showAndWait();
    }

}
