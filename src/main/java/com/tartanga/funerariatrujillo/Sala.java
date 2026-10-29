package com.tartanga.funerariatrujillo;

import java.io.Serializable;
import java.util.Objects;

public class Sala implements Comparable<Sala>, Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String descripcion;
    private int capacidad;

    public Sala() {
        nombre = "";
        descripcion = "";
        capacidad = 0;
    }

    public Sala(Sala s) {
        this.nombre = s.nombre;
        this.descripcion = s.descripcion;
        this.capacidad = s.capacidad;
    }

    public Sala(String n, String d, int c) {
        this.nombre = n;
        this.descripcion = d;
        this.capacidad = c;
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

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    // toString()
    @Override
    public String toString() {
        return ("Nombre: " + nombre + ", descripción: " + descripcion + ", capacidad: " + capacidad + " personas");
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(nombre, descripcion, capacidad);
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
        Sala other = (Sala) obj;

        return nombre.equals(other.nombre);
    }

    @Override
    public int compareTo(Sala other) {
        return this.nombre.compareTo(other.nombre);
    }
}
