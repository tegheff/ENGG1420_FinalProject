package com.example.loginui.waitlistManagement;

import com.example.loginui.AppState;
import com.example.loginui.CampusEventApplication;
import com.example.loginui.eventManagement.Event;
import com.example.loginui.eventManagement.EventManager;
import java.time.LocalDateTime;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BookingManagementView {

    private final BookingManager bookingManager = AppState.getBookingManager();
    private final EventManager eventManager = AppState.getEventManager();

    private TextArea out;
    private HBox menuButtons;

    public Parent build() {
        out = new TextArea();
        out.setEditable(false);
        out.setPrefHeight(420);

        Button mainManagementBtn = new Button("Main Management");
        mainManagementBtn.setOnAction(e -> goToMainManagement(mainManagementBtn));

        Button addBookingBtn = new Button("Add Booking");
        addBookingBtn.setOnAction(e -> addBooking());

        Button viewRosterBtn = new Button("View Event Roster");
        viewRosterBtn.setOnAction(e -> viewRoster());

        Button viewWaitlistBtn = new Button("View Event Waitlist");
        viewWaitlistBtn.setOnAction(e -> viewWaitlist());

        Button cancelBookingBtn = new Button("Cancel Confirmed Booking");
        cancelBookingBtn.setOnAction(e -> cancelConfirmedBooking());

        Button removeWaitlistBtn = new Button("Remove Booking From Waitlist");
        removeWaitlistBtn.setOnAction(e -> removeFromWaitlist());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> out.appendText("Back selected.\n\n"));

        menuButtons = new HBox(10, addBookingBtn, viewRosterBtn, viewWaitlistBtn,
                cancelBookingBtn, removeWaitlistBtn, backBtn);

        VBox root = new VBox(10,
                new Label("Booking Management"),
                mainManagementBtn,
                menuButtons,
                out,
                new HBox()
        );
        root.setPadding(new Insets(10));

        out.appendText("Choose an action using the buttons above.\n\n");
        return root;
    }

    private void goToMainManagement(Node source) {
        try {
            Parent root = FXMLLoader.load(CampusEventApplication.class.getResource("dashboard-view.fxml"));
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
            out.appendText("Could not load main management view.\n\n");
        }
    }

    private void addBooking() {
        if (eventManager.getEvents().isEmpty()) {
            out.appendText("No events available. Add an event first.\n\n");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Booking");
        dialog.setHeaderText("Enter booking details");

        TextField bookingIdField = new TextField();
        bookingIdField.setPromptText("Booking ID");
        bookingIdField.setText(bookingManager.nextBookingId());
        bookingIdField.setEditable(false);

        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");

        ComboBox<Event> eventBox = new ComboBox<>();
        eventBox.getItems().addAll(eventManager.getEvents());
        eventBox.setPromptText("Select Event");
        eventBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Event event) {
                if (event == null) {
                    return "";
                }
                return event.getTitle() + " (" + event.getEventID() + ")";
            }

            @Override
            public Event fromString(String string) {
                return null;
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.getChildren().addAll(
                new Label("Booking ID:"), bookingIdField,
                new Label("User ID:"), userIdField,
                new Label("Event:"), eventBox
        );

        dialog.getDialogPane().setContent(dialogLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String bookingId = bookingIdField.getText().trim();
                String userId = userIdField.getText().trim();
                Event event = eventBox.getValue();

                if (bookingId.isEmpty() || userId.isEmpty() || event == null) {
                    out.appendText("All fields are required.\n\n");
                    return;
                }

                String eventId = event.getEventID();

                Booking booking = new Booking(
                        bookingId,
                        userId,
                        eventId,
                        LocalDateTime.now()
                );

                boolean ok = bookingManager.addBooking(event, booking);
                if (ok) {
                    out.appendText("Booking created. Status: " + booking.getStatusText() + "\n\n");
                } else {
                    if (bookingManager.bookingIdExistsForEvent(bookingId, eventId)) {
                        out.appendText("Booking ID already exists for this event. Use a different ID.\n\n");
                    } else if (bookingManager.isEventCancelled(event)) {
                        out.appendText("Event is cancelled. Booking not allowed.\n\n");
                    } else if (bookingManager.hasActiveBookingForUser(event, userId)) {
                        out.appendText("User already has an active booking for this event.\n\n");
                    } else {
                        out.appendText("Failed to create booking.\n\n");
                    }
                }
            }
        });
    }

    private void viewRoster() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View Event Roster");
        dialog.setHeaderText("Enter event ID");
        dialog.setContentText("Event ID:");

        dialog.showAndWait().ifPresent(eventId -> {
            Event event = eventManager.getEventById(eventId.trim());

            if (event == null) {
                out.appendText("Event not found.\n\n");
                return;
            }

            out.appendText("\n--- Event Roster ---\n");
            out.appendText("Event: " + event.getTitle() + " (" + event.getEventID() + ")\n");
            out.appendText("Confirmed (" + bookingManager.getConfirmedBookingsForEvent(event).size()
                    + "/" + event.getCapacity() + "):\n");

            if (bookingManager.getConfirmedBookingsForEvent(event).isEmpty()) {
                out.appendText("(none)\n\n");
            } else {
                for (Booking b : bookingManager.getConfirmedBookingsForEvent(event)) {
                    out.appendText(b.toString() + "\n\n");
                }
            }

            out.appendText("Waitlist:\n");
            if (bookingManager.getWaitlistedBookingsForEvent(event).isEmpty()) {
                out.appendText("(none)\n\n");
            } else {
                for (Booking b : bookingManager.getWaitlistedBookingsForEvent(event)) {
                    out.appendText(b.toString() + "\n\n");
                }
            }
        });
    }

    private void viewWaitlist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View Waitlist");
        dialog.setHeaderText("Enter event ID");
        dialog.setContentText("Event ID:");

        dialog.showAndWait().ifPresent(eventId -> {
            Event event = eventManager.getEventById(eventId.trim());

            if (event == null) {
                out.appendText("Event not found.\n\n");
                return;
            }

            out.appendText("\n--- Waitlist for " + event.getTitle() + " ---\n");

            if (bookingManager.getWaitlistedBookingsForEvent(event).isEmpty()) {
                out.appendText("(empty)\n\n");
            } else {
                for (Booking b : bookingManager.getWaitlistedBookingsForEvent(event)) {
                    out.appendText(b.toString() + "\n\n");
                }
            }
        });
    }

    private void cancelConfirmedBooking() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cancel Confirmed Booking");
        dialog.setHeaderText("Enter event and booking IDs");

        TextField eventIdField = new TextField();
        eventIdField.setPromptText("Event ID");

        TextField bookingIdField = new TextField();
        bookingIdField.setPromptText("Booking ID");

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.getChildren().addAll(
                new Label("Event ID:"), eventIdField,
                new Label("Booking ID:"), bookingIdField
        );

        dialog.getDialogPane().setContent(dialogLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String eventId = eventIdField.getText().trim();
                String bookingId = bookingIdField.getText().trim();

                if (eventId.isEmpty() || bookingId.isEmpty()) {
                    out.appendText("All fields are required.\n\n");
                    return;
                }

                Event event = eventManager.getEventById(eventId);
                if (event == null) {
                    out.appendText("Event not found.\n\n");
                    return;
                }

                boolean ok = bookingManager.cancelConfirmedBooking(event, bookingId);
                out.appendText(ok ? "Confirmed booking cancelled.\n\n"
                        : "Could not cancel confirmed booking.\n\n");
            }
        });
    }

    private void removeFromWaitlist() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Remove Waitlisted Booking");
        dialog.setHeaderText("Enter event and booking IDs");

        TextField eventIdField = new TextField();
        eventIdField.setPromptText("Event ID");

        TextField bookingIdField = new TextField();
        bookingIdField.setPromptText("Booking ID");

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.getChildren().addAll(
                new Label("Event ID:"), eventIdField,
                new Label("Booking ID:"), bookingIdField
        );

        dialog.getDialogPane().setContent(dialogLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String eventId = eventIdField.getText().trim();
                String bookingId = bookingIdField.getText().trim();

                if (eventId.isEmpty() || bookingId.isEmpty()) {
                    out.appendText("All fields are required.\n\n");
                    return;
                }

                Event event = eventManager.getEventById(eventId);
                if (event == null) {
                    out.appendText("Event not found.\n\n");
                    return;
                }

                boolean ok = bookingManager.removeFromWaitlist(event, bookingId);
                out.appendText(ok ? "Waitlisted booking removed.\n\n"
                        : "Could not remove waitlisted booking.\n\n");
            }
        });
    }
}
