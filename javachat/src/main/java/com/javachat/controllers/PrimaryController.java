package com.javachat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import com.javachat.message.*;
import com.javachat.client.ChatClient;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.Color;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

import com.javachat.user.User;

public class PrimaryController {

    private ChatClient client;
    private User user;

    @FXML
    private TextField chatTextField;

    @FXML
    private ListView<Message> chatListView;

    @FXML
    private Button sendButton;

    Consumer<Message> updateChatWindow = message -> {
        Platform.runLater(() -> addMessageToChatListView(message));
    };

    public void initialize() {
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
                    setText(message.getTextRepresentation());
                    setStyle(message.getStyleClass());
                    setBackground(Background.EMPTY);
                }
            }
        });

        // Set the BackgroundImage
        BackgroundImage bgImage = BGImageLoader.load("/com/javachat/background.png");
        if (bgImage != null) {
            chatListView.setBackground(new Background(bgImage));
        }
    }

    public void setupClient() {
        if (client != null) {
            client.startReceivingMessages(updateChatWindow);
            Platform.runLater(() -> chatTextField.requestFocus());
        } else {
            System.out.println("Client is not set properly for controller.");
        }
 
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
        sendMessage();

    }

    public void addMessageToChatListView(Message message) {
        chatListView.getItems().add(message);
        chatTextField.requestFocus();
    }

    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String txt = chatTextField.getText();
        if (txt.trim().isEmpty()) {
            return; // Do not send empty messages
        }

        SentMessage msg = new SentMessage(txt, user.getUserId());
        client.sendMessage(msg);

        chatTextField.clear();
        addMessageToChatListView(msg);
        chatListView.scrollTo(chatListView.getItems().size() - 1);
    }

    public void setChatClient(ChatClient client) {
        this.client = client;
    }

    public void setUser(User user) {
        this.user = user;
    }

}