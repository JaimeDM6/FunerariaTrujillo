package com.tartanga.funerariatrujillo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Cliente implements Comparable<Cliente>, Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellidos;
    private String dni;
    private String correo_electronico;
    private Direccion direccion;
    private int telefono;
    private Date fecha_registro;

    public Cliente() {
        nombre = "";
        apellidos = "";
        dni = "";
        correo_electronico = "";
        direccion = new Direccion();
        telefono = 0;
        fecha_registro = new Date();
    }

    public Cliente(Cliente c) {
        this.nombre = c.nombre;
        this.apellidos = c.apellidos;
        this.dni = c.dni;
        this.correo_electronico = c.correo_electronico;
        this.direccion = c.direccion;
        this.telefono = c.telefono;
        this.fecha_registro = c.fecha_registro;
    }

    public Cliente(String n, String a, String d, String c, Direccion di, int t, Date f) {
        this.nombre = n;
        this.apellidos = a;
        this.dni = d;
        this.correo_electronico = c;
        this.direccion = di;
        this.telefono = t;
        this.fecha_registro = f;
    }

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public Date getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    // toString()
    @Override
    public String toString() {
        return ("Nombre: " + nombre + " " + apellidos + ", DNI: " + dni + ", correo electrónico: " + correo_electronico + ", dirección: " + direccion + ", teléfono: " + telefono + ", fecha de registro: " + fecha_registro);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellidos, dni, correo_electronico, direccion, telefono, fecha_registro);
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
        Cliente other = (Cliente) obj;

        return dni.equals(other.dni);
    }

    @Override
    public int compareTo(Cliente other) {
        return this.nombre.compareTo(other.nombre);
    }
}
