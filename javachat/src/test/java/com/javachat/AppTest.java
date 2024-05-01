package com.javachat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class AppTest extends ApplicationTest {

    private Stage primaryStage; // Store stage as a field

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage; // Assign the stage to the field
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/javachat/chat_view.fxml"));
        Parent mainNode = fxmlLoader.load();
        stage.setScene(new Scene(mainNode));
        stage.setResizable(false);
        stage.show();
        stage.toFront();
    }

    @Test
    public void testInitialScene() {
        assertEquals(600, primaryStage.getScene().getWidth(), 0.1);
        assertEquals(400, primaryStage.getScene().getHeight(), 0.1);
    }
}
