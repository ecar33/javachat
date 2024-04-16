package com.javachat.message;

public class SentMessage implements Message{
  private final String text;
  private final int userID;

  public SentMessage(String text, int userID) {
    this.text = text;
    this.userID = userID;
  }

  public String getText() {
    return text;
  }

  public int userID() {
    return userID;
  }

  @Override
  public String getTextRepresentation() {
      return "Me: " + text;
  }

  @Override
  public String getStyleClass() {
      return "sent-message";
  }


}
