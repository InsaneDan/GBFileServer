package ru.isakov.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.Command;
import ru.isakov.client.model.Network;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private Network network;

    @FXML VBox clientPanel, serverPanel;
    PanelController serverPanelController, clientPanelController;

    @FXML HBox createDirBox;
    @FXML TextField createDirPath;
    @FXML Button createDirConfirmButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.serverPanelController = (PanelController) serverPanel.getProperties().get("ctrlr");
        this.clientPanelController = (PanelController) clientPanel.getProperties().get("ctrlr");
        // TODO: исправить пути к папкам (сервер / клиент)
        serverPanelController.updateList(Paths.get("D:/testDirServer"));
        clientPanelController.updateList(Paths.get("D:/testDirClient"));

        network = new Network();

    }

    public void exitAction() {
        network.close();
        Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {

        if (serverPanelController.getSelectedFilename() == null && clientPanelController.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не был выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        PanelController srcPC = null, dstPC = null;
        if (serverPanelController.getSelectedFilename() != null) {
            srcPC = serverPanelController;
            dstPC = clientPanelController;
        }
        if (clientPanelController.getSelectedFilename() != null) {
            srcPC = clientPanelController;
            dstPC = serverPanelController;
        }

        Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFilename());
        Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName().toString());

        try {
            Files.copy(srcPath, dstPath);
            dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось скопировать указанный файл", ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void sendObject(ActionEvent actionEvent) {
        network.sendCommand(Command.exitCommand());
    }




}