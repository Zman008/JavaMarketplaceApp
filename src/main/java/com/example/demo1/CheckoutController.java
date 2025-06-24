package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CheckoutController {

    public Button confirmButton;
    public TextField addressField;
    @FXML
    private Label totalAmountLabel;

    @FXML
    private ChoiceBox<String> paymentMethod;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        paymentMethod.getItems().addAll("bKash", "Cash on Delivery", "Credit Card");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT SUM(quantity * price) AS total FROM cart WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, LoggedInUser.getId());
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                int total = result.getInt("total");
                totalAmountLabel.setText(total + " Tk");
            } else {
                totalAmountLabel.setText("0 Tk");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load total amount.");
        }
    }

    @FXML
    public void handleConfirm(ActionEvent e) throws IOException {
        if (addressField.getText().isEmpty()) {
            statusLabel.setText("You must enter your address");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
