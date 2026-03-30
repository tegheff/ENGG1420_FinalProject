package com.example.loginui.eventManagement;

import java.time.LocalDateTime;

public class Workshop extends Event {
    protected String topic;

    public Workshop(String eventID, String title, LocalDateTime dateTime, String location, int capacity, int status, String topic){
        super(eventID,  title, dateTime, location, capacity, status);
        this.topic = topic;

    }

    public String gettopic(){
        return topic;
    }

    public void settopic(String topic){
        this.topic = topic;
    }


    @Override
    public void displayInfo() {
        super.displayInfo();  // calls parent's displayInfo first
        System.out.println("Topic: " + topic);
        System.out.println();
    }


}
