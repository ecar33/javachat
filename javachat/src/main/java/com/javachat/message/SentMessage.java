package com.javachat.message;

public class SentMessage implements Message{
  private final String text;
  private final String userId;

  public SentMessage(String text, String userId) {
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
      return "Me: " + text;
  }

  @Override
  public String getStyleClass() {
      return "sent-message";
  }

  @Override 
  public boolean isSentByUser() {
    return true;
  }



}
