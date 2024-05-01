package com.javachat.message;

import com.javachat.user.UserInfo;

public class SentMessage implements Message {
    private final String text;
    private final UserInfo userInfo;

    public SentMessage(String text, UserInfo userInfo) {
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
        return userInfo.getUserName() + ": " + text;
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
