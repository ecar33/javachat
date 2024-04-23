package com.javachat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import com.javachat.cli.CommandListener;


public class Server implements Runnable {
    private ServerSocket serverSocket;
    private final SessionManager sessionManager;

    public Server() {
        this.sessionManager = new SessionManager();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(5000);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, this)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final Server server;
        private final SessionManager sessionManager;
        private UserSession userSession;
        private String userId;

        public ClientHandler(Socket socket, Server server) {
            this.clientSocket = socket;
            this.server = server;
            this.sessionManager = server.getSessionManager();
        }

        @Override
        public void run() {
            try {
                // Create a temp reader to read the userId sent by the client
                BufferedReader tempReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                userId = tempReader.readLine(); // Assuming the first line is the userId

                if (userId != null) {
                    System.out.println("Client connected with userID: " + userId);
                    this.userSession = new UserSession(clientSocket, userId, server);
                    sessionManager.addSession(userId, userSession);
                    userSession.processMessages();
                } else {
                    System.out.println("Failed to read userId from the client.");
                }
            } catch (IOException e) {
                System.out.println("Error handling client #" + clientSocket + ": " + e.getMessage());
            } finally {
                if (userId != null) {
                    sessionManager.removeSession(userId);
                    System.out.println("Session for user with id " + userId + "removed.");
                    if (userSession != null) {
                        userSession.closeSession();
                        System.out.println("Session closed for user with id " + userId);
                    }
                }
            }
        }
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        System.out.println("Server started and waiting for connections...");

        CommandListener cli = new CommandListener(server);
        Thread commandThread = new Thread(cli);
        commandThread.start();

        System.out.println("Command listener started");

        try {
            serverThread.join();
        } catch (InterruptedException e) {
            System.out.println("Server thread was interrupted");
            Thread.currentThread().interrupt(); // Set the interrupt flag again
        }
    }
}