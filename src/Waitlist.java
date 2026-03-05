import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;

class Booking {
    private final String bookingID;
    private final String userID;
    private final String eventID;
    private final LocalDateTime createdAt;
    private int bookStatus; // 1=Confirmed, 2=Waitlisted, 3=Cancelled

    public Booking(String bookingID, String userID, String eventID, LocalDateTime createdAt) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.eventID = eventID;
        this.createdAt = createdAt;
        this.bookStatus = 0;
    }

    public String getBookingID() { return bookingID; }
    public String getUserID() { return userID; }
    public String getEventID() { return eventID; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public int getBookStatus() { return bookStatus; }
    public void setBookStatus(int status) { this.bookStatus = status; }

    @Override
    public String toString() {
        String s = (bookStatus == 1) ? "Confirmed"
                : (bookStatus == 2) ? "Waitlisted"
                : (bookStatus == 3) ? "Cancelled"
                : "Unset";
        return bookingID + " (" + userID + ", " + createdAt + ", " + s + ")";
    }
}

class Event {
    protected String dateTime;
    protected String location;
    protected int capacity;
    protected String status;
    protected String title;
    protected String eventID;
    protected Boolean isCancelled;

    protected ArrayList<Booking> waitList = new ArrayList<>();
    protected ArrayList<Booking> confirmedList = new ArrayList<>();

    public Event(String eventID, String title, String dateTime, String location, int capacity, String status) {
        this.eventID = eventID;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = Math.max(1, capacity);
        this.status = status;
        this.isCancelled = false;
    }

    
    public boolean isEventCancelled() {
        return isCancelled || (status != null && status.equals("Cancelled"));
    }

    
    public int confirmedCount() {
        int count = 0;
        for (Booking b : confirmedList) {
            if (b.getBookStatus() == 1) count++;
        }
        return count;
    }

    
    public boolean isFull() {
        return confirmedCount() >= capacity;
    }
    
    public boolean hasActiveBookingForUser(String userId) {
        for (Booking b : confirmedList) {
            if (b.getUserID().equals(userId) && b.getBookStatus() != 3) return true;
        }
        for (Booking b : waitList) {
            if (b.getUserID().equals(userId) && b.getBookStatus() != 3) return true;
        }
        return false;
    }

    public boolean addToWaitlist(Booking book) {
        if (book == null) return false;
        if (isEventCancelled()) return false;

        if (hasActiveBookingForUser(book.getUserID())) return false;

        book.setBookStatus(2); 
        waitList.add(book);

        
        waitList.sort(Comparator.comparing(Booking::getCreatedAt));
        return true;
    }

    
    public void viewWaitlist() {
        System.out.println("Waitlist for Event " + eventID + " - " + title);

        if (waitList.isEmpty()) {
            System.out.println("(empty)");
            return;
        }

        for (Booking b : waitList) {
            System.out.println(
                    "User: " + b.getUserID()
                            + " | BookingID: " + b.getBookingID()
                            + " | CreatedAt: " + b.getCreatedAt()
            );
        }
    }

    
    public boolean removeFromWaitlist(String bookingID) {
        if (bookingID == null) return false;

        for (int i = 0; i < waitList.size(); i++) {
            Booking b = waitList.get(i);
            if (b.getBookingID().equals(bookingID)) {
                b.setBookStatus(3); 
                waitList.remove(i);
                return true;
            }
        }
        return false;
    }

    
    public Booking promoteFromWaitlistIfPossible() {
        if (isEventCancelled()) return null;
        if (confirmedCount() >= capacity) return null;
        if (waitList.isEmpty()) return null;

        Booking promoted = waitList.remove(0);
        promoted.setBookStatus(1); // Confirmed
        confirmedList.add(promoted);

        return promoted;
    }

    public void handleCapacityIncrease() {
        if (isEventCancelled()) return;

        while (confirmedCount() < capacity && !waitList.isEmpty()) {
            promoteFromWaitlistIfPossible();
        }
    }

