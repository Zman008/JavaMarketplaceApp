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

public class RegisterController {
    public TextField nameField;
    public TextField emailField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Hyperlink backLink;
    public Label messageLabel;
    public TextField phoneField;

    public boolean handleRegister(ActionEvent actionEvent) {
        messageLabel.setText("");

        if (!Objects.equals(passwordField.getText(), confirmPasswordField.getText())) {
            messageLabel.setText("Password does not match!");
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        String checkSql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, emailField.getText());
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                messageLabel.setText("Email already registered!");
                messageLabel.setTextFill(Color.RED);
                return false;
            }

            String insertSql = "INSERT INTO users (name, email, phone, pass) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, nameField.getText());
                insertStmt.setString(2, emailField.getText());
                insertStmt.setString(3, phoneField.getText());
                insertStmt.setString(4, passwordField.getText());

                int rowsInserted = insertStmt.executeUpdate();
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Registration Successful!");

                gotoLogin(actionEvent);

                return rowsInserted > 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            messageLabel.setText("Registration Error: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            return false;
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

    public void gotoLogin(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
