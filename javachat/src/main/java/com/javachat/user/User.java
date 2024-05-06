package com.javachat.user;

public class User {
  private String userName;
  private String userId;

  // private MessageHistory history;
  // private UserSettings settings;

  public User(String userName) {
    this.userName = userName;
    this.userId = UserInfoHandler.getOrCreateUserId(userName);
  }

  public String getUserName() {
    return userName;
  }

  public String getUserId() {
    return userId;
  }

}