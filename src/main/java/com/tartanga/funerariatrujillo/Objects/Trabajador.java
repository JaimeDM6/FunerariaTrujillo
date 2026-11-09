package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

public class Trabajador implements Comparable<Trabajador>, Serializable {

    private static final long serialVersionUID = 1L;

    public enum Cargo {
        TANATOPRACTOR, AGENTE_FUNERARIO, RECEPCIONISTA, DIRECTOR, CONDUCTOR
    }

    public enum Rol {
        AGENTE_FUNERARIO("Agente Funerario"),
        CONTABILIDAD("Contabilidad"),
        TANATOPRAXIA("Tanatopraxia"),
        ADMINISTRACION("Administración");

        private final String nombreVisible;

        Rol(String nombreVisible) {
            this.nombreVisible = nombreVisible;
        }

        @Override
        public String toString() {
            return nombreVisible;
        }
    }

    private String id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String correo_electronico;
    private String usuario;
    /**
     * Contraseña almacenada como hash PBKDF2, nunca en texto plano.
     */
    private String password;
    private Direccion direccion;
    private String numero_ss;
    private String iban;
    private Cargo cargo;
    private Rol rol;
    private Double salario;
    private String tipo_jornada;

    public Trabajador() {
        id = "";
        nombre = "";
        apellidos = "";
        dni = "";
        correo_electronico = "";
        usuario = "";
        password = "";
        direccion = new Direccion();
        numero_ss = "";
        iban = "";
        cargo = Cargo.AGENTE_FUNERARIO;
        rol = Rol.AGENTE_FUNERARIO;
        salario = 0.0;
        tipo_jornada = "";
    }

    public Trabajador(Trabajador t) {
        this.id = t.id;
        this.nombre = t.nombre;
        this.apellidos = t.apellidos;
        this.dni = t.dni;
        this.correo_electronico = t.correo_electronico;
        this.usuario = t.usuario;
        this.password = t.password;
        this.direccion = t.direccion;
        this.numero_ss = t.numero_ss;
        this.iban = t.iban;
        this.cargo = t.cargo;
        this.rol = t.rol;
        this.salario = t.salario;
        this.tipo_jornada = t.tipo_jornada;
    }

    public Trabajador(String n, String a, String d, String c, String u, String p, Direccion di, String nu, String i,
            Cargo ca, Double s, String t) {
        this.nombre = n;
        this.apellidos = a;
        this.dni = d;
        this.correo_electronico = c;
        this.usuario = u;
        this.password = p;
        this.direccion = di;
        this.numero_ss = nu;
        this.iban = i;
        this.cargo = ca;
        this.rol = ca == Cargo.TANATOPRACTOR ? Rol.TANATOPRAXIA : Rol.AGENTE_FUNERARIO;
        this.salario = s;
        this.tipo_jornada = t;
    }

    // Getters and Setters
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getNumero_ss() {
        return numero_ss;
    }

    public void setNumero_ss(String numero_ss) {
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
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
        return ("Nombre: " + nombre + " " + apellidos + ", DNI: " + dni + ", correo electrónico: " + correo_electronico
                + ", usuario: " + usuario + ", dirección: " + direccion + ", número de la SS: " + numero_ss + ", IBAN: "
                + iban + ", cargo: " + cargo + ", salario: " + salario + ", tipo de jornada: " + tipo_jornada);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellidos, dni, correo_electronico, usuario, password, direccion, numero_ss, iban,
                cargo, salario, tipo_jornada);
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
