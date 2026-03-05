import java.time.LocalDateTime;

public class Booking {

    public enum Roles {
        STUDENT,
        STAFF,
        GUESTS
    }

    enum BookStatus {
        CONFIRMED,
        WAITLISTED,
        CANCELLED,
        UNDOCUMENTED
    }

    Event event;
    Roles status;
    String userid;
    LocalDateTime createdAt;
    int maxBook;
    BookStatus bookStatus;

    public Booking(Roles status, String userid, Event event) {
        this.userid = userid;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.bookStatus = BookStatus.UNDOCUMENTED;
        this.event = event;

        setMaxBooking(status);
    }

    public Event getEvent() {
        return event;

    }

    public String getUserID() {
        return userid;
    }

    void setMaxBooking(Roles status) {
        switch (status) {
            case STUDENT:
                maxBook = 3;
                break;
            case STAFF:
                maxBook = 5;
                break;
            case GUESTS:
                maxBook = 1;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Roles getStatus() {
        return status;
    }

    public int getMaxBookings() {
        return maxBook;
    }

    public void decreaseMaxBooking() {
        maxBook--;
    }

    public void setBookStatus(int i) {
        switch (i) {
            case 1:
                bookStatus = BookStatus.CONFIRMED;
                break;
            case 2:
                bookStatus = BookStatus.WAITLISTED;
                break;
            default:
                bookStatus = BookStatus.CANCELLED;
        }
    }

    public String getBookStatus() {
        switch (bookStatus) {
            case CONFIRMED:
                return "CONFIRMED";

            case WAITLISTED:
                return "WAITLISTED";

            case CANCELLED:
                return "CANCELLED";
            default:
                return "NOT BOOKED";

        }
    }
}