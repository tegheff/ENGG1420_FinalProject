package com.example.loginui.userManagement;

import com.example.loginui.AppState;
import java.util.*;

public class UserManager {
    private final Map<String, User> users = new LinkedHashMap<>();

    public boolean addUser(User user) {
        if (users.containsKey(user.getUserId())) return false;
        users.put(user.getUserId(), user);
        AppState.saveAll();
        return true;
    }

    public User getUserById(String userId) {
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
