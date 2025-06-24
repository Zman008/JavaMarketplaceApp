package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartController {
    @FXML
    public VBox cartItemsVBox;
    @FXML
    public Label totalLabel;
    public ChoiceBox<String> removeChoice;
    public Button removeBtn;
    public Button checkoutBtn;

    public void initialize() throws SQLException {
        String sql = "SELECT id, quantity, pname, price FROM cart WHERE user_id = ?";
        int priceTotal = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int userId = LoggedInUser.getId();
            stmt.setInt(1, userId);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                int quantity = result.getInt("quantity");
                int price = result.getInt("price");
                int subtotal = quantity * price;
                priceTotal += subtotal;

                removeChoice.getItems().add(result.getString("pname"));
                Label nameLabel = new Label(result.getString("pname"));
                Label quantityLabel = new Label("" + quantity);
                Label priceLabel = new Label(price + " Tk");
                Label subtotalLabel = new Label(subtotal + " Tk");

                // Styling all labels
                Label[] labels = {nameLabel, quantityLabel, priceLabel, subtotalLabel};
                for (Label label : labels) {
                    label.setFont(new Font("Segoe UI", 20));
                    label.setPrefWidth(480);
                    label.setAlignment(Pos.CENTER);
                    label.setContentDisplay(ContentDisplay.CENTER);
                    label.setMaxWidth(Double.MAX_VALUE);
                }

                HBox hbox = new HBox(10);
                hbox.getChildren().addAll(nameLabel, priceLabel, quantityLabel, subtotalLabel);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPrefHeight(80);
                hbox.setPadding(new Insets(10, 20, 10, 20));
                hbox.setStyle("""
                    -fx-background-color: #ffffff;
                    -fx-border-color: #cccccc;
                    -fx-border-radius: 5px;
                    -fx-background-radius: 5px;
                """);

                cartItemsVBox.getChildren().add(hbox);
            }

            totalLabel.setText(priceTotal + " Tk");
        }
    }

    @FXML
    public void remove(ActionEvent event) {
        String selectedProduct = removeChoice.getValue();

        if (selectedProduct == null || selectedProduct.equals("---Select Item---")) {
            System.out.println("No item selected.");
            return;
        }

        int userId = LoggedInUser.getId(); 

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cart WHERE user_id = ? AND pname = ?")) {

            stmt.setInt(1, userId);
            stmt.setString(2, selectedProduct);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Item removed: " + selectedProduct);
                removeChoice.getItems().remove(selectedProduct);
                refreshCart(); 
            } else {
                System.out.println("Item not found in cart.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCart() {
        cartItemsVBox.getChildren().clear();

        String sql = "SELECT id, quantity, pname, price FROM cart WHERE user_id = ?";
        int priceTotal = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int userId = LoggedInUser.getId();
            stmt.setInt(1, userId);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                int quantity = result.getInt("quantity");
                int price = result.getInt("price");
                int subtotal = quantity * price;
                priceTotal += subtotal;

                Label nameLabel = new Label(result.getString("pname"));
                Label quantityLabel = new Label("" + quantity);
                Label priceLabel = new Label(price + " Tk");
                Label subtotalLabel = new Label(subtotal + " Tk");

                // Styling all labels
                Label[] labels = {nameLabel, quantityLabel, priceLabel, subtotalLabel};
                for (Label label : labels) {
                    label.setFont(new Font("Segoe UI", 20));
                    label.setPrefWidth(480);
                    label.setAlignment(Pos.CENTER);
                    label.setContentDisplay(ContentDisplay.CENTER);
                    label.setMaxWidth(Double.MAX_VALUE);
                }

                HBox hbox = new HBox(10);
                hbox.getChildren().addAll(nameLabel, priceLabel, quantityLabel, subtotalLabel);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setPrefHeight(80);
                hbox.setPadding(new Insets(10, 20, 10, 20));
                hbox.setStyle("""
                    -fx-background-color: #ffffff;
                    -fx-border-color: #cccccc;
                    -fx-border-radius: 5px;
                    -fx-background-radius: 5px;
                """);

                cartItemsVBox.getChildren().add(hbox);
            }

            totalLabel.setText(priceTotal + " Tk");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void checkout(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("checkout-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
