package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

public class Sala implements Comparable<Sala>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String descripcion;
    private int capacidad;

    public Sala() {
        id = null;
        nombre = "";
        descripcion = "";
        capacidad = 0;
    }

    public Sala(Sala s) {
        this.id = s.id;
        this.nombre = s.nombre;
        this.descripcion = s.descripcion;
        this.capacidad = s.capacidad;
    }

    public Sala(String n, String d, int c) {
        this.id = null;
        this.nombre = n;
        this.descripcion = d;
        this.capacidad = c;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "Nombre: " + nombre
                + ", descripción: " + descripcion
                + ", capacidad: " + capacidad + " personas";
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

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

    @Override
    public int compareTo(Sala other) {
        return this.nombre.compareTo(other.nombre);
    }
}