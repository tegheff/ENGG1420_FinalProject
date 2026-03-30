package com.example.loginui.waitlistManagement;

import com.example.loginui.AppState;
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

        return event.getStatus() == Booking.STATUS_CANCELLED;
    }

    public boolean hasActiveBookingForUser(Event event, String userID) {
        if (event == null || userID == null) {
            return false;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getUserID().equals(userID)
                    && booking.getBookingStatus() != Booking.STATUS_CANCELLED) {
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
            booking.setBookingStatus(Booking.STATUS_CONFIRMED);
        } else {
            booking.setBookingStatus(Booking.STATUS_WAITLISTED);
        }

        allBookings.add(booking);
        AppState.saveAll();
        return true;
    }

    public void addBookingDirect(Booking booking) {
        if (booking == null) {
            return;
        }
        allBookings.add(booking);
    }

    public boolean removeFromWaitlist(Event event, String bookingID) {
        if (event == null || bookingID == null) {
            return false;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getBookingID().equals(bookingID)
                    && booking.getBookingStatus() == Booking.STATUS_WAITLISTED) {
                booking.setBookingStatus(Booking.STATUS_CANCELLED);
                AppState.saveAll();
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
                    && booking.getBookingStatus() == Booking.STATUS_CONFIRMED) {
                booking.setBookingStatus(Booking.STATUS_CANCELLED);
                promoteFromWaitlistIfPossible(event);
                AppState.saveAll();
                return true;
            }
        }

        return false;
    }

    public void promoteFromWaitlistIfPossible(Event event) {
        if (event == null || isEventCancelled(event)) {
            return;
        }

        if (getConfirmedBookingsForEvent(event).size() >= event.getCapacity()) {
            return;
        }

        ArrayList<Booking> waitlisted = getWaitlistedBookingsForEvent(event);
        if (waitlisted.isEmpty()) {
            return;
        }

        waitlisted.sort(Comparator.comparing(Booking::getCreatedAt));
        Booking promoted = waitlisted.get(0);
        promoted.setBookingStatus(Booking.STATUS_CONFIRMED);
    }

    public void handleCapacityIncrease(Event event) {
        if (event == null || isEventCancelled(event)) {
            return;
        }

        while (getConfirmedBookingsForEvent(event).size() < event.getCapacity()
                && !getWaitlistedBookingsForEvent(event).isEmpty()) {
            promoteFromWaitlistIfPossible(event);
        }
        AppState.saveAll();
    }

    public void cancelAllBookingsForEvent(Event event) {
        if (event == null) {
            return;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())) {
                booking.setBookingStatus(Booking.STATUS_CANCELLED);
            }
        }
        AppState.saveAll();
    }

    public ArrayList<Booking> getConfirmedBookingsForEvent(Event event) {
        ArrayList<Booking> confirmedBookings = new ArrayList<>();

        if (event == null) {
            return confirmedBookings;
        }

        for (Booking booking : allBookings) {
            if (booking.getEventID().equals(event.getEventID())
                    && booking.getBookingStatus() == Booking.STATUS_CONFIRMED) {
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
                    && booking.getBookingStatus() == Booking.STATUS_WAITLISTED) {
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
