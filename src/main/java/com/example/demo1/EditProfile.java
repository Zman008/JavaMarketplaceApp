package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class EditProfile {
    public TextField nameField;
    public TextField emailField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Hyperlink backLink;
    public Label messageLabel;
    public TextField phoneField;

    public void initialize() throws SQLException {
        String sql = "SELECT name, email, phone, pass FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int userId = LoggedInUser.getId();
            stmt.setInt(1, userId);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                nameField.setText(result.getString("name"));
                emailField.setText(result.getString("email"));
                phoneField.setText(result.getString("phone"));
                passwordField.setText(result.getString("pass"));
                confirmPasswordField.setText(result.getString("pass"));
            }
        }
    }

    public void handleRegister(ActionEvent actionEvent) {
        messageLabel.setText("");

        if (!Objects.equals(passwordField.getText(), confirmPasswordField.getText())) {
            messageLabel.setText("Password does not match!");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        String checkSql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, emailField.getText());
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                messageLabel.setText("Email already registered!");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            String updateSql = "UPDATE users SET name = ?, email = ?, phone = ?, pass = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, nameField.getText());
                updateStmt.setString(2, emailField.getText());
                updateStmt.setString(3, phoneField.getText());
                updateStmt.setString(4, passwordField.getText());
                updateStmt.setInt(5, LoggedInUser.getId());

                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    messageLabel.setTextFill(Color.GREEN);
                    messageLabel.setText("Update Successful!");

                    LoggedInUser.setUsername(nameField.getText());
                    LoggedInUser.setEmail(emailField.getText());
                    LoggedInUser.setPhone(phoneField.getText());

                    goBack(actionEvent);
                } else {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText("No user found to update.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            messageLabel.setText("Registration Error: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    public void goBack(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
