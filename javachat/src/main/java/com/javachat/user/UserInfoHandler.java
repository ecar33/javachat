package com.javachat.user;

import java.io.IOException;
import java.nio.file.*;

public class UserInfoHandler {
  
  private static final String USER_ID_DIRECTORY = "userdata/";
  private static final String USER_ID_EXTENSION = "userid.txt";
  
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

  public static void saveUserID(String userName, String userID) throws IOException {
    Path filePath = Paths.get(USER_ID_DIRECTORY + '/' + userName, USER_ID_EXTENSION);
    Files.write(filePath, userID.getBytes(), StandardOpenOption.CREATE);
  }

  public static String generateUniqueUserId() {
    return java.util.UUID.randomUUID().toString().substring(0, 8); // Generate a unique identifier
  }

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
