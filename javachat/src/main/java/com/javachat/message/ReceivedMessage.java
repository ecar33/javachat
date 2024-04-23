package com.javachat.message;

public class ReceivedMessage implements Message{
  private final String text;
  private final String userId;

  public ReceivedMessage(String text, String userId) {
    this.text = text;
    this.userId = userId;
  }

  public String getContent() {
    return text;
  }

  public String getUserId() {
    return userId;
  }

  @Override
  public String getTextRepresentation() {
      return "Them with id: " + userId + text;
  }

  @Override
  public String getStyleClass() {
      return "received-message";
  }

  @Override 
  public boolean isSentByUser() {
    return false;
  }


}
