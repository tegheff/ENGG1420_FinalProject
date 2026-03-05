import java.util.ArrayList;

public class Event {
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
        this.capacity = capacity;
        this.status = status;

        isCancelled = false;

    }

    // Getters
    public String getEventID() {
        return eventID;
    }

    public String getTitle() {
        return title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setdateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setlocation(String location) {
        this.location = location;
    }

    public void setcapacity(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        } else
            this.capacity = 1; // or throw an error
    }

    public void setstatus(String status) {
        if (status.equals("Active") || status.equals("Cancelled")) {
            this.status = status;
        } else
            this.status = null;
    }

    public void displayInfo() {
        System.out.println("Event: " + title);
        System.out.println("Location: " + location);
        System.out.println("Capacity: " + capacity);
        System.out.println("Status: " + status);

    }

    public void viewEventRoster() {
        if (confirmedList.size() == 0)
            return;
        System.out.println("Confirmed List: ");

        for (Booking x : confirmedList) {
            System.out.print(x.getUserID() + ", ");
        }

        System.out.println();
        if (waitList.size() == 0)
            return;
        System.out.println("WaitList: ");

        for (Booking x : waitList) {
            System.out.print(x.getUserID() + ", ");
        }

        System.out.println();
    }

    public void addParticipant(Booking book) {
        if (!isValid(book) || book.getMaxBookings() == 0 || isCancelled) {
            return;
        }
        if (!isFull()) {
            book.setBookStatus(1);
            confirmedList.add(book);
        } else {
            book.setBookStatus(2);
            waitList.add(book);
        }

        book.decreaseMaxBooking();
    }

    Booking deQueueWaitList() {
        if (waitList.isEmpty())
            return null;
        return waitList.remove(0);
    }

    void enQueueConfirmedList(Booking user) {
        if (user == null)
            return;
        user.setBookStatus(1);
        confirmedList.add(user);
    }

    public void cancelBooking(Booking book) {
        if (confirmedList.contains(book)) {
            confirmedList.remove(book);
            enQueueConfirmedList(deQueueWaitList());
            book.setBookStatus(3);
            return;
        }

        else if (waitList.contains(book)) {
            waitList.remove(book);
            book.setBookStatus(3);
            return;
        }

    }

    public void cancelEvent() {
        for (Booking x : waitList) {
            x.setBookStatus(3);
        }
        for (Booking x : confirmedList) {
            x.setBookStatus(3);
        }
        waitList.clear();
        isCancelled = true;
    }

    boolean isValid(Booking book) {
        return !confirmedList.contains(book) && !waitList.contains(book);
    }

    boolean isFull() {
        return confirmedList.size() >= capacity;
    }

}