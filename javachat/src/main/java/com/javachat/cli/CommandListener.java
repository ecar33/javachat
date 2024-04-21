package com.javachat.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.javachat.server.Server;

public class CommandListener implements Runnable {
    private Server server;

    public CommandListener(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;

            while ((line = reader.readLine()) != null) {
                switch (line.trim().toLowerCase()) {
                    case "status":
                        System.out.println("Current server status: Active");
                        break;
                    case "users":
                        server.getSessionManager().printSessions();
                        break;
                    case "help":
                        System.out.println("Available commands: status, users");
                        break;
                    default:
                        System.out.println("Invalid command. Type 'help");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception in CommandListener: " + e.getMessage());
        }
    }
}
