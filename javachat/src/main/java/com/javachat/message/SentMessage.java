package com.javachat.message;

public class SentMessage implements Message{
  private final String text;
  private final String userID;

  public SentMessage(String text, String userID) {
    this.text = text;
    this.userID = userID;
  }

  public String getContent() {
    return text;
  }

  public String getUserID() {
    return userID;
  }

  @Override
  public String getTextRepresentation() {
      return "Me with id: " + userID + text;
  }

  @Override
  public String getStyleClass() {
      return "sent-message";
  }


}
