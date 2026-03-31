package com.example.loginui.persistence;

import com.example.loginui.eventManagement.Concert;
import com.example.loginui.eventManagement.Event;
import com.example.loginui.eventManagement.EventManager;
import com.example.loginui.eventManagement.Seminar;
import com.example.loginui.eventManagement.Workshop;
import com.example.loginui.userManagement.Guest;
import com.example.loginui.userManagement.Staff;
import com.example.loginui.userManagement.Student;
import com.example.loginui.userManagement.User;
import com.example.loginui.userManagement.UserManager;
import com.example.loginui.waitlistManagement.Booking;
import com.example.loginui.waitlistManagement.BookingManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class CsvStore {
    private static final File DATA_DIR = new File("data");
    private static final File USERS_CSV = new File(DATA_DIR, "users.csv");
    private static final File EVENTS_CSV = new File(DATA_DIR, "events.csv");
    private static final File BOOKINGS_CSV = new File(DATA_DIR, "bookings.csv");

    private CsvStore() {}

    public static void loadAll(UserManager userManager, EventManager eventManager, BookingManager bookingManager) {
        ensureDataDir();
        loadUsers(userManager);
        loadEvents(eventManager);
        loadBookings(bookingManager);
    }

    public static void saveAll(UserManager userManager, EventManager eventManager, BookingManager bookingManager) {
        ensureDataDir();
        saveUsers(userManager);
        saveEvents(eventManager);
        saveBookings(bookingManager);
    }

    private static void ensureDataDir() {
        if (!DATA_DIR.exists()) {
            if (!DATA_DIR.mkdirs()) {
                System.out.println("Could not create data directory.");
            }
        }
    }

    private static void loadUsers(UserManager userManager) {
        if (!USERS_CSV.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_CSV))) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = splitCsv(line, 4);
                if (parts.length < 4) {
                    continue;
                }
                String userId = parts[0].trim();
                String name = parts[1].trim();
                String email = parts[2].trim();
                String type = parts[3].trim();
                User user = switch (type) {
                    case "Student" -> new Student(userId, name, email);
                    case "Staff" -> new Staff(userId, name, email);
                    case "Guest" -> new Guest(userId, name, email);
                    default -> null;
                };
                if (user != null) {
                    userManager.addUser(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadEvents(EventManager eventManager) {
        if (!EVENTS_CSV.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(EVENTS_CSV))) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = splitCsv(line, 10);
                if (parts.length < 10) {
                    continue;
                }
                String eventId = parts[0].trim();
                String title = parts[1].trim();
                LocalDateTime dateTime = LocalDateTime.parse(parts[2].trim());
                String location = parts[3].trim();
                int capacity = Integer.parseInt(parts[4].trim());
                String status = parts[5].trim();
                String eventType = parts[6].trim();
                String topic = parts[7].trim();
                String speaker = parts[8].trim();
                String ageRestriction = parts[9].trim();

                int statusValue = "Cancelled".equalsIgnoreCase(status)
                        ? Booking.STATUS_CANCELLED
                        : Booking.STATUS_CONFIRMED;

                Event event = null;
                if ("Workshop".equals(eventType)) {
                    event = new Workshop(eventId, title, dateTime, location, capacity, statusValue, topic);
                } else if ("Seminar".equals(eventType)) {
                    event = new Seminar(eventId, title, dateTime, location, capacity, statusValue, speaker);
                } else if ("Concert".equals(eventType)) {
                    event = new Concert(eventId, title, dateTime, location, capacity, statusValue, ageRestriction);
                }
                if (event != null) {
                    eventManager.addEvent(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadBookings(BookingManager bookingManager) {
        if (!BOOKINGS_CSV.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_CSV))) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = splitCsv(line, 5);
                if (parts.length < 5) {
                    continue;
                }
                String bookingId = parts[0].trim();
                String userId = parts[1].trim();
                String eventId = parts[2].trim();
                LocalDateTime createdAt = LocalDateTime.parse(parts[3].trim());
                String status = parts[4].trim();
                Booking booking = new Booking(bookingId, userId, eventId, createdAt);
                booking.setBookingStatus(parseBookingStatus(status));
                bookingManager.addBookingDirect(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveUsers(UserManager userManager) {
        List<String> lines = new ArrayList<>();
        lines.add("userId,name,email,userType");
        for (User user : userManager.getAllUsers()) {
            lines.add(String.join(",",
                    sanitize(user.getUserId()),
                    sanitize(user.getName()),
                    sanitize(user.getEmail()),
                    sanitize(user.getType())));
        }
        writeLines(USERS_CSV, lines);
    }

    private static void saveEvents(EventManager eventManager) {
        List<String> lines = new ArrayList<>();
        lines.add("eventId,title,dateTime,location,capacity,status,eventType,topic,speakerName,ageRestriction");
        for (Event event : eventManager.getEvents()) {
            String status = event.getStatus() == Booking.STATUS_CANCELLED ? "Cancelled" : "Active";
            String eventType;
            String topic = "";
            String speaker = "";
            String ageRestriction = "";
            if (event instanceof Workshop) {
                eventType = "Workshop";
                topic = ((Workshop) event).gettopic();
            } else if (event instanceof Seminar) {
                eventType = "Seminar";
                speaker = ((Seminar) event).getspeakername();
            } else if (event instanceof Concert) {
                eventType = "Concert";
                ageRestriction = ((Concert) event).getageRestriction();
            } else {
                eventType = "Event";
            }
            lines.add(String.join(",",
                    sanitize(event.getEventID()),
                    sanitize(event.getTitle()),
                    sanitize(event.getDateTime().toString()),
                    sanitize(event.getLocation()),
                    String.valueOf(event.getCapacity()),
                    status,
                    eventType,
                    sanitize(topic),
                    sanitize(speaker),
                    sanitize(ageRestriction)
            ));
        }
        writeLines(EVENTS_CSV, lines);
    }

    private static void saveBookings(BookingManager bookingManager) {
        List<String> lines = new ArrayList<>();
        lines.add("bookingId,userId,eventId,createdAt,bookingStatus");
        for (Booking booking : bookingManager.getAllBookings()) {
            lines.add(String.join(",",
                    sanitize(booking.getBookingID()),
                    sanitize(booking.getUserID()),
                    sanitize(booking.getEventID()),
                    sanitize(booking.getCreatedAt().toString()),
                    bookingStatusText(booking.getBookingStatus())
            ));
        }
        writeLines(BOOKINGS_CSV, lines);
    }

    private static int parseBookingStatus(String status) {
        if ("Confirmed".equalsIgnoreCase(status)) {
            return Booking.STATUS_CONFIRMED;
        }
        if ("Waitlisted".equalsIgnoreCase(status)) {
            return Booking.STATUS_WAITLISTED;
        }
        if ("Cancelled".equalsIgnoreCase(status)) {
            return Booking.STATUS_CANCELLED;
        }
        return 0;
    }

    private static String bookingStatusText(int status) {
        return switch (status) {
            case Booking.STATUS_CONFIRMED -> "Confirmed";
            case Booking.STATUS_WAITLISTED -> "Waitlisted";
            case Booking.STATUS_CANCELLED -> "Cancelled";
            default -> "Unset";
        };
    }

    private static void writeLines(File file, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] splitCsv(String line, int minParts) {
        String[] parts = line.split(",", -1);
        if (parts.length >= minParts) {
            return parts;
        }
        return parts;
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", " ").trim();
    }
}
