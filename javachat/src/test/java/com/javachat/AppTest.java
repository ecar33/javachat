package com.javachat;

import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.assertions.api.Assertions;

import com.javachat.controllers.PrimaryController;
import com.javachat.user.UserInfo;
import com.javachat.client.ChatClient;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test class for the JavaChat application, focusing on the user interface interactions
 * and behaviors using TestFX.
 */
@ExtendWith(ApplicationExtension.class)
public class AppTest extends ApplicationTest {

    private Stage stage;
    private PrimaryController controller;

    /**
     * Sets up the testing environment before each test runs. This method initializes the primary stage
     * and loads the necessary FXML, setting up the primary controller with a simulated chat client.
     *
     * @param stage The primary stage provided by the test framework.
     * @throws Exception if loading the FXML file fails.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/javachat/chat_view.fxml"));
        Parent root = loader.load();
        this.controller = loader.getController();
        this.stage = stage;
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
        
        // Simulate initializing the chat client
        UserInfo userInfo = new UserInfo("testUser", "Test User");
        controller.setUserInfo(userInfo);
        controller.setChatClient(new ChatClient("127.0.0.1", 5000, userInfo, App.getInstance()));
        controller.setupClient();
    }

    /**
     * Verifies that the initial conditions of the controller and chat client are correctly set.
     */
    @Test
    public void testInitialConditions() {
        Assertions.assertThat(controller).isNotNull();
        Assertions.assertThat(controller.getChatClient()).isNotNull();
    }

    /**
     * Tests that messages are correctly inputted and displayed in the chat ListView when a non-empty
     * message is submitted.
     */
    @Test
    public void testMessageInput() {
        clickOn("#chatTextField").write("Hello, world!");
        press(KeyCode.ENTER);

        try {
            waitFor(5, TimeUnit.SECONDS, () -> {
                ListView<?> listView = lookup("#chatListView").queryListView();
                return !listView.getItems().isEmpty();
            });

            verifyThat("#chatListView", (ListView<?> listView) ->
                !listView.getItems().isEmpty() && listView.getItems().get(0).toString().equals("Hello, world!"));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that empty messages are not sent and do not appear in the chat ListView.
     */
    @Test
    public void testEmptyMessageNotSent() {
        clickOn("#chatTextField").write("");
        press(KeyCode.ENTER);

        ListView<?> listView = lookup("#chatListView").queryListView();
        Assertions.assertThat(listView.getItems()).isEmpty();
    }
}