    public void cancelEvent() {
        isCancelled = true;
        status = "Cancelled";

        for (Booking b : confirmedList) b.setBookStatus(3);
        for (Booking b : waitList) b.setBookStatus(3);

        waitList.clear();
    }

    public void sortWaitlistByCreatedAt() {
        waitList.sort(Comparator.comparing(Booking::getCreatedAt));
    }


    public void addParticipantDemo(Booking book) {
        if (book == null) return;
        if (isEventCancelled()) return;

        // prevent duplicates
        if (hasActiveBookingForUser(book.getUserID())) return;

        if (confirmedCount() < capacity) {
            book.setBookStatus(1); 
            confirmedList.add(book);
        } else {
            addToWaitlist(book);
        }
    }

    public Booking cancelConfirmedAndPromoteDemo(Booking confirmedBooking) {
        if (confirmedBooking == null) return null;

        if (confirmedList.contains(confirmedBooking)) {
            // set cancel
            confirmedBooking.setBookStatus(3);

            // promote next waitlist
            return promoteFromWaitlistIfPossible();
        }
        return null;
    }

    public void viewEventRoster() {
        System.out.println("\nEvent Roster for " + eventID + " - " + title);

        System.out.print("Confirmed (" + confirmedCount() + "/" + capacity + "): ");
        boolean anyConfirmed = false;
        for (Booking b : confirmedList) {
            if (b.getBookStatus() == 1) {
                System.out.print(b.getUserID() + " ");
                anyConfirmed = true;
            }
        }
        if (!anyConfirmed) System.out.print("(none)");
        System.out.println();

        System.out.print("Waitlist: ");
        if (waitList.isEmpty()) System.out.print("(none)");
        for (Booking b : waitList) System.out.print(b.getUserID() + " ");
        System.out.println("\n");
    }

    public void setcapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
        handleCapacityIncrease(); 
    }
}

public class Main {
    public static void main(String[] args) {

        Event e = new Event("E205", "AI Safety Talk", "2026-03-01T10:00", "MACN 113", 2, "Active");

        // f
        Booking c1 = new Booking("B1", "U001", e.eventID, LocalDateTime.now().minusMinutes(10));
        Booking c2 = new Booking("B2", "U002", e.eventID, LocalDateTime.now().minusMinutes(9));
        e.addParticipantDemo(c1);
        e.addParticipantDemo(c2);

        // add waitlist bookings
        Booking w1 = new Booking("B3", "U003", e.eventID, LocalDateTime.now().minusMinutes(5));
        Booking w2 = new Booking("B4", "U004", e.eventID, LocalDateTime.now().minusMinutes(4));
        Booking w3 = new Booking("B5", "U005", e.eventID, LocalDateTime.now().minusMinutes(3));
        e.addParticipantDemo(w1);
        e.addParticipantDemo(w2);
        e.addParticipantDemo(w3);

        e.viewEventRoster();
        e.viewWaitlist();

        // remove from waitlist)
        System.out.println("\nCancelling waitlisted booking B4...");
        e.removeFromWaitlist("B4");
        e.viewWaitlist();

        // cancel booking 
        System.out.println("\nCancelling confirmed booking B2 -> should promote first waitlisted...");
        Booking promoted = e.cancelConfirmedAndPromoteDemo(c2);
        System.out.println("Promoted: " + (promoted == null ? "(none)" : promoted.getUserID()));
        e.viewEventRoster();
        e.viewWaitlist();

        // increase cap
        System.out.println("\nIncreasing capacity to 3 -> should promote next waitlisted...");
        e.setcapacity(3);
        e.viewEventRoster();
        e.viewWaitlist();

        // clear waitlist
        System.out.println("\nCancelling event -> waitlist cleared...");
        e.cancelEvent();
        e.viewWaitlist();
    }
}
