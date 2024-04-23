package com.javachat.message;

public interface Message {
  String getTextRepresentation();
  String getStyleClass();
  String getContent();
  String getUserId();
  boolean isSentByUser();
}
