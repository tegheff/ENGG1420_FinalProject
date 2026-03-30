package com.example.loginui.userManagement;

import com.example.loginui.AppState;
import com.example.loginui.CampusEventApplication;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserManagementView {

    private final UserManager userManager = AppState.getUserManager();

    private TextArea out;
    private HBox menuButtons;

    public Parent build() {
        out = new TextArea();
        out.setEditable(false);
        out.setPrefHeight(420);

        Button addUserBtn = new Button("Add User");
        addUserBtn.setOnAction(e -> addUser());

        Button viewUserBtn = new Button("View User Details");
        viewUserBtn.setOnAction(e -> viewUser());

        Button listUsersBtn = new Button("List All Users");
        listUsersBtn.setOnAction(e -> {
            listAllUsers();
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            out.appendText("Back selected.\n\n");
        });

        Button mainManagementBtn = new Button("Main Management");
        mainManagementBtn.setOnAction(e -> goToMainManagement(mainManagementBtn));

        menuButtons = new HBox(10, addUserBtn, viewUserBtn, listUsersBtn, backBtn, mainManagementBtn);

        VBox root = new VBox(10,
                new Label("User Management"),
                out,
                menuButtons,
                new HBox()
        );
        root.setPadding(new Insets(10));

        out.appendText("Choose an action using the buttons below.\n\n");
        return root;
    }

    private void goToMainManagement(Node source) {
        try {
            Parent root = FXMLLoader.load(CampusEventApplication.class.getResource("dashboard-view.fxml"));
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
            out.appendText("Could not load main management view.\n");
        }
    }

    private void addUser() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Enter user details");

        TextField idField = new TextField();
        idField.setPromptText("User ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Student", "Staff", "Guest");
        typeBox.setPromptText("User Type");

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.getChildren().addAll(
                new Label("User ID:"), idField,
                new Label("Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Type:"), typeBox
        );

        dialog.getDialogPane().setContent(dialogLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String type = typeBox.getValue();

                if (id.isEmpty() || name.isEmpty() || email.isEmpty() || type == null) {
                    out.appendText("All fields are required.\n\n");
                    return;
                }
                if (userManager.getUserById(id) != null) {
                    out.appendText("That userId already exists.\n\n");
                    return;
                }

                User user;
                switch (type) {
                    case "Student" -> user = new Student(id, name, email);
                    case "Staff" -> user = new Staff(id, name, email);
                    case "Guest" -> user = new Guest(id, name, email);
                    default -> {
                        out.appendText("Invalid user type.\n\n");
                        return;
                    }
                }

                boolean ok = userManager.addUser(user);
                out.appendText(ok ? "User created.\n\n" : "Failed to create user.\n\n");
            }
        });
    }

    // ----- View User -----

    private void viewUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View User");
        dialog.setHeaderText("View user details");
        dialog.setContentText("User ID:");

        dialog.showAndWait().ifPresent(id -> {
            User user = userManager.getUserById(id.trim());
            if (user == null) {
                out.appendText("User not found.\n\n");
                return;
            }
            out.appendText("""
                    
                    --- User Details ---
                    """);
            out.appendText("User ID: " + user.getUserId() + "\n");
            out.appendText("Name: " + user.getName() + "\n");
            out.appendText("Email: " + user.getEmail() + "\n");
            out.appendText("Type: " + user.getType() + "\n");
            out.appendText("Bookings summary: (available in Booking module later)\n\n");
        });
    }

    // ----- List Users -----

    private void listAllUsers() {
        var users = userManager.getAllUsers();
        if (users.isEmpty()) {
            out.appendText("No users registered.\n\n");
            return;
        }
        out.appendText("""
                
                --- All Users ---
                userId | name | email | type
                ------------------------------------------
                """);
        for (User u : users) {
            out.appendText(u.toString() + "\n");
        }
        out.appendText("\n");
    }
}
