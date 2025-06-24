package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    public Button loginButton;
    public Hyperlink backLink;
    public Hyperlink register;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private Label messageLabel;

    @FXML
    public void handleLogin(ActionEvent e) throws IOException {
        if (email.getText() == "admin" && password.getText() == "admin") {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            return;
        }

        messageLabel.setText("");
        String sql = "SELECT id, pass, name, phone FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String em = email.getText().trim();
            stmt.setString(1, em);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                String storedPassword = result.getString("pass");
                int id = result.getInt("id");
                String name = result.getString("name");
                String phone = result.getString("phone");

                if (storedPassword.equals(password.getText().trim())) {
                    LoggedInUser.setId(id);
                    LoggedInUser.setUsername(name);
                    LoggedInUser.setEmail(em);
                    LoggedInUser.setPhone(phone);
                    messageLabel.setText("Login successful! ");

                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());

                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.setMaximized(true);
                } else {
                    messageLabel.setText("Incorrect password!");
                }
            } else {
                messageLabel.setText("Email not registered!");
            }
        } catch (SQLException exception) {
            messageLabel.setText("Database error: " + exception.getMessage());
        }
    }

    public void goBack(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void handleRegister(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
