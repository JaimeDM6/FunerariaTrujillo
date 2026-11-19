package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de una sala de la funeraria. Se identifica por su nombre.
 *
 * @author Aritz
 */
public class Sala implements Comparable<Sala>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String descripcion;
    private int capacidad;

    /**
     * Crea una sala con valores por defecto.
     */
    public Sala() {
        id = null;
        nombre = "";
        descripcion = "";
        capacidad = 0;
    }

    /**
     * Crea una sala copiando los datos de otro.
     *
     * @param s objeto que se copia
     */
    public Sala(Sala s) {
        this.id = s.id;
        this.nombre = s.nombre;
        this.descripcion = s.descripcion;
        this.capacidad = s.capacidad;
    }

    /**
     * Crea una sala con todos sus datos.
     *
     * @param n el nombre
     * @param d la descripción
     * @param c la capacidad en personas
     */
    public Sala(String n, String d, int c) {
        this.id = null;
        this.nombre = n;
        this.descripcion = d;
        this.capacidad = c;
    }

    // Getters y setters
    /**
     * Devuelve el identificador.
     *
     * @return El identificador
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador.
     *
     * @param id el identificador
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre.
     *
     * @return El nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre.
     *
     * @param nombre el nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la descripción.
     *
     * @return La descripción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción.
     *
     * @param descripcion la descripción
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve la capacidad en personas.
     *
     * @return La capacidad en personas
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Establece la capacidad en personas.
     *
     * @param capacidad la capacidad en personas
     */
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * Devuelve una descripción en texto de la sala.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return "Nombre: " + nombre
                + ", descripción: " + descripcion
                + ", capacidad: " + capacidad + " personas";
    }

    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    /**
     * Compara este objeto con otro: dos objetos son iguales si tienen el mismo valor de nombre.
     *
     * @param obj objeto con el que se compara
     * @return {@code true} si son iguales
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Sala other = (Sala) obj;
        return Objects.equals(nombre, other.nombre);
    }

    /**
     * Compara este objeto con otro por nombre.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Sala other) {
        return this.nombre.compareTo(other.nombre);
    }
}