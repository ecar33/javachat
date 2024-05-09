package com.javachat.user;

/**
 * Represents a user of the chat application, encapsulating user identification and username.
 */
public class User {
    private String userName; // The name of the user
    private String userId;   // A unique identifier for the user

    /**
     * Constructs a new User instance.
     * Initializes the user with a username and generates a unique user ID.
     *
     * @param userName the username of the user.
     */
    public User(String userName) {
        this.userName = userName;
        this.userId = UserInfoHandler.getOrCreateUserId(userName);
    }

    /**
     * Returns the username of this user.
     *
     * @return the username as a String.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return the user ID as a String.
     */
    public String getUserId() {
        return userId;
    }

    // Note: The commented-out properties such as MessageHistory and UserSettings suggest planned future extensions to include user settings and message histories.
}
