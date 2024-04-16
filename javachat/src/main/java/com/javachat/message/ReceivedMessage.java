package com.javachat.message;

public class ReceivedMessage implements Message{
  private final String text;
  private final int userID;

  public ReceivedMessage(String text, int userID) {
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
      return "Them: " + text;
  }

  @Override
  public String getStyleClass() {
      return "received-message";
  }


}
