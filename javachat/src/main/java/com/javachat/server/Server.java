package com.javachat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.javachat.message.SentMessage;
import com.google.gson.Gson;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private final Gson gson = new Gson();

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server has started, waiting for connections...");

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
                }

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    SentMessage message = gson.fromJson(inputLine, SentMessage.class);
                    System.out.println("Received from " + clientUserID + " : " + message.getText());
                }

            } catch (IOException e) {
                System.out.println("Error handling client #" + clientSocket + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Could not close a socket: " + e.getMessage());
                }
            }
        }
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