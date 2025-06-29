package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.*;

public class AdminOrders {

    @FXML
    private VBox ordersVBox;

    @FXML
    public void initialize() {
        loadOrders();
    }

    private void loadOrders() {
        ordersVBox.getChildren().clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String orderQuery = "SELECT order_id, address, total_amount, name FROM orders o JOIN users u ON o.user_id = u.id ";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            ResultSet orders = orderStmt.executeQuery();

            while (orders.next()) {
                int orderId = orders.getInt("order_id");
                String address = orders.getString("address");
                String userName = orders.getString("name");
                int total = orders.getInt("total_amount");

                VBox orderBox = new VBox(10);
                orderBox.setPadding(new Insets(15));
                orderBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");
                orderBox.setPrefWidth(1600);
                VBox.setMargin(orderBox, new Insets(0, 300, 0, 300));

                Label orderInfo = new Label("Order #" + orderId + " | User Name: " + userName + " | Address: " + address + " | Total: " + total + " Tk");
                orderInfo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
                orderBox.getChildren().add(orderInfo);

                String itemsQuery = "SELECT pname, quantity, price FROM order_items WHERE order_id = ?";
                PreparedStatement itemStmt = conn.prepareStatement(itemsQuery);
                itemStmt.setInt(1, orderId);
                ResultSet items = itemStmt.executeQuery();

                while (items.next()) {
                    String pname = items.getString("pname");
                    int qty = items.getInt("quantity");
                    int price = items.getInt("price");

                    Label itemLabel = new Label(" - " + pname + " | Qty: " + qty + " | Price: " + price);
                    itemLabel.setStyle("-fx-font-size: 20px;");
                    orderBox.getChildren().add(itemLabel);
                }


                Button deleteBtn = new Button("Mark as Done");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 20px;");
                deleteBtn.setOnAction(e -> {
                    deleteOrder(orderId);
                    loadOrders();
                });

                orderBox.getChildren().add(deleteBtn);
                ordersVBox.getChildren().add(orderBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrder(int orderId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            PreparedStatement deleteItems = conn.prepareStatement("DELETE FROM order_items WHERE order_id = ?");
            deleteItems.setInt(1, orderId);
            deleteItems.executeUpdate();

            PreparedStatement deleteOrder = conn.prepareStatement("DELETE FROM orders WHERE order_id = ?");
            deleteOrder.setInt(1, orderId);
            deleteOrder.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Deleted");
            alert.setHeaderText(null);
            alert.setContentText("Order #" + orderId + " marked as done.");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
