package com.example.loginui.waitlistManagement;

import com.example.loginui.eventManagement.Event;
import com.example.loginui.eventManagement.EventManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class BookingManagementView {

    private final BookingManager bookingManager = new BookingManager();
    private final EventManager eventManager = new EventManager();

    private TextArea out;
    private TextField in;

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

        VBox root = new VBox(10,
                new Label("Booking Management"),
                out,
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
        out.appendText("""

                === Booking Management ===
                1) Add Booking
                2) View Event Roster
                3) View Event Waitlist
                4) Cancel Confirmed Booking
                5) Remove Booking From Waitlist
                0) Back
                Choose:
                """);
    }

    private void handleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> {
                out.appendText("Enter booking ID: ");
                state = State.ADD_BOOKING_ID;
            }
            case "2" -> {
                out.appendText("Enter event ID to view roster: ");
                state = State.VIEW_EVENT_ID;
            }
            case "3" -> {
                out.appendText("Enter event ID to view waitlist: ");
                state = State.VIEW_WAITLIST_EVENT_ID;
            }
            case "4" -> {
                out.appendText("Enter event ID: ");
                state = State.CANCEL_BOOKING_EVENT_ID;
            }
            case "5" -> {
                out.appendText("Enter event ID: ");
                state = State.REMOVE_WAITLIST_EVENT_ID;
            }
            case "0" -> {
                out.appendText("Back selected.\n");
                showMenu();
            }
            default -> {
                out.appendText("Invalid choice.\n");
                showMenu();
            }
        }
    }

    private void handleAddBookingId(String text) {
        if (text.isEmpty()) {
            out.appendText("Booking ID cannot be empty.\nEnter booking ID: ");
            return;
        }

        tmpBookingId = text;
        out.appendText("Enter user ID: ");
        state = State.ADD_USER_ID;
    }

    private void handleAddUserId(String text) {
        if (text.isEmpty()) {
            out.appendText("User ID cannot be empty.\nEnter user ID: ");
            return;
        }

        tmpUserId = text;
        out.appendText("Enter event ID: ");
        state = State.ADD_EVENT_ID;
    }

    private void handleAddEventId(String text) {
        if (text.isEmpty()) {
            out.appendText("Event ID cannot be empty.\nEnter event ID: ");
            return;
        }

        tmpEventId = text;

        Event event = eventManager.getEventById(tmpEventId);
        if (event == null) {
            out.appendText("Event not found.\n");
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
            out.appendText("Booking created. Status: " + booking.getStatusText() + "\n");
        } else {
            out.appendText("Failed to create booking.\n");
        }

        showMenu();
    }

    private void handleViewEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n");
            showMenu();
            return;
        }

        out.appendText("\n--- Event Roster ---\n");
        out.appendText("Event: " + event.getTitle() + " (" + event.getEventID() + ")\n");
        out.appendText("Confirmed (" + bookingManager.getConfirmedBookingsForEvent(event).size()
                + "/" + event.getCapacity() + "):\n");

        if (bookingManager.getConfirmedBookingsForEvent(event).isEmpty()) {
            out.appendText("(none)\n");
        } else {
            for (Booking b : bookingManager.getConfirmedBookingsForEvent(event)) {
                out.appendText(b.toString() + "\n");
            }
        }

        out.appendText("Waitlist:\n");
        if (bookingManager.getWaitlistedBookingsForEvent(event).isEmpty()) {
            out.appendText("(none)\n");
        } else {
            for (Booking b : bookingManager.getWaitlistedBookingsForEvent(event)) {
                out.appendText(b.toString() + "\n");
            }
        }

        showMenu();
    }

    private void handleViewWaitlistEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n");
            showMenu();
            return;
        }

        out.appendText("\n--- Waitlist for " + event.getTitle() + " ---\n");

        if (bookingManager.getWaitlistedBookingsForEvent(event).isEmpty()) {
            out.appendText("(empty)\n");
        } else {
            for (Booking b : bookingManager.getWaitlistedBookingsForEvent(event)) {
                out.appendText(b.toString() + "\n");
            }
        }

        showMenu();
    }

    private void handleCancelBookingEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n");
            showMenu();
            return;
        }

        tmpEventId = eventId;
        out.appendText("Enter confirmed booking ID to cancel: ");
        state = State.CANCEL_BOOKING_ID;
    }

    private void handleCancelBookingId(String bookingId) {
        Event event = eventManager.getEventById(tmpEventId);

        boolean ok = bookingManager.cancelConfirmedBooking(event, bookingId);
        out.appendText(ok ? "Confirmed booking cancelled.\n" : "Could not cancel confirmed booking.\n");
        showMenu();
    }

    private void handleRemoveWaitlistEventId(String eventId) {
        Event event = eventManager.getEventById(eventId);

        if (event == null) {
            out.appendText("Event not found.\n");
            showMenu();
            return;
        }

        tmpEventId = eventId;
        out.appendText("Enter waitlisted booking ID to remove: ");
        state = State.REMOVE_WAITLIST_BOOKING_ID;
    }

    private void handleRemoveWaitlistBookingId(String bookingId) {
        Event event = eventManager.getEventById(tmpEventId);

        boolean ok = bookingManager.removeFromWaitlist(event, bookingId);
        out.appendText(ok ? "Waitlisted booking removed.\n" : "Could not remove waitlisted booking.\n");
        showMenu();
    }
}