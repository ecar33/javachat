package com.javachat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import com.javachat.message.*;
import com.javachat.App;
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

import com.javachat.user.UserInfo;

/**
 * Controller for the primary chat window in the JavaChat application.
 * Handles user interactions for sending messages and managing the chat UI.
 */
public class PrimaryController {

    private ChatClient client;
    private UserInfo userInfo;
    private App app;

    @FXML
    private TextField chatTextField;

    @FXML
    private ListView<Message> chatListView;

    @FXML
    private Button sendButton;

    /**
     * Consumer to handle updating the chat window by adding messages.
     */
    Consumer<Message> updateChatWindow = message -> {
        Platform.runLater(() -> addMessageToChatListView(message));
    };

    /**
     * Initializes the controller, setting up UI components and handlers.
     */
    public void initialize() {
        // Initialize UI components such as setting the background color for the send button.
        sendButton.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 196, 65), CornerRadii.EMPTY, Insets.EMPTY)));

        chatTextField.setOnKeyPressed(event -> handleEnterKeyPress(event));
        
        chatListView.setCellFactory(param -> new MessageCell());
        

        // Load and set the background image for the chat list view.
        BackgroundImage bgImage = BGImageLoader.load("/com/javachat/background.png");
        if (bgImage != null) {
            chatListView.setBackground(new Background(bgImage));
        }
    }

    /**
     * Sets the reference to the main application class to handle scene switching.
     * @param app The main application class instance.
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * Sets up the client for receiving messages and focuses the text input field.
     */
    public void setupClient() {
        if (client != null) {
            client.startReceivingMessages(updateChatWindow);
            Platform.runLater(() -> chatTextField.requestFocus());
        } else {
            System.out.println("Client is not set properly for controller.");
        }
    }

    /**
     * Handles mouse over event on the send button to provide visual feedback.
     * @param event The mouse event triggered when hovering over the send button.
     */
    @FXML
    private void handleSendButtonMouseOver(MouseEvent event) {
        sendButton.setBackground(
                new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        sendButton.setScaleX(0.95);
        sendButton.setScaleY(0.95);
        event.consume();
    }

    /**
     * Handles mouse exit event on the send button to reset its appearance.
     * @param event The mouse event triggered when the mouse exits the send button.
     */
    @FXML
    private void handleSendButtonMouseExited(MouseEvent event) {
        sendButton.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 196, 65), CornerRadii.EMPTY, Insets.EMPTY)));
        sendButton.setScaleX(1.0);
        sendButton.setScaleY(1.0);
        event.consume();
    }

    /**
     * Handles pressing the send button to send a message.
     * @param event The action event triggered by pressing the send button.
     */
    @FXML
    private void handleSendButtonPress(ActionEvent event) {
        sendMessage();
    }

    /**
     * Adds a new message to the chat list view and requests focus on the text field.
     * @param message The message to be added to the chat display.
     */
    public void addMessageToChatListView(Message message) {
        chatListView.getItems().add(message);
        chatTextField.requestFocus();
    }

    /**
     * Handles the ENTER key press to send a message.
     * @param event The key event triggered when a key is pressed.
     */
    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    /**
     * Sends a message written in the chat text field.
     */
    private void sendMessage() {
        String txt = chatTextField.getText();
        if (txt.trim().isEmpty()) {
            return; // Do not send empty messages
        }

        SentMessage msg = new SentMessage(txt, userInfo);
        client.sendMessage(msg);

        chatTextField.clear();
        addMessageToChatListView(msg);
        chatListView.scrollTo(chatListView.getItems().size() - 1);
    }

    /**
     * Sets the client instance for this controller.
     * @param client The chat client instance to be used for message handling.
     */
    public void setChatClient(ChatClient client) {
        this.client = client;
    }

    /**
     * Returns the client instance for this controller.
     */
    public ChatClient getChatClient() {
        return client;
    }

    /**
     * Sets the user information for the client.
     * @param userInfo The user information of the logged-in user.
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
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
