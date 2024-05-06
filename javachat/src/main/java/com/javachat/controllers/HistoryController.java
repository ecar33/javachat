package com.javachat.controllers;

import com.javachat.App;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.File;

public class HistoryController {
    @FXML
    Button menuButton;

    private App app;
    private File[] files;

    public void setApp(App app) {
        this.app = app;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    @FXML
    private void handleMenuButtonPress(ActionEvent e) {
        app.loadView("login_view" + ".fxml", controller -> {
            if (controller instanceof LoginController) {
                ((LoginController) controller).setApp(app);
            }
        });

    }
}
