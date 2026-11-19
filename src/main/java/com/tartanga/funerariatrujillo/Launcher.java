package com.tartanga.funerariatrujillo;

/**
 * Punto de entrada de la aplicación. Se utiliza una clase lanzadora distinta de {@code App} para
 * poder arrancar JavaFX desde un JAR sin módulos.
 *
 * @author Aritz
 */
public class Launcher {

    /**
     * Lanza la aplicación JavaFX delegando en {@link App#main(String[])}.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        App.main(args);
    }

}
