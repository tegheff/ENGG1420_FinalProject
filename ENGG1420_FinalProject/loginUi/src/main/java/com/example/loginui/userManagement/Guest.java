package com.example.loginui.userManagement;

public class Guest extends User {
    public Guest(String userId, String name, String email) {
        super(userId, name, email);
    }

    @Override
    public String getType() {
        return "Guest";
    }
}