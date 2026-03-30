package com.example.loginui.waitlistManagement;

import com.example.loginui.eventManagement.Event;

import java.util.ArrayList;
import java.util.Comparator;

public class BookingManager {
    private final ArrayList<Booking> allBookings;

    public BookingManager() {
        this.allBookings = new ArrayList<>();
    }

    public ArrayList<Booking> getAllBookings() {
        return allBookings;
    }

    public boolean isEventCancelled(Event event) {
        if (event == null) {
            return true;
        }

        String status = event.getStatus();
        return status != null && status.equalsIgnoreCase("Cancelled");
    }

    public boolean hasActiveBookingForUser(Event event, String userID) {
        if (event == null || userID == null) {
            return false;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getUserID().equals(userID)
                    && booking.getBookingStatus() != 3) {
                return true;
            }
        }

        return false;
    }

    public boolean addBooking(Event event, Booking booking) {
        if (event == null || booking == null) {
            return false;
        }

        if (isEventCancelled(event)) {
            return false;
        }

        if (hasActiveBookingForUser(event, booking.getUserID())) {
            return false;
        }

        if (getConfirmedBookingsForEvent(event).size() < event.getCapacity()) {
            booking.setBookingStatus(1); // Confirmed
        } else {
            booking.setBookingStatus(2); // Waitlisted
        }

        allBookings.add(booking);
        return true;
    }

    public boolean removeFromWaitlist(Event event, String bookingID) {
        if (event == null || bookingID == null) {
            return false;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getBookingID().equals(bookingID)
                    && booking.getBookingStatus() == 2) {
                booking.setBookingStatus(3);
                return true;
            }
        }

        return false;
    }

    public boolean cancelConfirmedBooking(Event event, String bookingID) {
        if (event == null || bookingID == null) {
            return false;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getBookingID().equals(bookingID)
                    && booking.getBookingStatus() == 1) {
                booking.setBookingStatus(3);
                promoteFromWaitlistIfPossible(event);
                return true;
            }
        }

        return false;
    }

    public Booking promoteFromWaitlistIfPossible(Event event) {
        if (event == null || isEventCancelled(event)) {
            return null;
        }

        if (getConfirmedBookingsForEvent(event).size() >= event.getCapacity()) {
            return null;
        }

        ArrayList<Booking> waitlisted = getWaitlistedBookingsForEvent(event);
        if (waitlisted.isEmpty()) {
            return null;
        }

        waitlisted.sort(Comparator.comparing(Booking::getCreatedAt));
        Booking promoted = waitlisted.get(0);
        promoted.setBookingStatus(1);
        return promoted;
    }

    public void handleCapacityIncrease(Event event) {
        if (event == null || isEventCancelled(event)) {
            return;
        }

        while (getConfirmedBookingsForEvent(event).size() < event.getCapacity()
                && !getWaitlistedBookingsForEvent(event).isEmpty()) {
            promoteFromWaitlistIfPossible(event);
        }
    }

    public void cancelAllBookingsForEvent(Event event) {
        if (event == null) {
            return;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())) {
                booking.setBookingStatus(3);
            }
        }
    }

    public ArrayList<Booking> getConfirmedBookingsForEvent(Event event) {
        ArrayList<Booking> confirmedBookings = new ArrayList<>();

        if (event == null) {
            return confirmedBookings;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getBookingStatus() == 1) {
                confirmedBookings.add(booking);
            }
        }

        return confirmedBookings;
    }

    public ArrayList<Booking> getWaitlistedBookingsForEvent(Event event) {
        ArrayList<Booking> waitlistedBookings = new ArrayList<>();

        if (event == null) {
            return waitlistedBookings;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getBookingStatus() == 2) {
                waitlistedBookings.add(booking);
            }
        }

        waitlistedBookings.sort(Comparator.comparing(Booking::getCreatedAt));
        return waitlistedBookings;
    }

    public ArrayList<Booking> getBookingsForUser(String userID) {
        ArrayList<Booking> userBookings = new ArrayList<>();

        if (userID == null) {
            return userBookings;
        }

        for (Booking booking : allBookings) {
            if (booking.getUserID().equals(userID)) {
                userBookings.add(booking);
            }
        }

        return userBookings;
    }

    public void viewEventRoster(Event event) {
        if (event == null) {
            return;
        }

        ArrayList<Booking> confirmed = getConfirmedBookingsForEvent(event);
        ArrayList<Booking> waitlisted = getWaitlistedBookingsForEvent(event);

        System.out.println("\nEvent Roster for " + event.getEventID() + " - " + event.getTitle());

        System.out.print("Confirmed (" + confirmed.size() + "/" + event.getCapacity() + "): ");
        if (confirmed.isEmpty()) {
            System.out.print("(none)");
        } else {
            for (Booking booking : confirmed) {
                System.out.print(booking.getUserID() + " ");
            }
        }

        System.out.println();

        System.out.print("Waitlist: ");
        if (waitlisted.isEmpty()) {
            System.out.print("(none)");
        } else {
            for (Booking booking : waitlisted) {
                System.out.print(booking.getUserID() + " ");
            }
        }

        System.out.println("\n");
    }

    public void viewWaitlist(Event event) {
        if (event == null) {
            return;
        }

        ArrayList<Booking> waitlisted = getWaitlistedBookingsForEvent(event);

        System.out.println("Waitlist for Event " + event.getEventID() + " - " + event.getTitle());

        if (waitlisted.isEmpty()) {
            System.out.println("(empty)");
            return;
        }

        for (Booking booking : waitlisted) {
            System.out.println(
                    "User: " + booking.getUserID()
                            + " | BookingID: " + booking.getBookingID()
                            + " | CreatedAt: " + booking.getCreatedAt()
            );
        }
    }
}