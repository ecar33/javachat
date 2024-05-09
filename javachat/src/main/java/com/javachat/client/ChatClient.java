package com.javachat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.javachat.App;
import com.javachat.message.Message;
import com.javachat.message.ReceivedMessage;
import com.javachat.message.SentMessage;
import com.javachat.user.UserInfo;
import com.google.gson.Gson;

/**
 * Handles the client-side functionality of the chat application, managing the connection,
 * sending, and receiving messages.
 */
public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private App app;
    private PrintWriter out;
    private Gson gson = new Gson();
    private UserInfo userInfo;
    private Consumer<Message> onMessageReceived;
    private ConcurrentHashMap<String, List<Message>> messagesByUser;

    /**
     * Constructs a new ChatClient, initializing the connection to the server.
     * 
     * @param serverAddress the IP address or hostname of the server
     * @param port the port number on which the server is listening
     * @param userInfo the user information for the client
     * @param app the main application class
     */
    public ChatClient(String serverAddress, int port, UserInfo userInfo, App app) {
        this.userInfo = userInfo;
        this.app = app;

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the UserInfo of the client to the server
            out.println(gson.toJson(userInfo));
            out.flush();

        } catch (IOException e) {
            System.out.println("Connection was unsuccessful to the server, make sure the server is online.");
            e.printStackTrace();
        }
    }

    /**
     * Starts a new thread to receive messages from the server continuously.
     * Converts the received JSON messages into message objects and notifies the application.
     *
     * @param onMessageReceived a consumer that handles received messages
     */
    public void startReceivingMessages(Consumer<Message> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
        new Thread(() -> {
            try {
                String jsonString;
                while ((jsonString = in.readLine()) != null) {
                    SentMessage msg = gson.fromJson(jsonString, SentMessage.class);
                    ReceivedMessage receivedMsg = new ReceivedMessage(msg.getContent(), msg.getUserInfo());
                    onMessageReceived.accept(receivedMsg);

                    app.cacheMessage(msg);
                }
            } catch (IOException e) {
                System.out.println("Connection lost.");
            } catch (Exception e) {
                System.out.println("Error processing message: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Sends a message to the server.
     *
     * @param msg the message to send
     */
    public void sendMessage(Message msg) {
        String jsonMessage = gson.toJson(msg); // Serialize the message to JSON
        out.println(jsonMessage); // Send the JSON string to the server
        out.flush(); // Ensure the data is sent immediately

        app.cacheMessage(msg);
    }

    /**
     * Closes the client connection, including the input/output streams and the socket.
     */
    public void closeConnection() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            
            System.out.println("Client socket properly closed");

        } catch (IOException e) {
            System.out.println("Error closing socket for user " + userInfo.getUserId());
        }
    }
}
