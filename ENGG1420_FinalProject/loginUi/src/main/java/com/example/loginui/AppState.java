package com.example.loginui;

import com.example.loginui.eventManagement.EventManager;
import com.example.loginui.persistence.CsvStore;
import com.example.loginui.userManagement.UserManager;
import com.example.loginui.waitlistManagement.BookingManager;

public final class AppState {
    private static final UserManager USER_MANAGER = new UserManager();
    private static final EventManager EVENT_MANAGER = new EventManager();
    private static final BookingManager BOOKING_MANAGER = new BookingManager();
    private static boolean initialized = false;
    private static boolean loading = false;

    private AppState() {}

    public static void init() {
        if (initialized) {
            return;
        }
        loading = true;
        CsvStore.loadAll(USER_MANAGER, EVENT_MANAGER, BOOKING_MANAGER);
        loading = false;
        initialized = true;
    }

    public static UserManager getUserManager() {
        init();
        return USER_MANAGER;
    }

    public static EventManager getEventManager() {
        init();
        return EVENT_MANAGER;
    }

    public static BookingManager getBookingManager() {
        init();
        return BOOKING_MANAGER;
    }

    public static void saveAll() {
        if (loading) {
            return;
        }
        CsvStore.saveAll(USER_MANAGER, EVENT_MANAGER, BOOKING_MANAGER);
    }
}
