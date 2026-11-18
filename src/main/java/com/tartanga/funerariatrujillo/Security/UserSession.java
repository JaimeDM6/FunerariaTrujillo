package com.tartanga.funerariatrujillo.Security;

import com.tartanga.funerariatrujillo.Objects.Trabajador;

public final class UserSession {

    private static Trabajador currentUser;

    private UserSession() {
    }

    public static void login(Trabajador trabajador) {
        currentUser = trabajador;
    }

    public static Trabajador getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}
