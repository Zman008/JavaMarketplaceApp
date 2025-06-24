package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    public Button logout;
    @FXML
    private Label username;
    @FXML
    private Label email;
    @FXML
    private Label phone;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserInfo();
    }

    private void loadUserInfo() {
        username.setText(LoggedInUser.getUsername());
        email.setText(LoggedInUser.getEmail());
        phone.setText(LoggedInUser.getPhone());
    }

    public void logout(ActionEvent e) throws IOException {
        LoggedInUser.clear();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    public void gotoCart(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader;
        if (LoggedInUser.getId() == 0) {
            fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        } else {
            fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("cart-view.fxml"));
        }
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    @FXML
    public void editProfile(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("edit-profile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
