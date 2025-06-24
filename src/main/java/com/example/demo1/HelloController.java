package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    public Button shopping_cart;
    @FXML
    private Button login;
    @FXML
    private Button register;
    FXMLLoader fxmlLoader;

    @FXML
    public void gotoLogin(ActionEvent e) throws IOException {
        if (LoggedInUser.getId() == 0) {
            fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        } else {
            fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
        }
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    @FXML
    public void gotoCart(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("cart-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
