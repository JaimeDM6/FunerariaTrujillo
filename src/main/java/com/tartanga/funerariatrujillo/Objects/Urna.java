package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

public class Urna implements Comparable<Urna>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Double capacidad;
    private String material;
    private Double precio;

    public Urna() {
        id = "";
        capacidad = 0.0;
        material = "";
        precio = 0.0;
    }

    public Urna(Urna u) {
        this.id = u.id;
        this.capacidad = u.capacidad;
        this.material = u.material;
        this.precio = u.precio;
    }

    public Urna(String i, Double c, String m, Double pr) {
        this.id = i;
        this.capacidad = c;
        this.material = m;
        this.precio = pr;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Double capacidad) {
        this.capacidad = capacidad;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    // toString()
    @Override
    public String toString() {
        return ("Capacidad: " + capacidad + "L, material: " + material + ", precio: " + precio + "€");
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(capacidad, material, precio);
    }

    // equals
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

    @Override
    public int compareTo(Urna other) {
        return this.capacidad.compareTo(other.capacidad);
    }
}
