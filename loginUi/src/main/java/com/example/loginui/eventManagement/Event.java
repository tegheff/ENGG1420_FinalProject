package com.example.loginui.eventManagement;

import com.example.loginui.waitlistManagement.Booking;
import java.time.LocalDateTime;

public class Event{
    protected LocalDateTime dateTime;
    protected String location;
    protected int capacity;
    protected int status;
    protected String title;
    protected String eventID;

    public Event(String eventID, String title, LocalDateTime dateTime, String location, int capacity, int status){
        this.eventID = eventID;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.status = status;

    }

    //Getters
    public String getEventID() {
        return eventID;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStatus() {
        return status;
    }

    //Adding new getters for BookingManager



    //Setters
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setCapacity(int capacity){
        if (capacity > 0) {
        this.capacity = capacity;
        } 
        else 
            this.capacity = 1;  // or throw an error
    }

    public void setstatus(int status){
        if (status == Booking.STATUS_CONFIRMED
                || status == Booking.STATUS_WAITLISTED
                || status == Booking.STATUS_CANCELLED) {
            this.status = status;
        } else {
            this.status = 0;
        }
    }


    public void displayInfo(){
        System.out.println("Event: " + title);
        System.out.println("Location: " + location);
        System.out.println("Capacity: " + capacity);
        System.out.println("Status: " + getStatusText());
        
    }

    public static void main(String[] args){
        Event intro_to_git = new Event("E001", "Intro to Git", LocalDateTime.parse("2026-05-15 T12:30"), "Library 101", 40, Booking.STATUS_CONFIRMED);
        Workshop ws = new Workshop("E101", "Intro to Git", LocalDateTime.parse("2026-02-12T14:30"), "Library 101", 40, Booking.STATUS_CONFIRMED, "Version Control");
        Seminar sem = new Seminar("E205", "AI Safety Talk", LocalDateTime.parse("2026-03-01T10:00"), "MACN 113", 120, Booking.STATUS_CONFIRMED, "Dr. Noor");
        Concert con = new Concert("E330", "Winter Concert", LocalDateTime.parse("2026-03-10T19:00"), "UC Hall", 300, Booking.STATUS_CONFIRMED, "18+");



        
        intro_to_git.displayInfo();
        ws.displayInfo();
        sem.displayInfo();
        con.displayInfo();
        

    }

    //Added for BookingManager
    public boolean isEventCancelled() {
        return status == Booking.STATUS_CANCELLED;
    }

    public String getStatusText() {
        switch (status) {
            case Booking.STATUS_CONFIRMED:
                return "Confirmed";
            case Booking.STATUS_WAITLISTED:
                return "Waitlisted";
            case Booking.STATUS_CANCELLED:
                return "Cancelled";
            default:
                return "Unset";
        }
    }

}
