import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookingManagerTest {

    @Test
    public void testBookingUnderCapacity() {
        BookingManager bookingManager = new BookingManager();

        User user = new Student("U001", "Razan", "r@email.com");
        Event event = new Workshop("E101", "Git Workshop", "2026-03-29T10:00",
                "Library 101", 2, "Active", "Version Control");

        bookingManager.createBooking(user, event);

        assertEquals(1, event.getConfirmedBookings().size());
        assertEquals(0, event.getWaitlistBookings().size());
        assertEquals("Confirmed", event.getConfirmedBookings().get(0).getStatus());
    }

    @Test
    public void testBookingWhenFullGoesToWaitlist() {
        BookingManager bookingManager = new BookingManager();

        User user1 = new Student("U001", "Razan", "r@email.com");
        User user2 = new Student("U002", "Ali", "a@email.com");
        User user3 = new Student("U003", "Sara", "s@email.com");

        Event event = new Workshop("E101", "Git Workshop", "2026-03-29T10:00",
                "Library 101", 2, "Active", "Version Control");

        bookingManager.createBooking(user1, event);
        bookingManager.createBooking(user2, event);
        bookingManager.createBooking(user3, event);

        assertEquals(2, event.getConfirmedBookings().size());
        assertEquals(1, event.getWaitlistBookings().size());
        assertEquals("Waitlisted", event.getWaitlistBookings().get(0).getStatus());
        assertEquals("U003", event.getWaitlistBookings().get(0).getUserId());
    }

    @Test
    public void testCancelBookingPromotesWaitlist() {
        BookingManager bookingManager = new BookingManager();

        User user1 = new Student("U001", "Razan", "r@email.com");
        User user2 = new Student("U002", "Ali", "a@email.com");
        User user3 = new Student("U003", "Sara", "s@email.com");

        Event event = new Workshop("E101", "Git Workshop", "2026-03-29T10:00",
                "Library 101", 2, "Active", "Version Control");

        bookingManager.createBooking(user1, event); // B1 confirmed
        bookingManager.createBooking(user2, event); // B2 confirmed
        bookingManager.createBooking(user3, event); // B3 waitlisted

        bookingManager.cancelBooking("B1", event);

        assertEquals(2, event.getConfirmedBookings().size());
        assertEquals(0, event.getWaitlistBookings().size());

        boolean user3Promoted = false;
        for (Booking b : event.getConfirmedBookings()) {
            if (b.getUserId().equals("U003") && b.getStatus().equals("Confirmed")) {
                user3Promoted = true;
            }
        }

        assertTrue(user3Promoted);
    }

    @Test
    public void testDuplicateBookingPrevention() {
        BookingManager bookingManager = new BookingManager();

        User user = new Student("U001", "Razan", "r@email.com");
        Event event = new Workshop("E101", "Git Workshop", "2026-03-29T10:00",
                "Library 101", 2, "Active", "Version Control");

        bookingManager.createBooking(user, event);
        bookingManager.createBooking(user, event); // duplicate attempt

        assertEquals(1, event.getConfirmedBookings().size());
        assertEquals(0, event.getWaitlistBookings().size());
    }
}