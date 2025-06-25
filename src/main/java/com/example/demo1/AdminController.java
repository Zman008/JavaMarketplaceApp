package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminController {


    @FXML
    private TextField productNameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField imagePathField;

    @FXML
    private Label messageLabel;

    @FXML
    public void addProduct() {
        String name = productNameField.getText().trim();
        String priceText = priceField.getText().trim();
        String imagePath = imagePathField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || imagePath.isEmpty()) {
            messageLabel.setText("All fields are required.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            int price = Integer.parseInt(priceText);

            String sql = "INSERT INTO product (product_name, price, image_path) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setInt(2, price);
                stmt.setString(3, "images/" + imagePath + ".png");
                stmt.executeUpdate();

                messageLabel.setText("Product added successfully!");
                messageLabel.setTextFill(Color.GREEN);

                productNameField.clear();
                priceField.clear();
                imagePathField.clear();
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Price must be a number.");
            messageLabel.setTextFill(Color.RED);
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to add product.");
            messageLabel.setTextFill(Color.RED);
        }
    }
}
