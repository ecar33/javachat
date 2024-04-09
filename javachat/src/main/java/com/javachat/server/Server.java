package com.javachat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.javachat.message.Message;
import com.google.gson.Gson;

public class Server implements Runnable {
    private ServerSocket serverSocket;

    @Override
    public void run() {
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);

        try {
            serverSocket = new ServerSocket(5000, 50, InetAddress.getLoopbackAddress());
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Gson gson = new Gson();

            Thread sender = new Thread(new Runnable() {
                String line;
                String jsonMessage;

                @Override // annotation to override the run method
                public void run() {
                    while (true) {
                        line = sc.nextLine(); // reads data from user's keybord
                        Message msg = new Message(line, false);
                        jsonMessage = gson.toJson(msg);
                        out.println(jsonMessage); // write data stored in msg in the clientSocket
                        out.flush(); // forces the sending of the data
                    }
                }
            });
            sender.start();

            Thread receive = new Thread(new Runnable() {
                String jsonMessage;

                @Override
                public void run() {
                    try {
                        jsonMessage = in.readLine();
                        while (jsonMessage != null) {
                            Message msg = gson.fromJson(jsonMessage, Message.class);

                            System.out.println("Recieved from client: " + msg.getText());

                            Message echoMsg = new Message(msg.getText(), false);
                            jsonMessage = gson.toJson(echoMsg);
                            out.println(jsonMessage); // write data stored in msg in the clientSocket
                            out.flush(); // forces the sending of the data

                            jsonMessage = in.readLine();
                        }

                        System.out.println("Client disconnected");

                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            // Close resources
                            if (out != null) {
                                out.close();
                            }
                            if (clientSocket != null) {
                                clientSocket.close();
                            }
                            if (serverSocket != null) {
                                serverSocket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            receive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
