package com.example.loginui;

import com.example.loginui.userManagement.UserManagementView;
import com.example.loginui.eventManagement.EventManagementView;
import com.example.loginui.waitlistManagement.BookingManagementView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    private void go(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(CampusEventApplication.class.getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        go(event, "login-view.fxml");
    }

    @FXML
private void openUsers(ActionEvent event) {
    try {
        Parent root = new UserManagementView().build();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 900, 600));
    } catch (Exception e) {
        e.printStackTrace();
    }
}

@FXML
private void openEvents(ActionEvent event) {
    try {
        Parent root = new EventManagementView().build();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 900, 600));
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @FXML
    private void openBookings(ActionEvent event) {
        try {
            Parent root = new BookingManagementView().build();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openWaitlist(ActionEvent event) {
        System.out.println("Waitlist clicked");
    }
}