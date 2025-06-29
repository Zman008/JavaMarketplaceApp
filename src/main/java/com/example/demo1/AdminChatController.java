package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.net.Socket;

public class AdminChatController {

    public Button sendButton;
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private TextField recipientField;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final String adminName = "admin"; // must match the server logic

    @FXML
    public void initialize() {
        try {
            socket = new Socket("localhost", 6600);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write(adminName + "\n");
            writer.flush();

            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        String finalMsg = msg;
                        Platform.runLater(() -> chatArea.appendText(finalMsg + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Disconnected from server.\n"));
                }
            });
            listener.setDaemon(true);
            listener.start();

        } catch (IOException e) {
            chatArea.appendText("Failed to connect to chat server.\n");
        }
    }

    @FXML
    public void sendMessage() {
        String recipient = recipientField.getText().trim();
        String message = messageField.getText().trim();

        if (recipient.isEmpty() || message.isEmpty()) {
            chatArea.appendText("Recipient and message cannot be empty.\n");
            return;
        }

        String formattedMessage = "@" + recipient + " " + message;

        try {
            writer.write(formattedMessage + "\n");
            writer.flush();
            chatArea.appendText("You to " + recipient + ": " + message + "\n");
            messageField.clear();
        } catch (IOException e) {
            chatArea.appendText("Failed to send message.\n");
        }
    }
}
