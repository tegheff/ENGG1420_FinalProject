package com.example.loginui.eventManagement;

public class Workshop extends Event {
    protected String topic;

    public Workshop(String eventID, String title, String dateTime, String location, int capacity, String status, String topic){
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
