package events;


public class Event{
    protected String dateTime;
    protected String location;
    protected int capacity;
    protected String status;
    protected String title;
    protected String eventID;

    public Event(String eventID, String title, String dateTime, String location, int capacity, String status){
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

    //Setters
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setdateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setlocation(String location){
        this.location = location;
    }

    public void setcapacity(int capacity){
        if (capacity > 0) {
        this.capacity = capacity;
        } 
        else 
            this.capacity = 1;  // or throw an error
    }

    public void setstatus(String status){
        if (status.equals("Active")|| status.equals("Cancelled")){
            this.status =status;
        }
        else this.status = null;
    }


    public void displayInfo(){
        System.out.println("Event: " + title);
        System.out.println("Location: " + location);
        System.out.println("Capacity: " + capacity);
        System.out.println("Status: " + status);
        
    }

    public static void main(String[] args){
        Event intro_to_git = new Event("E001", "Intro to Git", "2026-02-12T14:30", "Library 101", 40, "Active");
        Workshop ws = new Workshop("E101", "Intro to Git", "2026-02-12T14:30", "Library 101", 40, "Active", "Version Control");
        Seminar sem = new Seminar("E205", "AI Safety Talk", "2026-03-01T10:00", "MACN 113", 120, "Active", "Dr. Noor");
        Concert con = new Concert("E330", "Winter Concert", "2026-03-10T19:00", "UC Hall", 300, "Active", "18+");
        
        
        intro_to_git.displayInfo();
        ws.displayInfo();
        sem.displayInfo();
        con.displayInfo();
        

    }



}

