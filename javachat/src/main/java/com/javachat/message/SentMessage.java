package com.javachat.message;

import com.javachat.user.UserInfo;

/**
 * Represents a message sent by the user.
 */
public class SentMessage implements Message {
    private final String text;
    private final UserInfo userInfo;

    /**
     * Constructs a new SentMessage with specified text and user information.
     * @param text the content of the message
     * @param userInfo the user info of the sender
     */
    public SentMessage(String text, UserInfo userInfo) {
        this.text = text;
        this.userInfo = userInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContent() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextRepresentation() {
        return userInfo.getUserName() + ": " + text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStyleClass() {
        return "sent-message";
    }

    /**
     * {@inheritDoc}
     */
    @Override 
    public boolean isSentByUser() {
        return true;
    }

}
