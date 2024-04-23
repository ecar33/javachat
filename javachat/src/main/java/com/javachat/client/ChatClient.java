package com.javachat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import com.javachat.message.Message;
import com.javachat.message.ReceivedMessage;
import com.javachat.message.SentMessage;
import com.javachat.user.User;
import com.javachat.user.UserInfo;
import com.google.gson.Gson;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson = new Gson();
    private UserInfo userInfo;
    private Consumer<Message> onMessageReceived;

    public ChatClient(String serverAddress, int port, UserInfo userInfo) {
        this.userInfo = userInfo;

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the UserInfo of the client to the server
            out.println(gson.toJson(userInfo));
            out.flush();

        } catch (IOException e) {
            System.out.println("Connection was unsuccesful to the server, make sure the server is online.");
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
                    SentMessage msg = gson.fromJson(jsonString, SentMessage.class);

                    // Convert to ReceivedMessage
                    ReceivedMessage receivedMsg = new ReceivedMessage(msg.getContent(), msg.getUserId());
                    onMessageReceived.accept(receivedMsg);
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
            
            System.out.println("Client socket properly closed");

        } catch (IOException e) {
            System.out.println("Error closing socket for user " + userInfo.getUserId());
        }
    }
}
