package com.example.loginui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CampusEventController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String u = usernameField.getText().trim();
        String p = passwordField.getText().trim();

        if (u.equals("admin") && p.equals("admin")) {
            try {
                Parent root = FXMLLoader.load(CampusEventApplication.class.getResource("dashboard-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 900, 600));
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Could not load dashboard-view.fxml");
            }
        } else {
            errorLabel.setText("Invalid login (try admin/admin)");
        }
    }
}