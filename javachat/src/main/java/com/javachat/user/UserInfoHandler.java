package com.javachat.user;

import java.io.IOException;
import java.nio.file.*;

/**
 * Utility class for managing user IDs, including generating, saving, and loading operations.
 */
public class UserInfoHandler {
  
    private static final String USER_ID_DIRECTORY = "userdata/"; // Directory to store user ID files
    private static final String USER_ID_EXTENSION = "userid.txt"; // File extension for user ID files
  
    /**
     * Loads a user ID from a file based on the provided username. If the file does not exist,
     * it creates a new file and returns null.
     *
     * @param userName The username whose user ID file is to be loaded.
     * @return The user ID as a String, or null if the file is newly created.
     * @throws IOException If an I/O error occurs during file operations.
     */
    public static String loadUserID(String userName) throws IOException {
        Path filePath = Paths.get(USER_ID_DIRECTORY + '/' + userName, USER_ID_EXTENSION);
        if (Files.exists(filePath)) {
            return new String(Files.readAllBytes(filePath));
        } else {
            // Create the file if it doesn't exist
            Files.createDirectories(filePath.getParent()); // Create parent directories if they don't exist
            Files.createFile(filePath);
            return null; // Return null as the file is newly created
        }
    }

    /**
     * Saves the given user ID to a file associated with the specified username.
     *
     * @param userName The username whose user ID is to be saved.
     * @param userID The user ID to be saved.
     * @throws IOException If an I/O error occurs during file write operation.
     */
    public static void saveUserID(String userName, String userID) throws IOException {
        Path filePath = Paths.get(USER_ID_DIRECTORY + '/' + userName, USER_ID_EXTENSION);
        Files.write(filePath, userID.getBytes(), StandardOpenOption.CREATE);
    }

    /**
     * Generates a unique user ID using Java's UUID functionality.
     *
     * @return A unique user ID as a String.
     */
    public static String generateUniqueUserId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8); // Generate a unique identifier
    }

    /**
     * Retrieves an existing user ID for a given username from the filesystem,
     * or generates and saves a new one if it does not exist.
     *
     * @param userName The username for which the user ID is required.
     * @return The user ID as a String, or null if an error occurs during ID retrieval or generation.
     */
    public static String getOrCreateUserId(String userName) {
        try {
            String userId = loadUserID(userName);
            if (userId == null) {
                userId = generateUniqueUserId();
                saveUserID(userName, userId);
            }
            return userId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
