package com.dailydose.util;

import com.dailydose.model.User;

/**
 * SESSION MANAGER
 * Konsep Design Pattern: Singleton
 * Menyimpan informasi user yang sedang login sehingga bisa diakses
 * dari Controller manapun tanpa passing parameter.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    /** Cek apakah user yang login adalah Admin */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}
