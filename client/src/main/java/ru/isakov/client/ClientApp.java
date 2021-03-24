package ru.isakov.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.client.controller.AuthController;
import ru.isakov.client.controller.ClientController;
import ru.isakov.client.model.ClientState;
import ru.isakov.client.model.Network;

// to start in debug mode: mvn clean javafx:run@debug

public class ClientApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(ClientApp.class);

    private ClientState clientState = ClientState.AUTHENTICATION;
    private Stage primaryStage;
    private Stage authDialogStage;
    private Network network;
    private ClientController clientController;
    private AuthController authController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainForm.fxml"));
        Parent root = fxmlLoader.load();

        clientController = fxmlLoader.getController();

        primaryStage.setOnCloseRequest(event -> clientController.exitAction());
        primaryStage.setTitle("File Cloud Server");
        Scene scene = new Scene(root, 900, 500);
        scene.getStylesheets().add("/style.css");
//        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}