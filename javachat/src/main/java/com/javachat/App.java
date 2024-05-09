package com.javachat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.javachat.user.UserInfo;
import com.javachat.client.ChatClient;
import com.javachat.controllers.PrimaryController;
import com.javachat.message.Message;
import com.javachat.controllers.HistoryController;
import com.javachat.controllers.LoginController;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.javachat.user.UserMessageHistoryHandler;

import java.util.List;

/**
 * Main application class that initializes and starts the JavaFX application.
 * Manages the user interface scenes and transitions between them based on user
 * interaction.
 */
public class App extends Application {
    private static App instance;
    private Scene scene;
    private ChatClient client;
    private UserInfo userInfo = null;
    private UserMessageHistoryHandler messageHistoryHandler;
    private ConcurrentHashMap<String, List<Message>> messagesByUser;
    private volatile boolean alreadySaved = false;
    private File[] historyFiles; // Holds the chat history files for the current user

    /**
     * Returns the singleton instance of the application.
     * 
     * @return the current instance of the application
     */
    public static App getInstance() {
        return instance;
    }

    /**
     * Sets the user info for the current session.
     * 
     * @param userInfo the user info to set
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Initialization method for the application. Sets up necessary handlers and
     * runtime hooks.
     */
    @Override
    public void init() {
        instance = this;
        messagesByUser = new ConcurrentHashMap<>();
        this.messageHistoryHandler = new UserMessageHistoryHandler(userInfo, messagesByUser);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            App instance = App.getInstance();
            if (instance != null && instance.userInfo != null) {
                instance.saveMessagesIfNeeded();
            } else {
                System.out.println("Shutdown process initiated without user info, no chatlog to save.");
            }
        }));
    }

    /**
     * Starts the primary stage and sets up the initial scene.
     * 
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login_view.fxml"));
        Parent root = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        if (loginController != null) {
            loginController.setApp(this);
            System.out.println("App set for login view");
        }
        scene = new Scene(root, 600, 400);

        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            // Handle window close request by user.
            Platform.exit();
        });
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Initializes the chat client with user information and switches the scene to
     * the chat view.
     * 
     * @param userInfo the user info
     */
    public void initializeClientAndSwitchScene(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.client = new ChatClient("127.0.0.1", 5000, userInfo, instance);
        this.messageHistoryHandler = new UserMessageHistoryHandler(userInfo, messagesByUser);
        setRootForChatClient("chat_view", userInfo);
    }

    /**
     * Loads a view from the specified FXML file and sets the scene's root to the
     * loaded view.
     * Allows for controller customization via a consumer functional interface.
     * 
     * @param fxml     the FXML file name to load
     * @param userInfo user information to pass to the controller
     */
    public void setRootForChatClient(String fxml, UserInfo userInfo) {
        loadView(fxml + ".fxml", controller -> {
            if (controller instanceof PrimaryController) {
                ((PrimaryController) controller).setChatClient(client);
                ((PrimaryController) controller).setUserInfo(userInfo);
                ((PrimaryController) controller).setupClient();
                ((PrimaryController) controller).setApp(instance);
            }
        });
    }

    /**
     * Retrieves chat history files for the specified user.
     * 
     * @param username the username for whom to retrieve chat history
     * @return true if files were found, false otherwise
     */
    public boolean getFiles(String username) {
        File userDir = new File("userdata/" + username + "/messages");
        if (!userDir.exists() || !userDir.isDirectory()) {
            System.out.println("No chat history available for: " + username);
            return false;
        }

        File[] files = userDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No chat history files found for: " + username);
            return false;
        }

        historyFiles = files;
        return true;
    }

    /**
     * Caches a received message.
     * 
     * @param msg the message to cache
     */
    public void cacheMessage(Message msg) {
        messageHistoryHandler.cacheMessage(msg);
    }

    /**
     * Dynamically loads an FXML file and optionally sets up its controller.
     * 
     * @param fxmlFile        the FXML file path
     * @param setupController a consumer to set up the controller after loading
     */
    public void loadView(String fxmlFile, Consumer<Object> setupController) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Object controller = fxmlLoader.getController();
            if (controller != null && setupController != null) {
                setupController.accept(controller);
            }
            scene.setRoot(root);
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Switches to the history view for the specified user, setting up necessary
     * files.
     * 
     * @param username the username whose history is to be displayed
     */
    public void switchToHistoryView(String username) {
        // Retrieve the files for the current user
        getFiles(username);

        if (historyFiles != null) {
            loadView("history_view" + ".fxml", controller -> {
                if (controller instanceof HistoryController) {
                    ((HistoryController) controller).setFiles(historyFiles);
                    ((HistoryController) controller).setApp(instance);
                }
            });
        }

    }

    /**
     * Saves messages to file if they have not already been saved.
     */
    private synchronized void saveMessagesIfNeeded() {
        if (!alreadySaved) {
            messageHistoryHandler.saveMessagesToFile();
            alreadySaved = true;
        }
    }

    /**
     * Entry point for the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Handles application closure, ensuring that resources are freed and messages
     * are saved.
     */
    @Override
    public void stop() {
        if (client != null) {
            client.closeConnection();
        }
        saveMessagesIfNeeded();
        System.out.println("Successfully closed the application.");
    }

}
