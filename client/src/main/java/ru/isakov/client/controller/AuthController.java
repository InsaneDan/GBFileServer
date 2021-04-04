package ru.isakov.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.isakov.Command;
import ru.isakov.client.model.AlertPopup;
import ru.isakov.client.model.Network;

public class AuthController {

    private ClientController parentController;

    @FXML TextField login;
    @FXML PasswordField password;
    @FXML Button btnSignIn;
    @FXML Button btnSignUp;

    public void setParentController(ClientController parentController) {
        this.parentController = parentController;
    }

    public void exitAction() {
        parentController.exitAction();
        Platform.exit();
    }

    public void signIn(ActionEvent actionEvent) {
        if (!login.getText().trim().equals("") && !password.getText().trim().equals("")) {
            Command command = Command.authCommand(login.getText().trim(), password.getText().trim());
            parentController.getNetwork().sendCommand(command);

//            backController.refreshLocalFilesList();

        } else {
            AlertPopup.show(Alert.AlertType.ERROR, "Неверные данные",
                    "Не указан логин или пароль", "");
        }

    }

    public void signUp(ActionEvent actionEvent) {
        if (!login.getText().trim().equals("") && !password.getText().trim().equals("")) {
//            backController.getClientFileMethods().sendCommand(UnitedType.AUTH, (login.getText().trim() + " " + password.getText().trim()));
//            backController.refreshLocalFilesList();
        } else {
            AlertPopup.show(Alert.AlertType.ERROR, "Неверные данные",
                    "Не указан логин или пароль", "");
        }
    }

}