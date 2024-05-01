package com.javachat.message;

import com.javachat.user.UserInfo;

public class ReceivedMessage implements Message {
    private final String text;
    private final UserInfo userInfo;

    public ReceivedMessage(String text, UserInfo userInfo) {
        this.text = text;
        this.userInfo = userInfo;
    }

    @Override
    public String getContent() {
        return text;
    }

    @Override
    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public String getTextRepresentation() {
        return userInfo.getUserName() + ": " +  text;
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
