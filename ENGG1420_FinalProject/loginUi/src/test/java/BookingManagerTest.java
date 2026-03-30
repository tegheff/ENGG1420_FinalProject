import com.example.loginui.eventManagement.Concert;
import com.example.loginui.eventManagement.Seminar;
import com.example.loginui.eventManagement.Workshop;
import com.example.loginui.waitlistManagement.Booking;
import com.example.loginui.waitlistManagement.BookingManager;
import com.example.loginui.eventManagement.Event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingManagerTest{

@Test
void bookingWhenUnderCapacity(){
    BookingManager bookingManager = new BookingManager();
    Event event = new Seminar("E001","First Aid Training", LocalDateTime.parse("2026-02-21T12:30"),"THRN 1001",40,1,"Dr. Ozekinci");
    Booking booking = new Booking("B001","1330219", event.getEventID(), LocalDateTime.parse("2026-02-18T11:30"));


    boolean added = bookingManager.addBooking(event, booking);

    assertTrue(added);
    assertEquals(Booking.STATUS_CONFIRMED, booking.getBookingStatus());
}


@Test
void bookingWhenOverCapacity_Waitlisted() {

    BookingManager bookingManager = new BookingManager();
    Event event = new Workshop("E002","Improv 101", LocalDateTime.parse("2026-03-30T09:30"),"Library 101",1,1,"Ardo Wazowski");
    Booking bkg1 = new Booking("B001", "1220234", event.getEventID(), LocalDateTime.parse("2026-03-28T19:30"));
    Booking bkg2 = new Booking("B002","1440231", event.getEventID(), LocalDateTime.parse("2026-03-29T16:30"));

    bookingManager.addBooking(event,bkg1);
    boolean added = bookingManager.addBooking(event,bkg2);

    assertTrue(added);
    assertEquals(Booking.STATUS_CONFIRMED, bkg1.getBookingStatus());
    assertEquals(Booking.STATUS_WAITLISTED,bkg2.getBookingStatus());

}

@Test
void cancelBooking_WaitlistPromotion() {
    BookingManager bookingManager = new BookingManager();
    Event event = new Concert("E003","J.Cole Concert", LocalDateTime.parse("2026-05-10T12:30"),"Johnston Green", 1, 2, "18+");
    Booking bkg1 = new Booking("B001","1550123", event.getEventID(), LocalDateTime.parse("2026-04-01T10:00"));
    Booking bkg2 = new Booking("B002","1660456", event.getEventID(), LocalDateTime.parse("2026-04-01T10:05"));

    bookingManager.addBooking(event, bkg1);
    bookingManager.addBooking(event, bkg2);

    assertEquals(Booking.STATUS_CONFIRMED, bkg1.getBookingStatus());
    assertEquals(Booking.STATUS_WAITLISTED, bkg2.getBookingStatus());

    boolean cancelled = bookingManager.cancelConfirmedBooking(event, bkg1.getBookingID());

    assertTrue(cancelled);
    assertEquals(Booking.STATUS_CANCELLED, bkg1.getBookingStatus());
    assertEquals(Booking.STATUS_CONFIRMED, bkg2.getBookingStatus());

}

@Test
void duplicateBooking_Prevention(){
    BookingManager bookingManager = new BookingManager();
    Event event = new Workshop("E015","Intro to Machine Learning", LocalDateTime.parse("2026-04-18T11:45"), "MACN 121",40,1,"AI Fundamentals");
    Booking bkg1 = new Booking("B001", "1298476", event.getEventID(), LocalDateTime.parse("2026-03-10T10:29"));
    Booking bkg2 = new Booking("B002", "1298476", event.getEventID(), LocalDateTime.parse("2026-03-10T10:35"));

    bookingManager.addBooking(event, bkg1);
    boolean added = bookingManager.addBooking(event, bkg2);

    assertFalse(added);
    assertEquals(Booking.STATUS_CONFIRMED, bkg1.getBookingStatus());

}

}
