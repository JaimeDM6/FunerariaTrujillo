package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de una urna del catálogo de la funeraria. Se identifica por su capacidad.
 *
 * @author Jaime
 */
public class Urna implements Comparable<Urna>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Double capacidad;
    private String material;
    private Double precio;

    /**
     * Crea una urna con valores por defecto.
     */
    public Urna() {
        id = "";
        capacidad = 0.0;
        material = "";
        precio = 0.0;
    }

    /**
     * Crea una urna copiando los datos de otro.
     *
     * @param u objeto que se copia
     */
    public Urna(Urna u) {
        this.id = u.id;
        this.capacidad = u.capacidad;
        this.material = u.material;
        this.precio = u.precio;
    }

    /**
     * Crea una urna con todos sus datos.
     *
     * @param i el identificador
     * @param c la capacidad en litros
     * @param m el material
     * @param pr el precio en euros
     */
    public Urna(String i, Double c, String m, Double pr) {
        this.id = i;
        this.capacidad = c;
        this.material = m;
        this.precio = pr;
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
     * Devuelve la capacidad en litros.
     *
     * @return La capacidad en litros
     */
    public Double getCapacidad() {
        return capacidad;
    }

    /**
     * Establece la capacidad en litros.
     *
     * @param capacidad la capacidad en litros
     */
    public void setCapacidad(Double capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * Devuelve el material.
     *
     * @return El material
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Establece el material.
     *
     * @param material el material
     */
    public void setMaterial(String material) {
        this.material = material;
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
     * Devuelve una descripción en texto de la urna.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return ("Capacidad: " + capacidad + "L, material: " + material + ", precio: " + precio + "€");
    }

    // hashCode
    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(capacidad, material, precio);
    }

    // equals
    /**
     * Compara este objeto con otro: dos objetos son iguales si tienen el mismo valor de capacidad.
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
        Urna other = (Urna) obj;

        return capacidad.equals(other.capacidad);
    }

    /**
     * Compara este objeto con otro por capacidad.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Urna other) {
        return this.capacidad.compareTo(other.capacidad);
    }
}
