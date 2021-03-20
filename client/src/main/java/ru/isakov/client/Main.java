package ru.isakov.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.isakov.client.controller.ClientController;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client.fxml"));
        Parent root = fxmlLoader.load();
        ClientController clientController = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> clientController.exitAction());
        primaryStage.setTitle("Geek Chat Client");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}