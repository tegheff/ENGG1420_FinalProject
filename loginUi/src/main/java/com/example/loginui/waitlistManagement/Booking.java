package com.example.loginui.waitlistManagement;

import java.time.LocalDateTime;

public class Booking {
    private final String bookingID;
    private final String userID;
    private final String eventID;
    private final LocalDateTime createdAt;
    private int bookingStatus; // 1 = Confirmed, 2 = Waitlisted, 3 = Cancelled

    public Booking(String bookingID, String userID, String eventID, LocalDateTime createdAt) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.eventID = eventID;
        this.createdAt = createdAt;
        this.bookingStatus = 0;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getUserID() {
        return userID;
    }

    public String getEventID() {
        return eventID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getStatusText() {
        switch (bookingStatus) {
            case 1:
                return "Confirmed";
            case 2:
                return "Waitlisted";
            case 3:
                return "Cancelled";
            default:
                return "Unset";
        }
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingID +
                ", User ID: " + userID +
                ", Event ID: " + eventID +
                ", Created At: " + createdAt +
                ", Status: " + getStatusText();
    }
}