package com.example.loginui.eventManagement;

import java.time.LocalDateTime;

public class Seminar extends Event {
    protected String speakername;

    public Seminar(String eventID, String title, LocalDateTime dateTime, String location, int capacity, int status, String speakername){
        super(eventID,  title, dateTime, location, capacity, status);
        this.speakername = speakername;

    }

    public String getspeakername(){
        return speakername;
    }

    public void setspeakername(String speakername){
        this.speakername = speakername;
    }


    @Override
    public void displayInfo() {
        super.displayInfo();  // calls parent's displayInfo first
        System.out.println("Speaker Name: " + speakername);
        System.out.println();
    }


}
