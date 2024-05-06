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
import com.javachat.user.UserMessageHistoryHandler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class App extends Application {
    private static App instance;
    private Scene scene;
    private ChatClient client;
    private UserInfo userInfo = null;
    private UserMessageHistoryHandler messageHistoryHandler;
    private ConcurrentHashMap<String, List<Message>> messagesByUser;
    private volatile boolean alreadySaved = false;

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
            if (instance != null) {
                instance.saveMessagesIfNeeded();
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
        setRoot("chat_view", userInfo);
    }

    public void setRoot(String fxml, UserInfo userInfo) {
        try {
            // Ensure userInfo is initialized here
            setUserInfo(userInfo);

            if (userInfo != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
                Parent root = fxmlLoader.load();
                scene.setRoot(root);
                PrimaryController controller = fxmlLoader.getController();
                if (controller != null) {
                    controller.setChatClient(client);
                    controller.setUserInfo(userInfo);
                    controller.setupClient();
                } else {
                    System.out.println("No controller found for this FXML");
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + fxml);
            e.printStackTrace();
        }
    }

    public void cacheMessage(Message msg) {
        messageHistoryHandler.cacheMessage(msg);
    }

    public void switchToHistoryView(String username) {
        try {
            File userDir = new File("userdata/" + username + "/messages");
            if (!userDir.exists() || !userDir.isDirectory()) {
                System.out.println("No chat history available for: " + username);
                return; // or notify the user via the UI
            }

            File[] files = userDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null || files.length == 0) {
                System.out.println("No chat history files found for: " + username);
                return; // or notify the user via the UI
            }

            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/path/to/ChatHistoryView.fxml"));
            Parent root = fxmlLoader.load();
            scene.setRoot(root);
            HistoryController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.setFiles(files);
            }
        } catch (IOException e) {
            System.err.println("Error loading chat history view: " + e.getMessage());
            e.printStackTrace();
        }
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
