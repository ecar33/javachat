package com.javachat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import com.javachat.message.*;
import com.google.gson.Gson;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private final Gson gson = new Gson();
    private static ConcurrentHashMap<String, ClientHandler> activeClients = new ConcurrentHashMap<>();


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(5000);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String clientUserID;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

                // Read the first message which should be the userID
                this.clientUserID = in.readLine();
                

                if (clientUserID != null) {
                    System.out.println("Client connected with UID: " + clientUserID);
                    activeClients.put(clientUserID, this);
                }

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // Deserialize as SentMessage
                    SentMessage incomingMessage = gson.fromJson(inputLine, SentMessage.class);
                    
                    // Log to server console
                    System.out.println("Received from " + clientUserID + " : " + incomingMessage.getText());

                    // Create new ReceivedMessage for broadcasting
                    ReceivedMessage messageToBroadcast = new ReceivedMessage(incomingMessage.getText(), clientUserID);

                    // Broadcast Message
                    broadcastMessage(messageToBroadcast, clientUserID);
                }

            } catch (IOException e) {
                System.out.println("Error handling client #" + clientSocket + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    activeClients.remove(clientUserID);  // Remove client from the map
                    System.out.println("Client disconnected with UID: " + clientUserID);

                } catch (IOException e) {
                    System.out.println("Could not close a socket: " + e.getMessage());
                }
            }
        }
    
        public void sendMessage(Message msg) {
            Gson gson = new Gson();
            PrintWriter out;
            try {
                out = new PrintWriter(this.clientSocket.getOutputStream(), true);
                String jsonMessage = gson.toJson(msg); // Serialize the message to JSON
                out.println(jsonMessage); // Send the JSON string to the server
                out.flush(); // Ensure the data is sent immediately
            } catch (IOException e) {
                System.out.println("Failed to send message to " + clientUserID + ": " + e.getMessage());
            }
        }
    }

    public void broadcastMessage(Message message, String senderUserID) {
        activeClients.forEach((uid, handler) -> {
            if (!uid.equals(senderUserID)) {
                handler.sendMessage(message);
            }
        });
    }

    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

        System.out.println("Server started and waiting for connections...");

        try {
            serverThread.join();
        } catch (InterruptedException e) {
            System.out.println("Server thread was interrupted");
            Thread.currentThread().interrupt(); // Set the interrupt flag again
        }
    }
}