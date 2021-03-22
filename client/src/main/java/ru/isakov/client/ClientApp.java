package ru.isakov.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.isakov.client.controller.AuthController;
import ru.isakov.client.controller.ClientController;
import ru.isakov.client.model.ClientState;
import ru.isakov.client.model.Network;

public class ClientApp extends Application {

    private ClientState clientState = ClientState.AUTHENTICATION;
    private Stage primaryStage;
    private Stage authDialogStage;
    private Network network;
    private ClientController clientController;
    private AuthController authController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AuthForm.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/AuthForm.fxml"));
        Parent root = fxmlLoader.load();
        authController = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> authController.exitAction());
        primaryStage.setTitle("Authorization");
        Scene scene = new Scene(root, 320, 200);
        scene.getStylesheets().add("/style.css");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

//        network = new Network(this);
//        if (!network.connect()) {
//            showNetworkError("", "Failed to connect to server", primaryStage);
//        }
//
//        viewController.setNetwork(network);
//        viewController.setStage(primaryStage);
//
//        network.waitMessages(viewController);
//
//        primaryStage.setOnCloseRequest(event -> {
//            network.close();
//        });
//
//        openAuthDialog();
//
//













    }

    public static void main(String[] args) {
        launch(args);
    }
}