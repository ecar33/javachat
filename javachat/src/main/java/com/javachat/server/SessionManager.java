package com.javachat.server;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.javachat.message.Message;
import com.javachat.user.UserInfo;


public class SessionManager {
  private ConcurrentHashMap<String, UserSession> activeSessions;
  private ConcurrentHashMap<String, UserInfo> activeUserInfos;


  public SessionManager() {
      activeSessions = new ConcurrentHashMap<>();
      activeUserInfos = new ConcurrentHashMap<>();
  }

  public void addSession(String userId, UserSession session) {
      activeSessions.put(userId, session);
  }

  public void addUserInfo(String userId, UserInfo userInfo) {
    activeUserInfos.put(userId, userInfo);

  }

  public void removeSession(String userId) {
      UserSession session = activeSessions.remove(userId);
      if (session != null) {
          session.closeSession();
      }
  }

  public void printSessions() {
    System.out.println("Current active sessions: ");
    activeSessions.forEach((key, value) -> System.out.println(key + " = " + value));
  }

  public void printUsers() {
    System.out.println("Current active users: ");
    activeUserInfos.forEach((userId, userInfo) -> System.out.println(userInfo.getUserName()));

  }

  public UserSession getSession(String userId) {
      return activeSessions.get(userId);
  }

  public UserInfo getUserInfo(String userId) {
    return activeUserInfos.get(userId);
  }

  public void broadcastMessage(Message message, String senderUserID) {
      activeSessions.forEach((userId, session) -> {
          if (!userId.equals(senderUserID)) {
              session.sendMessage(message);
          }
      });
  }
}
