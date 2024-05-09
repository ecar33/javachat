package com.javachat.controllers;

import com.javachat.user.User;
import com.javachat.user.UserInfo;
import com.javachat.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controller for handling login operations in the chat application.
 * Manages user input for logging in and viewing chat history.
 */
public class LoginController {
    @FXML
    private TextField usernameField; // Text field for inputting username

    private App app; // Reference to the main application class

    /**
     * Sets the reference to the main application class to handle scene switching.
     * @param app The main application class instance.
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * Handles the login button press event.
     * Validates the username and notifies the application to initialize the client and switch scenes.
     * @param event The event triggered by pressing the login button.
     */
    @FXML
    private void handleLoginButtonPress(ActionEvent event) {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            return; // Do not proceed with login if username is empty
        }
        System.out.println("Logging in as: " + username);
        usernameField.clear(); // Clear the input field after retrieving the username

        User user = new User(username); // Create a new user instance
        UserInfo userInfo = new UserInfo(user.getUserId(), user.getUserName());
        app.initializeClientAndSwitchScene(userInfo); // Initiate client and switch scene in the app
    }

    /**
     * Handles the history button press event.
     * Validates the username and notifies the application to switch to the history view.
     * @param event The event triggered by pressing the history button.
     */
    @FXML
    private void handleHistoryButtonPress(ActionEvent event) {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            System.out.println("Username is empty.");
            return; // Do not proceed with history view if username is empty
        }
        usernameField.clear(); // Clear the input field after retrieving the username

        app.switchToHistoryView(username); // Switch to the history view for the given username
    }
}
