package com.javachat.message;

public class Message {
  private final String text;
  private final boolean isSent;

  public Message(String text, boolean isSent) {
    this.text = text;
    this.isSent = isSent;
  }

  public String getText() {
    return text;
  }

  public boolean isSent() {
    return isSent;
  }

}
