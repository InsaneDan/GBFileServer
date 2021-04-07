package ru.isakov.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.client.controller.AuthController;
import ru.isakov.client.controller.ClientController;
import ru.isakov.client.model.AlertPopup;
import ru.isakov.client.model.Network;

import java.io.IOException;

import static java.lang.Thread.sleep;

// to start in debug mode: mvn clean javafx:run@debug

public class ClientApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(ClientApp.class);

    private Stage primaryStage;
    private Stage authStage;
    private Network network;
    private boolean authState;
    private ClientController clientController;
    private AuthController authController;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception{
        establishConnection();
        authState = true;
        showAuth();
        if (authState) showClient();

    }

    // установить соединение с сервером
    private void establishConnection() throws InterruptedException {
        network = new Network(this);
        network.connect();
        // ждем флаг, удалось ли установить соединение
        while (network.isConnected() == null) { sleep(100);}
        // если соединение не установлено - сообщение об ошибке и выйти из метода
        if (network.isConnected() == Boolean.FALSE) {
            AlertPopup.show(Alert.AlertType.ERROR, "Ошибка соединения",
                    "Не удалось установить соединение с сервером!", "");
            return;
        }
    }

    // форма авторизации и регистрации
    private void showAuth(){
        try {
            authStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuthForm.fxml"));
            Parent root = loader.load();
            authController = loader.getController();
            authController.setNetwork(network);
            authStage.setOnCloseRequest(event -> authController.exitAction());
            authStage.setTitle("Authentication");
            Scene scene = new Scene(root, 320, 200);
            scene.getStylesheets().add("/style.css");
            authStage.setResizable(false); // зафиксировать размер
            authStage.setScene(scene);
            authStage.showAndWait();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void closeAuth() {
        authStage.close();
    }

    // основная форма
    private void showClient(){
        try {
            this.primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainForm.fxml"));
            Parent root = fxmlLoader.load();
            clientController = fxmlLoader.getController();
            primaryStage.setTitle("File Cloud Server");
            Scene scene = new Scene(root, 920, 500);
            scene.getStylesheets().add("/style.css");

            clientController.setNetwork(network);
            primaryStage.setOnCloseRequest(event -> clientController.exitAction());

//            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void setAuthState(boolean authState) {
        this.authState = authState;
    }

    public AuthController getAuthController() {
        return authController;
    }
}