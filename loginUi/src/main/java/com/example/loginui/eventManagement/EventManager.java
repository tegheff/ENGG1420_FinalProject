package com.example.loginui.eventManagement;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private ArrayList<Event> events;


    public EventManager() {
        events = new ArrayList<>();
    }
    
    public void addEvent(Event e) {
        events.add(e);
    }
    
    public void listAllEvents() {
        for (Event event : events){
            event.displayInfo();
        }
    }
    
    public ArrayList<Event> searchByTitle(String keyword) {
        ArrayList<Event> matches = new ArrayList<>();
        if (keyword == null) {
            return matches;
        }
        keyword = keyword.toLowerCase();

        for (Event event : events) {
            String title = event.getTitle().toLowerCase();
            if (title.contains(keyword)) {
                matches.add(event);
            }
        }
        return matches;
    }

    public boolean cancelEvent(String eventID){
        for (Event event : events){
            if (event.getEventID().equals(eventID)){
                event.setstatus("Cancelled");
                return true;
            }
        }
        return false;
    }

    public ArrayList<Event> filterByType(String type){
        ArrayList<Event> matches = new ArrayList<>();
        if (type == null) {
            return matches;
        }

        for (Event event : events) {
            if (type.equals("Workshop") && event instanceof Workshop){
                matches.add(event);
            }
            else if (type.equals("Seminar") && event instanceof Seminar){
                matches.add(event);
            }
            else if (type.equals("Concert") && event instanceof Concert) {
                matches.add(event);
            }
        }
        return matches;
    }

    public ArrayList<Event> filterbyType(String type){
        return filterByType(type);
    }

    public boolean updateEvent(String eventID, String newTitle, String newLocation, int newCapacity){
        for (Event event : events){
            if (event.getEventID().equals(eventID)) {
                event.setTitle(newTitle);
                event.setlocation(newLocation);
                event.setcapacity(newCapacity);
                return true;
            }
        }
        return false;
    }

    //Getter
    public ArrayList<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public String getAllEventsAsString() {
        return getEventsAsString(events);
    }

    public String getEventsAsString(List<Event> eventList) {
        if (eventList == null || eventList.isEmpty()) {
            return "No events found.";
        }

        StringBuilder result = new StringBuilder();
        for (Event event : eventList) {
            result.append(formatEvent(event)).append("\n");
        }
        return result.toString();
    }

    private String formatEvent(Event event) {
        StringBuilder result = new StringBuilder();
        result.append("ID: ").append(event.getEventID()).append("\n");
        result.append("Event: ").append(event.getTitle()).append("\n");
        result.append("Date/Time: ").append(event.getDateTime()).append("\n");
        result.append("Location: ").append(event.getLocation()).append("\n");
        result.append("Capacity: ").append(event.getCapacity()).append("\n");
        result.append("Status: ").append(event.getStatus()).append("\n");
        if (event instanceof Workshop) {
            result.append("Topic: ").append(((Workshop) event).gettopic()).append("\n");
        } else if (event instanceof Seminar) {
            result.append("Speaker: ").append(((Seminar) event).getspeakername()).append("\n");
        } else if (event instanceof Concert) {
            result.append("Age Restriction: ").append(((Concert) event).getageRestriction()).append("\n");
        }
        return result.toString();
    }
    public Event getEventById(String eventID) {
        if (eventID == null) {
            return null;
        }

        for (Event event : events) {
            if (event.getEventID().equals(eventID)) {
                return event;
            }
        }

        return null;
    }
    public static void main(String[] args) {
        EventManager manager = new EventManager();
        
        // Add your 3 test events
        manager.addEvent(new Workshop("E101", "Intro to Git", "2026-02-12T14:30", "Library 101", 40, "Active", "Version Control"));
        manager.addEvent(new Seminar("E205", "AI Safety Talk", "2026-03-01T10:00", "MACN 113", 120, "Active", "Dr. Noor"));
        manager.addEvent(new Concert("E330", "Winter Concert", "2026-03-10T19:00", "UC Hall", 300, "Active", "18+"));
        
        // Test listAllEvents
        manager.listAllEvents();
        
        // Test search
        System.out.println(manager.getEventsAsString(manager.searchByTitle("git")));

        //Test cancelEvent
        System.out.println("Cancelled E330: " + manager.cancelEvent("E330"));
        
        // Test filterbyType
        System.out.println(manager.getEventsAsString(manager.filterbyType("Workshop")));

        //Test updateEvent
        System.out.println("Updated E205: " + manager.updateEvent("E205", "Advanced AI Safety", "MACN 200", 150));
    }
}
