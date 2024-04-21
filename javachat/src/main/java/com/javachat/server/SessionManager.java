package com.javachat.server;

import java.util.concurrent.ConcurrentHashMap;
import com.javachat.message.Message;


public class SessionManager {
  private ConcurrentHashMap<String, UserSession> activeSessions;

  public SessionManager() {
      activeSessions = new ConcurrentHashMap<>();
  }

  public void addSession(String userId, UserSession session) {
      activeSessions.put(userId, session);
  }

  public void removeSession(String userId) {
      UserSession session = activeSessions.remove(userId);
      if (session != null) {
          session.closeSession();
      }
  }

  public void printSessions() {
    System.out.println("Current connected users: ");
    activeSessions.forEach((key, value) -> System.out.println(key + " = " + value));
  }

  public UserSession getSession(String userId) {
      return activeSessions.get(userId);
  }

  public void broadcastMessage(Message message, String senderUserID) {
      activeSessions.forEach((userId, session) -> {
          if (!userId.equals(senderUserID)) {
              session.sendMessage(message);
          }
      });
  }
}
