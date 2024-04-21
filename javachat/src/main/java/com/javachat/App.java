package com.javachat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.javachat.user.User;
import com.javachat.client.ChatClient;
import com.javachat.controllers.PrimaryController;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private Scene scene;
    private ChatClient client;

    @Override
    public void start(Stage stage) throws IOException {
        User user = new User("Jimmybob");
        this.client = new ChatClient(user, "127.0.0.1", 5000);

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
        controller.setChatClient(client); // Re-inject the client if switching roots
    }

    public static void main(String[] args) {
        launch(args);
    }
}
