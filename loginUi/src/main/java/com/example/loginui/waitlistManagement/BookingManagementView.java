package com.example.loginui.waitlistManagement;

import com.example.loginui.CampusEventApplication;
import com.example.loginui.eventManagement.Event;
import com.example.loginui.eventManagement.EventManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class BookingManagementView {

    private final BookingManager bookingManager = new BookingManager();
    private final EventManager eventManager = new EventManager();

    private TextArea out;
    private TextField in;
    private HBox menuButtons;

    private enum State {
        MENU,
        ADD_BOOKING_ID, ADD_USER_ID, ADD_EVENT_ID,
        VIEW_EVENT_ID,
        VIEW_WAITLIST_EVENT_ID,
        CANCEL_BOOKING_EVENT_ID, CANCEL_BOOKING_ID,
        REMOVE_WAITLIST_EVENT_ID, REMOVE_WAITLIST_BOOKING_ID
    }

    private State state = State.MENU;

    private String tmpBookingId;
    private String tmpUserId;
    private String tmpEventId;

    public Parent build() {
        out = new TextArea();
        out.setEditable(false);
        out.setPrefHeight(420);

        in = new TextField();
        in.setPromptText("Type here...");

        Button send = new Button("Send");
        send.setOnAction(e -> handleInput());
        in.setOnAction(e -> handleInput());

        Button mainManagementBtn = new Button("Main Management");
        mainManagementBtn.setOnAction(e -> goToMainManagement(mainManagementBtn));

        Button addBookingBtn = new Button("Add Booking");
        addBookingBtn.setOnAction(e -> startAddBooking());

        Button viewRosterBtn = new Button("View Event Roster");
        viewRosterBtn.setOnAction(e -> startViewRoster());

        Button viewWaitlistBtn = new Button("View Event Waitlist");
        viewWaitlistBtn.setOnAction(e -> startViewWaitlist());

        Button cancelBookingBtn = new Button("Cancel Confirmed Booking");
        cancelBookingBtn.setOnAction(e -> startCancelBooking());

        Button removeWaitlistBtn = new Button("Remove Booking From Waitlist");
        removeWaitlistBtn.setOnAction(e -> startRemoveWaitlist());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            out.appendText("Back selected.\n");
            showMenu();
        });

        menuButtons = new HBox(10, addBookingBtn, viewRosterBtn, viewWaitlistBtn,
                cancelBookingBtn, removeWaitlistBtn, backBtn);

        VBox root = new VBox(10,
                new Label("Booking Management"),
                mainManagementBtn,
                menuButtons,
                out,
                new HBox(10, in, send)
        );
        root.setPadding(new Insets(10));

        showMenu();
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

    private void handleInput() {
        String text = in.getText().trim();
        in.clear();

        switch (state) {
            case MENU -> handleMenuChoice(text);
            case ADD_BOOKING_ID -> handleAddBookingId(text);
            case ADD_USER_ID -> handleAddUserId(text);
            case ADD_EVENT_ID -> handleAddEventId(text);
            case VIEW_EVENT_ID -> handleViewEventId(text);
            case VIEW_WAITLIST_EVENT_ID -> handleViewWaitlistEventId(text);
            case CANCEL_BOOKING_EVENT_ID -> handleCancelBookingEventId(text);
            case CANCEL_BOOKING_ID -> handleCancelBookingId(text);
            case REMOVE_WAITLIST_EVENT_ID -> handleRemoveWaitlistEventId(text);
            case REMOVE_WAITLIST_BOOKING_ID -> handleRemoveWaitlistBookingId(text);
        }
    }

    private void showMenu() {
        state = State.MENU;
        out.appendText("Choose an action using the buttons above.\n\n");
    }

    private void handleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> {
                out.appendText("Enter booking ID:\n");
                state = State.ADD_BOOKING_ID;
            }
            case "2" -> {
                out.appendText("Enter event ID to view roster:\n");
                state = State.VIEW_EVENT_ID;
            }
            case "3" -> {
                out.appendText("Enter event ID to view waitlist:\n");
                state = State.VIEW_WAITLIST_EVENT_ID;
            }
            case "4" -> {
                out.appendText("Enter event ID:\n");
                state = State.CANCEL_BOOKING_EVENT_ID;
            }
            case "5" -> {
                out.appendText("Enter event ID:\n");
                state = State.REMOVE_WAITLIST_EVENT_ID;
            }
            case "0" -> {
                out.appendText("Back selected.\n\n");
                showMenu();
            }
            default -> {
                out.appendText("Invalid choice.\n\n");
                showMenu();
            }
        }
    }

    private void startAddBooking() {
        out.appendText("Enter booking ID:\n");
        state = State.ADD_BOOKING_ID;
    }

    private void startViewRoster() {
        out.appendText("Enter event ID to view roster:\n");
        state = State.VIEW_EVENT_ID;
    }

    private void startViewWaitlist() {
        out.appendText("Enter event ID to view waitlist:\n");
        state = State.VIEW_WAITLIST_EVENT_ID;
    }

    private void startCancelBooking() {
        out.appendText("Enter event ID:\n");
        state = State.CANCEL_BOOKING_EVENT_ID;
    }

    private void startRemoveWaitlist() {
        out.appendText("Enter event ID:\n");
        state = State.REMOVE_WAITLIST_EVENT_ID;
    }

    private void handleAddBookingId(String text) {
        if (text.isEmpty()) {
            out.appendText("Booking ID cannot be empty.\nEnter booking ID:\n");
            return;
        }

        tmpBookingId = text;
        out.appendText("Enter user ID:\n");
        state = State.ADD_USER_ID;
    }

    private void handleAddUserId(String text) {
        if (text.isEmpty()) {
            out.appendText("User ID cannot be empty.\nEnter user ID:\n");
            return;
        }

        tmpUserId = text;
        out.appendText("Enter event ID:\n");
        state = State.ADD_EVENT_ID;
    }

    private void handleAddEventId(String text) {
        if (text.isEmpty()) {
            out.appendText("Event ID cannot be empty.\nEnter event ID:\n");
            return;
        }

        tmpEventId = text;

        Event event = eventManager.getEventById(tmpEventId);
        if (event == null) {
            out.appendText("Event not found.\n\n");
            showMenu();
            return;
        }

        Booking booking = new Booking(
                tmpBookingId,
                tmpUserId,
                tmpEventId,
                LocalDateTime.now()
        );

        boolean ok = bookingManager.addBooking(event, booking);

        if (ok) {
            out.appendText("Booking created. Status: " + booking.getStatusText() + "\n\n");
        } else {
            out.appendText("Failed to create booking.\n\n");
        }

        showMenu();
    }

    private void handleViewEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n\n");
            showMenu();
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

        showMenu();
    }

    private void handleViewWaitlistEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n\n");
            showMenu();
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

        showMenu();
    }

    private void handleCancelBookingEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n\n");
            showMenu();
            return;
        }

        tmpEventId = eventId;
        out.appendText("Enter confirmed booking ID to cancel:\n");
        state = State.CANCEL_BOOKING_ID;
    }

    private void handleCancelBookingId(String bookingId) {
        Event event = eventManager.getEventById(tmpEventId);

        boolean ok = bookingManager.cancelConfirmedBooking(event, bookingId);
        out.appendText(ok ? "Confirmed booking cancelled.\n\n" : "Could not cancel confirmed booking.\n\n");
        showMenu();
    }

    private void handleRemoveWaitlistEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n\n");
            showMenu();
            return;
        }

        tmpEventId = eventId;
        out.appendText("Enter waitlisted booking ID to remove:\n");
        state = State.REMOVE_WAITLIST_BOOKING_ID;
    }

    private void handleRemoveWaitlistBookingId(String bookingId) {
        Event event = eventManager.getEventById(tmpEventId);

        boolean ok = bookingManager.removeFromWaitlist(event, bookingId);
        out.appendText(ok ? "Waitlisted booking removed.\n\n" : "Could not remove waitlisted booking.\n\n");
        showMenu();
    }
}
