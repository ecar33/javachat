package com.javachat.message;

import com.javachat.user.UserInfo;

public interface Message {
  String getTextRepresentation();
  String getStyleClass();
  String getContent();
  UserInfo getUserInfo();
  boolean isSentByUser();
}
