package com.example.loginui.userManagement;

public class Staff extends User {
    public Staff(String userId, String name, String email) {
        super(userId, name, email);
    }

    @Override
    public String getType() {
        return "Staff";
    }
}