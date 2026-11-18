package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de un servicio que ofrece la funeraria, con su descripción y precio. Se identifica por su
 * nombre.
 *
 * @author Iker
 */
public class Servicios implements Comparable<Servicios>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String descripcion;
    private Double precio;

    /**
     * Crea un servicio con valores por defecto.
     */
    public Servicios() {
        id = "";
        nombre = "";
        descripcion = "";
        precio = 0.0;
    }

    /**
     * Crea un servicio copiando los datos de otro.
     *
     * @param s objeto que se copia
     */
    public Servicios(Servicios s) {
        this.id = s.id;
        this.nombre = s.nombre;
        this.descripcion = s.descripcion;
        this.precio = s.precio;
    }

    /**
     * Crea un servicio con todos sus datos.
     *
     * @param n el nombre
     * @param d la descripción
     * @param p el precio en euros
     */
    public Servicios(String n, String d, Double p) {
        this.nombre = n;
        this.descripcion = d;
        this.precio = p;
    }

    // Getters and Setters
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
     * Devuelve el precio en euros.
     *
     * @return El precio en euros
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio en euros.
     *
     * @param precio el precio en euros
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    // toString()
    /**
     * Devuelve una descripción en texto de el servicio.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return ("Nombre " + nombre + ", descripción: " + descripcion + ", precio: " + precio + "€");
    }

    // hashCode
    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre, descripcion, precio);
    }

    // equals
    /**
     * Compara este objeto con otro: dos objetos son iguales si tienen el mismo valor de nombre.
     *
     * @param obj objeto con el que se compara
     * @return {@code true} si son iguales
     */
    @Override
    public boolean equals(Object obj) {
        // Si es el mismo objeto...
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        Servicios other = (Servicios) obj;

        return nombre.equals(other.nombre);
    }

    /**
     * Compara este objeto con otro por nombre.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Servicios other) {
        return this.nombre.compareTo(other.nombre);
    }
}
