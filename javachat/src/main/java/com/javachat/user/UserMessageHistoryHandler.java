package com.javachat.user;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import com.javachat.message.Message;

/**
 * Handles the storage and retrieval of message histories for users of the chat application.
 */
public class UserMessageHistoryHandler {
    private static final String USER_ID_DIRECTORY = "userdata/"; // Directory path for user data storage
    private UserInfo userInfo;
    private ConcurrentHashMap<String, List<Message>> messagesByUser;

    /**
     * Constructs a message history handler for a specific user.
     * 
     * @param userInfo The user info of the user whose messages are to be managed.
     * @param messagesByUser A concurrent map containing lists of messages for each user.
     */
    public UserMessageHistoryHandler(UserInfo userInfo, ConcurrentHashMap<String, List<Message>> messagesByUser) {
        this.userInfo = userInfo;
        this.messagesByUser = messagesByUser;
    }

    /**
     * Caches a message in memory.
     * If no list exists for the user, a new list is created and added to the map.
     * 
     * @param msg The message to be cached.
     */
    public void cacheMessage(Message msg) {
        messagesByUser.computeIfAbsent(userInfo.getUserId(), k -> Collections.synchronizedList(new ArrayList<>()))
                      .add(msg);
    }

    /**
     * Saves cached messages to a text file in a user-specific directory.
     * Each message is written in a formatted and readable manner, with timestamps.
     */
    public void saveMessagesToFile() {
        if (this.userInfo != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
            String timestamp = LocalDateTime.now().format(formatter);
            String fileName = userInfo.getUserId() + "_" + timestamp + "_messages.txt";

            Path userDirectory = Paths.get(USER_ID_DIRECTORY, userInfo.getUserName(), "messages");

            try {
                Files.createDirectories(userDirectory);

                // Construct the path for the new file
                Path filePath = userDirectory.resolve(fileName);

                // Convert the messages list to a formatted string for human-readable file
                List<Message> userMessages = messagesByUser.get(userInfo.getUserId());
                if (userMessages != null && !userMessages.isEmpty()) {
                    List<String> formattedMessages = userMessages.stream()
                        .map(msg -> String.format("%s [%s]: %s",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                msg.getUserInfo().getUserName(),
                                msg.getContent()))
                        .collect(Collectors.toList());

                    Files.write(filePath, formattedMessages, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                    maintainFileCount(userDirectory);
                }
            } catch (IOException e) {
                System.out.println("Failed to save messages: " + e.getMessage());
            }
        }
    }

    /**
     * Maintains a limit on the number of saved message files in the user directory.
     * Older files are deleted to keep the directory size manageable.
     * 
     * @param directory The path to the directory where message files are stored.
     * @throws IOException If an error occurs during file deletion.
     */
    private void maintainFileCount(Path directory) throws IOException {
        // Get all files, filter by text files, and sort by name (which contains timestamp)
        List<Path> files = Files.list(directory)
                                .filter(path -> path.toString().endsWith(".txt"))
                                .sorted()
                                .collect(Collectors.toList());

        // If there are more than 10 files, delete the oldest
        while (files.size() > 10) {
            Files.delete(files.get(0));
            files.remove(0);
        }
    }
}
