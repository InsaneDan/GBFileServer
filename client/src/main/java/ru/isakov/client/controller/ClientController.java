package ru.isakov.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.isakov.client.model.Network;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private Network network;

    @FXML
    TextField msgField;

    @FXML
    TextArea mainArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new Network((args) -> {
            mainArea.appendText((String)args[0]);
        });
    }

    public void sendMsgAction(ActionEvent actionEvent) {
        network.sendMessage(msgField.getText()); // отправить
        msgField.clear(); // очистить поле
        msgField.requestFocus(); // вернуть фокус в поле
    }

    public void exitAction() {
        network.close();
        Platform.exit();
    }
}