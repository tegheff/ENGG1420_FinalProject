import java.util.*;

public class EventManager {
    public ArrayList<Event> events = new ArrayList<>();
    public ArrayList<Booking> bookings = new ArrayList<>();

    public Event createNewEvent(String eventID, String title, String dateTime, String location, int capacity,
            String status) {
        Event event = new Event(eventID, title, dateTime, location, capacity, status);
        for (Event e : events)
            if (e.getEventID().equals(eventID))
                return null;
        events.add(event);
        return event;
    }

    public Booking createBooking(Booking.Roles role, String userID, Event event) {
        Booking book = new Booking(role, userID, event);
        bookings.add(book);
        event.addParticipant(book);
        return book;
    }

    public void listEvents() {
        for (Event x : events) {
            x.displayInfo();
        }
    }

    public <T extends Event> ArrayList<T> Search(String title, Class<T> type) {
        ArrayList<T> results = new ArrayList<>();
        for (Event e : events) {
            if (type.isInstance(e) && e.getTitle().equalsIgnoreCase(title))
                results.add(type.cast(e));
        }
        return results;
    }

    public ArrayList<Event> Search(String title) {
        ArrayList<Event> results = new ArrayList<>();
        for (Event e : events) {
            if (e.getTitle().equalsIgnoreCase(title))
                results.add(e);
        }
        return results;
    }

    public void displayUserBookings(String userID) {

        for (Booking b : bookings) {

            if (b.getUserID().equals(userID)) {

                Event e = b.getEvent();

                System.out.println(" | EventName: " + e.getTitle() + " | EventID: " + e.getEventID() + " | Status: "
                        + b.getBookStatus());
            }
        }
    }

}