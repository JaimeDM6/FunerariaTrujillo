package com.tartanga.funerariatrujillo;

import java.io.Serializable;
import java.util.Objects;

public class Trabajador implements Comparable<Trabajador>, Serializable {

    private static final long serialVersionUID = 1L;

    public enum Cargo {
        TANATOPRACTOR, AGENTE_FUNERARIO, RECEPCIONISTA, DIRECTOR, CONDUCTOR
    }

    private String nombre;
    private String apellidos;
    private String dni;
    private String correo_electronico;
    private Direccion direccion;
    private int numero_ss;
    private String iban;
    private Cargo cargo;
    private Double salario;
    private String tipo_jornada;

    public Trabajador() {
        nombre = "";
        apellidos = "";
        dni = "";
        correo_electronico = "";
        direccion = new Direccion();
        numero_ss = 0;
        iban = "";
        cargo = Cargo.AGENTE_FUNERARIO;
        salario = 0.0;
        tipo_jornada = "";
    }

    public Trabajador(Trabajador t) {
        this.nombre = t.nombre;
        this.apellidos = t.apellidos;
        this.dni = t.dni;
        this.correo_electronico = t.correo_electronico;
        this.direccion = t.direccion;
        this.numero_ss = t.numero_ss;
        this.iban = t.iban;
        this.cargo = t.cargo;
        this.salario = t.salario;
        this.tipo_jornada = t.tipo_jornada;
    }

    public Trabajador(String n, String a, String d, String c, Direccion di, int nu, String i, Cargo ca, Double s, String t) {
        this.nombre = n;
        this.apellidos = a;
        this.dni = d;
        this.correo_electronico = c;
        this.direccion = di;
        this.numero_ss = nu;
        this.iban = i;
        this.cargo = ca;
        this.salario = s;
        this.tipo_jornada = t;
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

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public int getNumero_ss() {
        return numero_ss;
    }

    public void setNumero_ss(int numero_ss) {
        this.numero_ss = numero_ss;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public String getTipo_jornada() {
        return tipo_jornada;
    }

    public void setTipo_jornada(String tipo_jornada) {
        this.tipo_jornada = tipo_jornada;
    }

    // toString()
    @Override
    public String toString() {
        return ("Nombre: " + nombre + " " + apellidos + ", DNI: " + dni + ", correo electrónico: " + correo_electronico + ", dirección: " + direccion + ", número de la SS: " + numero_ss + ", IBAN: " + iban + ", cargo: " + cargo + ", salario: " + salario + ", tipo de jornada: " + tipo_jornada);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellidos, dni, correo_electronico, direccion, numero_ss, iban, cargo, salario, tipo_jornada);
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
        Trabajador other = (Trabajador) obj;

        return dni.equals(other.dni);
    }

    @Override
    public int compareTo(Trabajador other) {
        return this.nombre.compareTo(other.nombre);
    }
}
