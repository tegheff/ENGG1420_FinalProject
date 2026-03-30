package com.example.loginui.eventManagement;

import java.time.LocalDateTime;

public class Concert extends Event {
    protected String ageRestriction;

    public Concert(String eventID, String title, LocalDateTime dateTime, String location, int capacity, int status, String ageRestriction){
        super(eventID,  title, dateTime, location, capacity, status);
        this.ageRestriction = ageRestriction;

    }

    public String getageRestriction(){
        return ageRestriction;
    }

    public void setageRestriction(String ageRestriction){
        this.ageRestriction = ageRestriction;
    }


    @Override
    public void displayInfo() {
        super.displayInfo();  // calls parent's displayInfo first
        System.out.println("Age Restiction: " + ageRestriction);
        System.out.println();
    }


}
