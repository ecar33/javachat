package com.javachat.message;

import com.javachat.user.UserInfo;

/**
 * Defines the common functionality for all message types in the chat application.
 */
public interface Message {
      /**
     * Returns a text representation of the message.
     * @return the text representation
     */
  String getTextRepresentation();
      /**
     * Returns the CSS style class associated with the message for UI display.
     * @return the CSS style class
     */
  String getStyleClass();
        /**
     * Returns the content of the message.
     * @return the message content
     */
  String getContent();
    /**
     * Returns the user information of the sender of the message.
     * @return the user info
     */
  UserInfo getUserInfo();
    /**
     * Determines if the message is sent by the user.
     * @return true if sent by the user, false otherwise
     */
  boolean isSentByUser();
}
