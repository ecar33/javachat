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

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static App instance;
    private Scene scene;
    private ChatClient client;

    public App() {
        // Singleton pattern to ensure there is only 1 instance of 'App'
        if (instance == null) {
            instance = this;
        } else {
            throw new IllegalStateException("Cannot create more than 1 instance of App");
        }
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        User user = new User("Jimmybob");
        UserInfo userInfo = new UserInfo(user.getUserId(), user.getUserName());

        this.client = new ChatClient("127.0.0.1", 5000, userInfo);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chatview.fxml"));
        Parent root = fxmlLoader.load(); // Load the FXML and create the controller

        // After loading, get the controller and set the client
        PrimaryController controller = fxmlLoader.getController();
        if (controller != null) {
            controller.setChatClient(client);
            controller.setUser(user);
            controller.setupClient();
        } else {
            System.out.println("Can't find controller for this fxml");
        }

        scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Save the instance
        instance = this;
    }

    @Override
    public void stop() {
        if (client != null) {
            client.closeConnection(); // Ensure the connection is properly closed on stop
            System.out.println("Client socket has closed successfully.");
        }
    }

    public void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
        scene.setRoot(fxmlLoader.load());
        PrimaryController controller = fxmlLoader.getController();
        if (controller != null) {
            controller.setChatClient(client); // Re-inject the client if switching roots
        } else {
            System.out.println("No controller found for this FXML");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
