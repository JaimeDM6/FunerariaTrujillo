package com.tartanga.funerariatrujillo.Security;

import com.tartanga.funerariatrujillo.Objects.Trabajador;

/**
 * Sesión del usuario. Guarda el trabajador que ha iniciado sesión para que el resto de la
 * aplicación pueda consultar su identidad y su rol.
 *
 * @author Aritz
 */
public final class UserSession {

    private static Trabajador currentUser;

    /**
     * Constructor privado: la clase solo ofrece métodos estáticos y no se instancia.
     */
    private UserSession() {
    }

    /**
     * Guarda el trabajador que ha iniciado sesión.
     *
     * @param trabajador trabajador autenticado
     */
    public static void login(Trabajador trabajador) {
        currentUser = trabajador;
    }

    /**
     * Devuelve el trabajador que ha iniciado sesión.
     *
     * @return trabajador autenticado, o {@code null} si no hay sesión iniciada
     */
    public static Trabajador getCurrentUser() {
        return currentUser;
    }

    /**
     * Cierra la sesión eliminando el trabajador guardado.
     */
    public static void logout() {
        currentUser = null;
    }
}
