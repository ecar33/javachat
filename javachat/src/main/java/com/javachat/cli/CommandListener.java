package com.javachat.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.javachat.server.Server;
import com.javachat.server.SessionManager;
import com.javachat.user.UserInfo;
import com.javachat.message.SentMessage;

/**
 * Provides a command-line interface to interact with the chat server.
 * Allows executing server administrative commands like broadcasting messages, 
 * viewing server status, and listing active sessions and users.
 */
public class CommandListener implements Runnable {
    private Server server;
    private SessionManager sessionManager;

    /**
     * Constructs a CommandListener with a reference to the Server.
     * @param server The server instance this listener will interact with.
     */
    public CommandListener(Server server) {
        this.server = server;
        this.sessionManager = server.getSessionManager();
    }

    /**
     * Continuously reads commands from the console and processes them.
     * Recognized commands include broadcasting messages, checking server status,
     * listing users, and sessions, as well as providing help.
     */
    @Override
    public void run() {
        UserInfo serverUInfo = new UserInfo("server", "@server");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();

                if ("broadcast".equalsIgnoreCase(line.split(" ")[0])) {
                    String messageContent = line.substring(line.indexOf(' ') + 1);
                    SentMessage message = new SentMessage(messageContent, serverUInfo);
                    sessionManager.broadcastMessage(message, "server");
                    continue;
                }
                switch (line) {
                    case "status":
                        System.out.println("Current server status: Active");
                        break;
                    case "users":
                        server.getSessionManager().printUsers();
                        break;
                    case "sessions":
                        server.getSessionManager().printSessions();
                        break;
                    case "help":
                        System.out.println("Available commands: status, users, sessions, broadcast <message>, help");
                        break;
                    default:
                        System.out.println("Invalid command. Type 'help' for a list of commands.");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception in CommandListener: " + e.getMessage());
        }
    }
}
