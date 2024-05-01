package com.javachat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.javachat.user.User;
import com.javachat.user.UserInfo;

import com.javachat.client.ChatClient;
import com.javachat.controllers.PrimaryController;
import com.javachat.controllers.LoginController;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static App instance;
    private Scene scene;
    private ChatClient client;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login_view.fxml"));
        Parent root = fxmlLoader.load(); // Load the FXML and create the controller

        // Pass the App instance to the login controller
        LoginController loginController = fxmlLoader.getController();
        if (loginController != null) {
            loginController.setApp(this);
        }

        scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void initializeClientAndSwitchScene(UserInfo userInfo) {
        this.client = new ChatClient("127.0.0.1", 5000, userInfo); // Initialize with actual server details
        setRoot("chat_view", userInfo); // Assuming userInfo holds a reference to User
    }

    public void setRoot(String fxml, UserInfo userInfo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
            Parent root = fxmlLoader.load();
            scene.setRoot(root);
            PrimaryController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.setChatClient(client);
                controller.setUserInfo(userInfo);
                controller.setupClient();
            } else {
                System.out.println("No controller found for this FXML");
            }
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + fxml);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (client != null) {
            client.closeConnection();
        }
    }
}
