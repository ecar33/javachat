package com.javachat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import com.javachat.message.Message;
import com.javachat.user.User;
import com.google.gson.Gson;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson = new Gson();
    private User user;
    private Consumer<Message> onMessageReceived;

    public ChatClient(User user, String serverAddress, int port) {
        this.user = user;

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the userID of the client to the server
            out.println(user.getUserId());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReceivingMessages(Consumer<Message> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
        new Thread(() -> {
            try {
                String jsonString;
                // Continuously read messages from the server
                while ((jsonString = in.readLine()) != null) {
                    // Use the Consumer to handle the received message
                    Message msg = gson.fromJson(jsonString, Message.class);
                    onMessageReceived.accept(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

    }

    public void sendMessage(Message msg) {
        String jsonMessage = new Gson().toJson(msg); // Serialize the message to JSON
        out.println(jsonMessage); // Send the JSON string to the server
        out.flush(); // Ensure the data is sent immediately
    }

    public void closeConnection() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            
            System.out.println("Client socket properly clossed");

        } catch (IOException e) {
            System.out.println("Error closing socket for user " + user.getUserId());
        }
    }
}
