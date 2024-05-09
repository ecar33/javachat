package com.javachat.message;

import com.javachat.user.UserInfo;

/**
 * Represents a message received by a user from another user.
 */
public class ReceivedMessage implements Message {
    private final String text;
    private final UserInfo userInfo;

    /**
     * Constructs a new ReceivedMessage with specified text and user information.
     * @param text the content of the message
     * @param userInfo the user info of the sender
     */
    public ReceivedMessage(String text, UserInfo userInfo) {
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
        return userInfo.getUserName() + ": " +  text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStyleClass() {
        return "received-message";
    }

    /**
     * {@inheritDoc}
     */
    @Override 
    public boolean isSentByUser() {
        return false;
    }
}
