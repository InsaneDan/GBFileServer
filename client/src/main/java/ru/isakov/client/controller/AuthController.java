package ru.isakov.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.isakov.server.Command;
import ru.isakov.client.model.AlertPopup;
import ru.isakov.client.model.Network;

public class AuthController {

    @FXML TextField login;
    @FXML PasswordField password;
    @FXML Button btnSignIn;
    @FXML Button btnSignUp;

    private Network network;

    public void setNetwork(Network network) { this.network = network; }

    public void exitAction() {
        // действие не требуется, просто закрыть окно
    }

    public void signIn(ActionEvent actionEvent) {
        if (!login.getText().trim().equals("") && !password.getText().trim().equals("")) {
            Command command = Command.authCommand(login.getText().trim(), password.getText().trim());
            network.sendCommand(command);
        } else {
            AlertPopup.show(Alert.AlertType.ERROR, "Неверные данные",
                    "Не указан логин или пароль", "");
        }

    }

    public void signUp(ActionEvent actionEvent) {
        if (!login.getText().trim().equals("") && !password.getText().trim().equals("")) {
            Command command = Command.authCommand(login.getText().trim(), password.getText().trim());
        } else {
            AlertPopup.show(Alert.AlertType.ERROR, "Неверные данные",
                    "Не указан логин или пароль", "");
        }
    }

}