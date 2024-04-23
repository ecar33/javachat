package com.javachat.user;

public class UserInfo {
  private String userName;
  private String userId;


  public UserInfo(String userId, String userName) {
    this.userName = userName;
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getUserId() {
    return userId;
  }

}
