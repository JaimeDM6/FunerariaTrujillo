package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de un féretro del catálogo de la funeraria. Se identifica por sus medidas.
 *
 * @author Aritz
 */
public class Feretro implements Comparable<Feretro>, Serializable {

    private static final long serialVersionUID = 1L;

    private String medidas;
    private String material;
    private Double peso;
    private Double precio;
    private String id;

    /**
     * Crea un féretro con valores por defecto.
     */
    public Feretro() {
        medidas = "";
        material = "";
        peso = 0.0;
        precio = 0.0;
    }

    /**
     * Crea un féretro copiando los datos de otro.
     *
     * @param f objeto que se copia
     */
    public Feretro(Feretro f) {
        this.medidas = f.medidas;
        this.material = f.material;
        this.peso = f.peso;
        this.precio = f.precio;
        this.id = f.id;
    }

    /**
     * Crea un féretro con todos sus datos.
     *
     * @param me las medidas
     * @param ma el material
     * @param pe el peso en kilogramos
     * @param pr el precio en euros
     */
    public Feretro(String me, String ma, Double pe, Double pr) {
        this.medidas = me;
        this.material = ma;
        this.peso = pe;
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
     * Devuelve las medidas.
     *
     * @return Las medidas
     */
    public String getMedidas() {
        return medidas;
    }

    /**
     * Establece las medidas.
     *
     * @param medidas las medidas
     */
    public void setMedidas(String medidas) {
        this.medidas = medidas;
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
     * Devuelve el peso en kilogramos.
     *
     * @return El peso en kilogramos
     */
    public Double getPeso() {
        return peso;
    }

    /**
     * Establece el peso en kilogramos.
     *
     * @param peso el peso en kilogramos
     */
    public void setPeso(Double peso) {
        this.peso = peso;
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
     * Devuelve una descripción en texto de el féretro.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return ("Medidas: " + medidas + ", material: " + material + ", peso: " + peso + " kg, precio: " + precio + "€");
    }

    // hashCode
    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(medidas, material, peso, precio);
    }

    // equals
    /**
     * Compara este objeto con otro: dos objetos son iguales si tienen el mismo valor de medidas.
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
        Feretro other = (Feretro) obj;

        return medidas.equals(other.medidas);
    }

    /**
     * Compara este objeto con otro por medidas.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Feretro other) {
        return this.medidas.compareTo(other.medidas);
    }
}
