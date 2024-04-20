package com.javachat.message;

public class ReceivedMessage implements Message{
  private final String text;
  private final String senderUserID;

  public ReceivedMessage(String text, String senderUserID) {
    this.text = text;
    this.senderUserID = senderUserID;
  }

  public String getContent() {
    return text;
  }

  public String getSenderUserID() {
    return senderUserID;
  }

  @Override
  public String getTextRepresentation() {
      return "Them with id: " + senderUserID + text;
  }

  @Override
  public String getStyleClass() {
      return "received-message";
  }


}
