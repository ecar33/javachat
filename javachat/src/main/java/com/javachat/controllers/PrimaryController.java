package com.javachat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import com.javachat.message.Message;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

public class PrimaryController {

    @FXML
    private TextField chatTextField;

    @FXML
    private ListView<Message> chatListView;

    @FXML
    private Button sendButton;

    public void initialize() {

        Platform.runLater(() -> chatTextField.requestFocus());

        // Set the initial background color of sendButton
        sendButton.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 196, 65), CornerRadii.EMPTY, Insets.EMPTY)));

        chatTextField.setOnKeyPressed(event -> handleEnterKeyPress(event));

        chatListView.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (message.isSent()) {
                        // Customize for sent message
                        setText("Me: " + message.getText());
                        setStyle("-fx-text-fill: blue; -fx-alignment: center-right;");
                        setBackground(Background.EMPTY); // Use Background.EMPTY
                    } else {
                        // Customize for received message
                        setText("Them: " + message.getText());
                        setStyle("-fx-text-fill: black; -fx-alignment: center-left;");
                        setBackground(Background.EMPTY); // Use Background.EMPTY
                    }
                }
            }
        });
        // Load the image
        Image image = new Image(getClass().getResourceAsStream("/com/javachat/background.png"));
        if (image.isError()) {
            System.out.println("Image loading failed: " + image.getException());
        }
        // Create a BackgroundImage
        BackgroundImage bgImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true));

        // Set the BackgroundImage
        chatListView.setBackground(new Background(bgImage));
    }

    @FXML
    private void handleSendButtonMouseOver(MouseEvent event) {
        sendButton.setBackground(
                new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        sendButton.setScaleX(0.95);
        sendButton.setScaleY(0.95);
        event.consume();
    }

    @FXML
    private void handleSendButtonMouseExited(MouseEvent event) {
        sendButton.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 196, 65), CornerRadii.EMPTY, Insets.EMPTY)));
        sendButton.setScaleX(1.0);
        sendButton.setScaleY(1.0);
        event.consume();
    }

    @FXML
    private void handleSendButtonPress(ActionEvent event) {
        String msg = chatTextField.getText();
        if (msg.trim().isEmpty()) {
            return; // Do not send empty messages
        }

        chatTextField.clear();
        addMessage(msg, true);
        chatListView.scrollTo(chatListView.getItems().size() - 1);

    }

    public void addMessage(String text, boolean isSent) {
        chatListView.getItems().add(new Message(text, isSent));
        chatTextField.requestFocus();
    }

    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String msg = chatTextField.getText();
            if (msg.trim().isEmpty()) {
                return;
            }
            chatTextField.clear();
            addMessage(msg, true);
            chatListView.scrollTo(chatListView.getItems().size() - 1);
        }
    }

}