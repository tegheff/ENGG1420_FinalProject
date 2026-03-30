package com.example.loginui.userManagement;

import com.example.loginui.CampusEventApplication;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserManagementView {

    private final UserManager userManager = new UserManager();

    private TextArea out;
    private TextField in;
    private HBox menuButtons;
    private HBox typeButtons;

    // --- simple state machine ---
    private enum State {
        MENU,
        ADD_ID, ADD_NAME, ADD_EMAIL, ADD_TYPE,
        VIEW_ID,
        DONE
    }

    private State state = State.MENU;

    // temp fields for Add User
    private String tmpId, tmpName, tmpEmail;

    public Parent build() {
        out = new TextArea();
        out.setEditable(false);
        out.setPrefHeight(420);

        in = new TextField();
        in.setPromptText("Type here...");

        Button send = new Button("Send");
        send.setOnAction(e -> handleInput());
        in.setOnAction(e -> handleInput());

        Button addUserBtn = new Button("Add User");
        addUserBtn.setOnAction(e -> startAddUser());

        Button viewUserBtn = new Button("View User Details");
        viewUserBtn.setOnAction(e -> startViewUser());

        Button listUsersBtn = new Button("List All Users");
        listUsersBtn.setOnAction(e -> {
            listAllUsers();
            showMenu();
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            out.appendText("Back selected.\n");
            showMenu();
        });

        Button mainManagementBtn = new Button("Main Management");
        mainManagementBtn.setOnAction(e -> goToMainManagement(mainManagementBtn));

        menuButtons = new HBox(10, addUserBtn, viewUserBtn, listUsersBtn, backBtn, mainManagementBtn);

        Button studentBtn = new Button("Student");
        studentBtn.setOnAction(e -> handleAddType("1"));

        Button staffBtn = new Button("Staff");
        staffBtn.setOnAction(e -> handleAddType("2"));

        Button guestBtn = new Button("Guest");
        guestBtn.setOnAction(e -> handleAddType("3"));

        typeButtons = new HBox(10, studentBtn, staffBtn, guestBtn);
        typeButtons.setVisible(false);
        typeButtons.setManaged(false);

        VBox root = new VBox(10,
                new Label("User Management"),
                out,
                menuButtons,
                typeButtons,
                new HBox(10, in, send)
        );
        root.setPadding(new Insets(10));

        showMenu();
        return root;
    }

    private void handleInput() {
        String text = in.getText().trim();
        in.clear();

        switch (state) {
            case MENU -> handleMenuChoice(text);
            case ADD_ID -> handleAddId(text);
            case ADD_NAME -> handleAddName(text);
            case ADD_EMAIL -> handleAddEmail(text);
            case ADD_TYPE -> handleAddType(text);
            case VIEW_ID -> handleViewId(text);
            default -> { /* no-op */ }
        }
    }

    private void showMenu() {
        state = State.MENU;
        typeButtons.setVisible(false);
        typeButtons.setManaged(false);
        out.appendText("Choose an action using the buttons above.\n");
    }

    private void handleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> {
                out.appendText("Enter userId: \n");
                state = State.ADD_ID;
            }
            case "2" -> {
                out.appendText("Enter userId to view: \n");
                state = State.VIEW_ID;
            }
            case "3" -> {
                listAllUsers();
                showMenu();
            }
            case "0" -> {
                out.appendText("Back selected.\n");
                // For demo: just return to menu. (If you want to go back to dashboard, we’ll wire it next.)
                showMenu();
            }
            default -> {
                out.appendText("Invalid choice.\n");
                showMenu();
            }
        }
    }

    private void startAddUser() {
        out.appendText("Enter userId: \n");
        state = State.ADD_ID;
    }

    private void startViewUser() {
        out.appendText("Enter userId to view: \n");
        state = State.VIEW_ID;
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

    // ----- Add User flow -----

    private void handleAddId(String id) {
        if (id.isEmpty()) {
            out.appendText("userId cannot be empty.\nEnter userId: ");
            return;
        }
        if (userManager.getUserById(id) != null) {
            out.appendText("That userId already exists.\nEnter userId: ");
            return;
        }
        tmpId = id;
        out.appendText("Enter name: \n");
        state = State.ADD_NAME;
    }

    private void handleAddName(String name) {
        tmpName = name;
        out.appendText("Enter email: \n");
        state = State.ADD_EMAIL;
    }

    private void handleAddEmail(String email) {
        tmpEmail = email;
        out.appendText("Select user type using the buttons below.\n");
        typeButtons.setVisible(true);
        typeButtons.setManaged(true);
        state = State.ADD_TYPE;
    }

    private void handleAddType(String typeChoice) {
        User user;
        switch (typeChoice) {
            case "1" -> user = new Student(tmpId, tmpName, tmpEmail);
            case "2" -> user = new Staff(tmpId, tmpName, tmpEmail);
            case "3" -> user = new Guest(tmpId, tmpName, tmpEmail);
            default -> {
                out.appendText("Invalid type.\n");
                showMenu();
                return;
            }
        }

        boolean ok = userManager.addUser(user);
        out.appendText(ok ? "User created.\n" : "Failed to create user.\n");
        showMenu();
    }

    // ----- View User -----

    private void handleViewId(String id) {
        User user = userManager.getUserById(id);
        if (user == null) {
            out.appendText("User not found.\n");
        } else {
            out.appendText("""
                    
                    --- User Details ---
                    """);
            out.appendText("User ID: " + user.getUserId() + "\n");
            out.appendText("Name: " + user.getName() + "\n");
            out.appendText("Email: " + user.getEmail() + "\n");
            out.appendText("Type: " + user.getType() + "\n");
            out.appendText("Bookings summary: (available in Booking module later)\n");
        }
        showMenu();
    }

    // ----- List Users -----

    private void listAllUsers() {
        var users = userManager.getAllUsers();
        if (users.isEmpty()) {
            out.appendText("No users registered.\n");
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
    }
}
