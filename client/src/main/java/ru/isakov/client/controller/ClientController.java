package ru.isakov.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @FXML
    VBox clientPanel, serverPanel;

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

    public void exitAction() {
        network.close();
        Platform.exit();
    }

    public void copyBtnAction(ActionEvent actionEvent) {

        PanelController leftPC = (PanelController) serverPanel.getProperties().get("ctrlr");
        PanelController rightPC = (PanelController) clientPanel.getProperties().get("ctrlr");

        if (leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ни один файл не был выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        PanelController srcPC = null, dstPC = null;
        if (leftPC.getSelectedFilename() != null) {
            srcPC = leftPC;
            dstPC = rightPC;
        }
        if (rightPC.getSelectedFilename() != null) {
            srcPC = rightPC;
            dstPC = leftPC;
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

}