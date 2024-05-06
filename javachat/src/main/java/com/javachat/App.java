package com.javachat;

import javafx.application.Application;
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

public class App extends Application {
    private static App instance;
    private Scene scene;
    private ChatClient client;
    private UserInfo userInfo = null;
    private UserMessageHistoryHandler messageHistoryHandler;
    private ConcurrentHashMap<String, List<Message>> messagesByUser;
    private volatile boolean alreadySaved = false;
    private File[] historyFiles; // Holds the chat history files for the current user

    public static App getInstance() {
        return instance;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void init() {
        instance = this;
        messagesByUser = new ConcurrentHashMap<>();
        this.messageHistoryHandler = new UserMessageHistoryHandler(userInfo, messagesByUser);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            App instance = App.getInstance();
            if (instance != null && instance.userInfo != null) { // Check if userInfo is set
                instance.saveMessagesIfNeeded();
            } else {
                // Log or handle the situation where no user info is available
                System.out.println("Shutdown process initiated without user info.");
            }
        }));
    }

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
        stage.setResizable(false);
        stage.show();
    }

    public void initializeClientAndSwitchScene(UserInfo userInfo) {
        this.userInfo = userInfo; // Ensure userInfo is updated
        this.client = new ChatClient("127.0.0.1", 5000, userInfo, instance);
        this.messageHistoryHandler = new UserMessageHistoryHandler(userInfo, messagesByUser);
        System.out.println("MHH initialized with user: " + userInfo.getUserName());
        setRootForChatClient("chat_view", userInfo);
    }

    public void setRootForChatClient(String fxml, UserInfo userInfo) {
        loadView(fxml + ".fxml", controller -> {
            if (controller instanceof PrimaryController) {
                ((PrimaryController) controller).setChatClient(client);
                ((PrimaryController) controller).setChatClient(client);
                ((PrimaryController) controller).setUserInfo(userInfo);
                ((PrimaryController) controller).setupClient();
            }
        });
    }

    public boolean getFiles(String username) {
        File userDir = new File("userdata/" + username + "/messages");
        if (!userDir.exists() || !userDir.isDirectory()) {
            System.out.println("No chat history available for: " + username);
            return false; // Indicate failure to find files
        }

        File[] files = userDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.out.println("No chat history files found for: " + username);
            return false; // Indicate failure to find files
        }

        historyFiles = files; // Set the sorted files to the instance variable
        return true; // Indicate successful file retrieval
    }

    public void cacheMessage(Message msg) {
        messageHistoryHandler.cacheMessage(msg);
    }

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

    public void switchToHistoryView(String username) {
        loadView("history_view" + ".fxml", controller -> {
            if (controller instanceof HistoryController) {
                ((HistoryController) controller).setFiles(historyFiles);
                ((HistoryController) controller).setApp(instance);
            }
        });
    }

    private synchronized void saveMessagesIfNeeded() {
        if (!alreadySaved) {
            messageHistoryHandler.saveMessagesToFile();
            alreadySaved = true;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (client != null) {
            client.closeConnection();
        }
        saveMessagesIfNeeded();
        System.out.println("Successfully closed the application.");
    }
}
