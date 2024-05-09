package com.javachat.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.javachat.user.UserInfo;
import com.javachat.message.*;
import com.google.gson.Gson;

/**
 * Represents a session for a user connected to the chat server.
 * Handles sending, receiving, and processing messages for the connected user.
 */
public class UserSession {
    private Socket socket;
    private String userId;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;
    private SessionManager sessionManager;
    private final Gson gson = new Gson(); // Gson instance for serializing/deserializing

    /**
     * Constructs a new UserSession for handling message transmission and reception.
     * @param socket the socket connected to the client
     * @param userId the unique identifier for the user
     * @param server the main server instance handling multiple sessions
     * @throws IOException if an I/O error occurs when creating input and output streams
     */
    public UserSession(Socket socket, String userId, Server server) throws IOException {
        this.socket = socket;
        this.userId = userId;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.server = server;
        this.sessionManager = server.getSessionManager();
    }

    /**
     * Continuously processes messages from the client, deserializing and handling them as they come.
     * Closes the session if the connection is interrupted or the client disconnects.
     */
    public void processMessages() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                SentMessage message = gson.fromJson(inputLine, SentMessage.class);
                handleMessage(message);
            }
        } catch (SocketTimeoutException ste) {
            System.out.println("Socket timed out waiting for client message for user " + userId);
        } catch (IOException e) {
            System.out.println("Error processing messages for " + userId);
        } finally {
            closeSession();
        }
    }

    /**
     * Sends a message to the client connected to this session.
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        String jsonMessage = gson.toJson(message);
        out.println(jsonMessage);
        out.flush();
    }

    /**
     * Handles a received message by broadcasting it to other users, or processing commands.
     * @param message the message received from the client
     */
    private void handleMessage(Message message) {
        UserInfo senderUserInfo = message.getUserInfo();

        if (senderUserInfo != null) {
            System.out.println("Message received from " + senderUserInfo.getUserName());
        } else {
            System.out.println("UserInfo couldn't be found.");
        }

        sessionManager.broadcastMessage(message, userId);
    }

    /**
     * Closes the session, shutting down input and output streams and closing the socket.
     */
    public void closeSession() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            System.out.println("Error closing socket for user " + userId);
        }
    }

    /**
     * Gets the user ID associated with this session.
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }
}
