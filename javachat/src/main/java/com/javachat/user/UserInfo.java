package com.javachat.user;

/**
 * Encapsulates basic user information including the user's identifier and username.
 */
public class UserInfo {
    private String userName; // The name of the user
    private String userId;   // The unique identifier for the user

    /**
     * Constructs a new UserInfo instance.
     * 
     * @param userId The unique identifier of the user.
     * @param userName The username of the user.
     */
    public UserInfo(String userId, String userName) {
        this.userName = userName;
        this.userId = userId;
    }

    /**
     * Returns the username of the user.
     * 
     * @return the username as a String.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the unique identifier of the user.
     * 
     * @return the user ID as a String.
     */
    public String getUserId() {
        return userId;
    }
}
