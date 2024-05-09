package com.javachat.controllers;

import com.javachat.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HistoryController {
    @FXML
    private Button menuButton;
    @FXML
    private Pagination pagination;

    private App app;
    private File[] files;

    public void setApp(App app) {
        this.app = app;
    }

    public void setFiles(File[] files) {
        this.files = files;
        if (files != null) {
            pagination.setPageCount(files.length);
            pagination.setPageFactory(this::createPage);
        }
    }

    private TextArea createPage(int pageIndex) {
        File file = files[pageIndex];
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            String content = String.join("\n", lines);
            textArea.setText(content);
        } catch (Exception e) {
            textArea.setText("Failed to load file: " + file.getName());
        }
        return textArea;
    }

    @FXML
    private void handleMenuButtonPress(ActionEvent e) {
        app.loadView("login_view.fxml", controller -> {
            if (controller instanceof LoginController) {
                ((LoginController) controller).setApp(app);
            }
        });
    }
}
