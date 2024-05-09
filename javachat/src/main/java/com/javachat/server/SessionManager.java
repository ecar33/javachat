package com.javachat.server;

import java.util.concurrent.ConcurrentHashMap;
import com.javachat.message.Message;
import com.javachat.user.UserInfo;

/**
 * Manages user sessions and user information for the chat server.
 * Handles adding, removing, and broadcasting messages across sessions.
 */
public class SessionManager {
  private ConcurrentHashMap<String, UserSession> activeSessions;
  private ConcurrentHashMap<String, UserInfo> activeUserInfos;

  /**
   * Constructs a new SessionManager with empty collections for sessions and user
   * infos.
   */
  public SessionManager() {
    activeSessions = new ConcurrentHashMap<>();
    activeUserInfos = new ConcurrentHashMap<>();
  }

  /**
   * Adds a new user session to the manager.
   * 
   * @param userId  the user ID that uniquely identifies the session
   * @param session the UserSession to be managed
   */
  public void addSession(String userId, UserSession session) {
    activeSessions.put(userId, session);
  }

  /**
   * Adds user information to the manager.
   * 
   * @param userId   the user ID associated with the user information
   * @param userInfo the UserInfo object containing user details
   */
  public void addUserInfo(String userId, UserInfo userInfo) {
    activeUserInfos.put(userId, userInfo);
  }

  /**
   * Removes user information from the manager.
   * 
   * @param userId the user ID associated with the user information to be removed
   */
  public void removeUserInfo(String userId) {
    UserInfo userInfo = activeUserInfos.remove(userId);
    if (userInfo != null) {
      System.out.println("UserInfo for " + userInfo.getUserName() + " has been removed.");
    } else {
      System.out.println("No user info found for user ID: " + userId);
    }
  }

  /**
   * Removes a user session from the manager and closes the session.
   * 
   * @param userId the user ID of the session to be removed
   */
  public void removeSession(String userId) {
    UserSession session = activeSessions.remove(userId);
    if (session != null) {
      session.closeSession();
      System.out.println("UserSession for user with id " + userId + " removed.");
    }
  }

  /**
   * Prints all currently active user sessions.
   */
  public void printSessions() {
    System.out.println("Current active sessions: ");
    activeSessions.forEach((key, value) -> System.out.println(key + " = " + value));
  }

  /**
   * Prints all currently active user information entries.
   */
  public void printUsers() {
    System.out.println("Current active users: ");
    activeUserInfos.forEach((userId, userInfo) -> System.out.println(userInfo.getUserName()));
  }

  /**
   * Retrieves a user session by user ID.
   * 
   * @param userId the user ID of the session to retrieve
   * @return the UserSession associated with the user ID, or null if no session
   *         exists
   */
  public UserSession getSession(String userId) {
    return activeSessions.get(userId);
  }

  /**
   * Retrieves user information by user ID.
   * 
   * @param userId the user ID of the user information to retrieve
   * @return the UserInfo associated with the user ID, or null if no user
   *         information exists
   */
  public UserInfo getUserInfo(String userId) {
    return activeUserInfos.get(userId);
  }

  /**
   * Broadcasts a message to all users except the sender.
   * 
   * @param message      the message to be sent
   * @param senderUserID the user ID of the sender, who will not receive their own
   *                     message
   */
  public void broadcastMessage(Message message, String senderUserID) {
    activeSessions.forEach((userId, session) -> {
      if (!userId.equals(senderUserID)) {
        session.sendMessage(message);
      }
    });
  }
}
