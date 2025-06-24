package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class ProductsController {

    @FXML
    private FlowPane productsFlowPane;

    @FXML
    public void initialize() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM product";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("product_name");
                int price = rs.getInt("price");
                String imgPath = rs.getString("image_path");

                VBox productTile = createProductTile(productId, name, price, imgPath);
                productsFlowPane.getChildren().add(productTile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductTile(int productId, String name, int price, String imgPath) {
        ImageView imgView = null;

        try {
            Image img = new Image(getClass().getResourceAsStream("/" + imgPath));
            imgView = new ImageView(img);
            imgView.setFitHeight(150);
            imgView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Failed to load image: " + imgPath);
            imgView = new ImageView();
        }

        Label nameLabel = new Label(name);
        nameLabel.setFont(new Font("Arial", 18));
        nameLabel.setWrapText(true);

        Label priceLabel = new Label(price + " Tk");
        priceLabel.setTextFill(Color.DARKGREEN);
        priceLabel.setFont(new Font(16));

        Spinner<Integer> quantitySpinner = new Spinner<>();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        quantitySpinner.setPrefWidth(50);

        Button addToCart = new Button("Add to Cart");
        addToCart.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        addToCart.setOnAction(e -> {
            if (LoggedInUser.getId() == 0) {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setMaximized(true);
            } else {

                int quantity = quantitySpinner.getValue();
                addProductToCart(productId, name, price, quantity);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Item Added");
                alert.setHeaderText(null);
                alert.setContentText(name + " x " + quantity + " added to cart.");
                alert.showAndWait();
            }
        });

        HBox hb = new HBox(15, quantitySpinner, addToCart);
        hb.setAlignment(Pos.CENTER);

        VBox tile = new VBox(10, imgView, nameLabel, priceLabel, hb);
        tile.setPadding(new Insets(15));
        tile.setAlignment(Pos.CENTER);
        tile.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5px;");
        tile.setPrefSize(350, 300);

        return tile;
    }

    private void addProductToCart(int productId, String name, int price, int quantity) {


        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO cart (user_id, product_id, pname, price, quantity) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, LoggedInUser.getId());
            stmt.setInt(2, productId);
            stmt.setString(3, name);
            stmt.setInt(4, price);
            stmt.setInt(5, quantity);
            stmt.executeUpdate();
            System.out.println("Added to cart: " + name + " x" + quantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
