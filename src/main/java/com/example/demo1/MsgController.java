package com.example.demo1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class MsgController {

    @FXML
    public TextArea chatBox;
    @FXML
    public TextField chatInput;
    @FXML
    public Button sendButton;

    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket sc;
    private String name;

    @FXML
    public void initialize() {
        name = LoggedInUser.getUsername();

        try {
            sc = new Socket("localhost", 6600);
            reader = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream()));

            writer.write(name + "\n");
            writer.flush();

            Thread serverListen = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        String finalMsg = msg;
                        Platform.runLater(() -> chatBox.appendText(finalMsg + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatBox.appendText("Disconnected from server.\n"));
                }
            });

            serverListen.setDaemon(true);
            serverListen.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void sendFunc(ActionEvent actionEvent) {
        String text = chatInput.getText().trim();

        if (!text.isEmpty() && writer != null) {
            try {
                writer.write(text + "\n");
                chatBox.appendText(name + ": " + text + "\n");
                writer.flush();
                chatInput.clear();
            } catch (IOException e) {
                chatBox.appendText("Failed to send message.\n");
            }
        }
    }
}
