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

import com.google.gson.Gson;
import com.javachat.message.Message;

public class UserMessageHistoryHandler {
    private static final String USER_ID_DIRECTORY = "userdata/";
    private UserInfo userInfo;
    private ConcurrentHashMap<String, List<Message>> messagesByUser;

    public UserMessageHistoryHandler(UserInfo userInfo, ConcurrentHashMap<String, List<Message>> messagesByUser) {
        this.userInfo = userInfo;
        this.messagesByUser = messagesByUser;
    }

    public void cacheMessage(Message msg) {
        messagesByUser.computeIfAbsent(userInfo.getUserId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(msg);
    }

    public void saveMessagesToFile() {
        Gson gson = new Gson();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = userInfo.getUserId() + "_" + timestamp + "_messages.json";

        Path userDirectory = Paths.get(USER_ID_DIRECTORY, userInfo.getUserName(), "messages");

        try {
            Files.createDirectories(userDirectory); 

            // Construct the path for the new file
            Path filePath = userDirectory.resolve(fileName);

            // Convert the messages list to JSON string
            String json = gson.toJson(messagesByUser.get(userInfo.getUserId()));
            if (!json.isEmpty()) {
                Files.write(filePath, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                maintainFileCount(userDirectory);
            }
        } catch (IOException e) {
            System.out.println("Failed to save messages: " + e.getMessage());
        }
    }

    private void maintainFileCount(Path directory) throws IOException {
        // Get all files, filter by JSON, and sort by name (which contains timestamp)
        List<Path> files = Files.list(directory)
                .filter(path -> path.toString().endsWith(".json"))
                .sorted()
                .collect(Collectors.toList());

        // If there are more than 10 files, delete the oldest
        while (files.size() > 10) {
            Files.delete(files.get(0));
            files.remove(0);
        }
    }
}

