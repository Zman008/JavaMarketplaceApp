package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

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

    private int totalAmount = 0;

    @FXML
    public void initialize() {
        paymentMethod.getItems().addAll("bKash", "Cash on Delivery", "Credit Card");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT SUM(quantity * price) AS total FROM cart WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, LoggedInUser.getId());
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                totalAmount = result.getInt("total");
                totalAmountLabel.setText(totalAmount + " Tk");
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

        try (Connection conn = DatabaseConnection.getConnection()) {

            String insertOrder = "INSERT INTO orders (user_id, address, total_amount) VALUES (?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, LoggedInUser.getId());
            orderStmt.setString(2, addressField.getText());
            orderStmt.setInt(3, totalAmount);
            orderStmt.executeUpdate();

            ResultSet keys = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (keys.next()) {
                orderId = keys.getInt(1);
            }

            String getCart = "SELECT product_id, pname, quantity, price FROM cart WHERE user_id = ?";
            PreparedStatement cartStmt = conn.prepareStatement(getCart);
            cartStmt.setInt(1, LoggedInUser.getId());
            ResultSet cartItems = cartStmt.executeQuery();

            String insertItem = "INSERT INTO order_items (order_id, product_id, pname, quantity, price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(insertItem);

            while (cartItems.next()) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, cartItems.getInt("product_id"));
                itemStmt.setString(3, cartItems.getString("pname"));
                itemStmt.setInt(4, cartItems.getInt("quantity"));
                itemStmt.setInt(5, cartItems.getInt("price"));
                itemStmt.executeUpdate();
            }

            PreparedStatement clearCart = conn.prepareStatement("DELETE FROM cart WHERE user_id = ?");
            clearCart.setInt(1, LoggedInUser.getId());
            clearCart.executeUpdate();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Order Placed");
            alert.setHeaderText("Your order has been placed successfully!");
            alert.setContentText("Thank you for shopping with us.");
            alert.showAndWait();

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Checkout failed. Please try again.");
        }
    }
}
