package com.javachat.controllers;

import com.javachat.user.User;
import com.javachat.user.UserInfo;
import com.javachat.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField usernameField;

    private App app; // Reference to the App class

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    private void handleLoginButtonPress(ActionEvent event) {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            return; // Do not login with an empty username
        }
        System.out.println("Logging in as: " + username);
        usernameField.clear();

        User user = new User(username);
        UserInfo userInfo = new UserInfo(user.getUserId(), user.getUserName());
        app.initializeClientAndSwitchScene(userInfo); // Notify the app to switch the scene
    }
}
