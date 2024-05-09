package com.javachat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import com.javachat.cli.CommandListener;
import com.google.gson.Gson;
import com.javachat.user.UserInfo;

/**
 * The main server class that listens for incoming connections and handles client requests.
 * Implements Runnable to allow the server to run on its own thread.
 */
public class Server implements Runnable {
    private ServerSocket serverSocket;
    private final SessionManager sessionManager;

    /**
     * Constructor that initializes a new SessionManager for managing client sessions.
     */
    public Server() {
        this.sessionManager = new SessionManager();
    }

    /**
     * The main server loop that accepts incoming client connections and creates a new thread for each.
     */
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

    /**
     * Inner class that handles individual client connections.
     * Each instance manages a single client socket.
     */
    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final Server server;
        private final SessionManager sessionManager;
        private UserSession userSession;
        private String userId;
        private Gson gson = new Gson();

        /**
         * Constructor for ClientHandler.
         * @param socket the client's socket
         * @param server the server instance this handler is part of
         */
        public ClientHandler(Socket socket, Server server) {
            this.clientSocket = socket;
            this.server = server;
            this.sessionManager = server.getSessionManager();
        }

        /**
         * The main run method that processes messages from the connected client.
         */
        @Override
        public void run() {
            try {
                BufferedReader tempReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = tempReader.readLine();
                UserInfo clientUserInfo = gson.fromJson(inputLine, UserInfo.class);

                if (clientUserInfo != null && clientUserInfo.getUserId() != null) {
                    System.out.println("Client connected with userID: " + clientUserInfo.getUserId());
                    this.userId = clientUserInfo.getUserId();
                    this.userSession = new UserSession(clientSocket, userId, server);

                    sessionManager.addSession(userId, userSession);
                    sessionManager.addUserInfo(userId, clientUserInfo);
                    
                    userSession.processMessages();
                } else {
                    System.out.println("Failed to read or parse UserInfo from the client.");
                }
            } catch (IOException e) {
                System.out.println("Error handling client #" + clientSocket + ": " + e.getMessage());
            } finally {
                if (userId != null) {
                    sessionManager.removeSession(userId);
                    sessionManager.removeUserInfo(userId);
                    if (userSession != null) {
                        userSession.closeSession();
                    }
                }
            }
        }
    }

    /**
     * Retrieves the session manager handling all client sessions.
     * @return the session manager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * The main method to start the server and command listener threads.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
        System.out.println("Server started and waiting for connections...");

        CommandListener cli = new CommandListener(server);
        Thread commandThread = new Thread(cli);
        commandThread.start();

        System.out.println("Command listener started, type 'help' for available commands");

        try {
            serverThread.join();
        } catch (InterruptedException e) {
            System.out.println("Server thread was interrupted");
            Thread.currentThread().interrupt(); // Set the interrupt flag again
        }
    }
}
