package com.tartanga.funerariatrujillo;

import java.io.Serializable;
import java.util.Objects;

public class Servicios implements Comparable<Servicios>, Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String descripcion;
    private Double precio;

    public Servicios() {
        nombre = "";
        descripcion = "";
        precio = 0.0;
    }

    public Servicios(Servicios s) {
        this.nombre = s.nombre;
        this.descripcion = s.descripcion;
        this.precio = s.precio;
    }

    public Servicios(String n, String d, Double p) {
        this.nombre = n;
        this.descripcion = d;
        this.precio = p;
    }

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        return ("Nombre " + nombre + ", descripción: " + descripcion + ", precio: " + precio + "€");
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(nombre, descripcion, precio);
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
        Servicios other = (Servicios) obj;

        return nombre.equals(other.nombre);
    }

    @Override
    public int compareTo(Servicios other) {
        return this.nombre.compareTo(other.nombre);
    }
}
