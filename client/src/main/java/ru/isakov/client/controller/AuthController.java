package ru.isakov.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.isakov.client.model.Network;

public class AuthController {
    private Network network;

    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    Button btnSignIn;

    @FXML
    Button btnSignUp;

    public void exitAction() {
        Platform.exit();
    }

    public void signIn(ActionEvent actionEvent) {

    }

    public void signUp(ActionEvent actionEvent) {

    }

}