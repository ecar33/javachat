package com.javachat.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import com.javachat.message.*;
import com.google.gson.Gson;

public class UserSession {
  private Socket socket;
  private String userId;
  private PrintWriter out;
  private BufferedReader in;
  private Server server;
  private SessionManager sessionManager;
  private final Gson gson = new Gson(); // Gson instance for serializing/deserializing

  public UserSession(Socket socket, String userId, Server server) throws IOException {
    this.socket = socket;
    this.userId = userId;
    this.out = new PrintWriter(socket.getOutputStream(), true);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.server = server;
    this.sessionManager = server.getSessionManager();
  }

  public void processMessages() {
    try {
      // socket.setSoTimeout(1000);
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        // Deserialize input to a SentMessage object
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

  public void sendMessage(Message message) {
    // Serialize the message to json before sending
    String jsonMessage = gson.toJson(message);
    out.println(jsonMessage);
    out.flush();
  }

  private void handleMessage(Message message) {
    // Process the message here (e.g., log, modify, forward)
    System.out.println("Received from " + userId + ": " + message.getContent());
    sessionManager.broadcastMessage(message, userId);
  }

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

  public String getUserId() {
    return userId;
  }

}