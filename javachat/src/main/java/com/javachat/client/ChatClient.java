package com.javachat.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import com.javachat.message.Message;
import com.google.gson.Gson;


public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson = new Gson();

    public ChatClient(String serverAddress, int port, Consumer<Message> onMessageReceived) {
        try {
            socket = new Socket(serverAddress, port);
            // Initialize the PrintWriter and BufferedReader for sending and receiving
            // messages
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg) {
        String jsonMessage = new Gson().toJson(msg); // Serialize the message to JSON
        out.println(jsonMessage); // Send the JSON string to the server
        out.flush(); // Ensure the data is sent immediately
    }

}
