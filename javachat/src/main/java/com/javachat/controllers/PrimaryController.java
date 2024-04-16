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

import com.javachat.server.Server;
import com.javachat.client.ChatClient;

import java.util.function.Consumer;

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

import com.javachat.message.*;
import com.google.gson.Gson;

public class PrimaryController {

    private ChatClient chatClient;

    private Thread serverThread;

    @FXML
    private TextField chatTextField;

    @FXML
    private ListView<Message> chatListView;

    @FXML
    private Button sendButton;

    Consumer<Message> addReceivedMessage = message -> {
        Platform.runLater(() -> addMessage(message));
    };

    public void initialize() {
        Server server = new Server();
        System.out.println("Server started");
        serverThread = new Thread(server);
        serverThread.setDaemon(true); // Set the server thread as a daemon so it doesn't prevent the application from
                                      // exiting
        serverThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
        }));

        chatClient = new ChatClient("127.0.0.1", 5000, addReceivedMessage);

        Platform.runLater(() -> chatTextField.requestFocus());

        // Set the initial background color of sendButton
        sendButton.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 196, 65), CornerRadii.EMPTY, Insets.EMPTY)));

        chatTextField.setOnKeyPressed(event -> handleEnterKeyPress(event));

        chatListView.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);

                if (empty || msg == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null);
                } else {
                    setText(msg.getTextRepresentation());
                    setStyle(msg.getStyleClass());
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
        String txt = chatTextField.getText();
        if (txt.trim().isEmpty()) {
            return; // Do not send empty messages
        }

        SentMessage msg = new SentMessage(txt);
        chatClient.sendMessage(msg);
        chatTextField.clear();
        addMessage(msg);
        chatListView.scrollTo(chatListView.getItems().size() - 1);

    }

    public void addMessage(Message message) {
        chatListView.getItems().add(message);
        chatTextField.requestFocus();
    }

    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String txt = chatTextField.getText();
            if (txt.trim().isEmpty()) {
                return; // Do not send empty messages
            }

            SentMessage msg = new SentMessage(txt);
            chatClient.sendMessage(msg);

            chatTextField.clear();
            addMessage(msg);
            chatListView.scrollTo(chatListView.getItems().size() - 1);
        }
    }

}