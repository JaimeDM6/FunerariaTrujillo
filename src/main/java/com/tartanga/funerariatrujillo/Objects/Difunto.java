package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Difunto implements Comparable<Difunto>, Serializable {

    private static final long serialVersionUID = 1L;

    public enum Servicio {
        ENTIERRO, CREMACIÓN
    }

    private String id;
    private String id_cliente;
    private String id_feretro;
    private String id_urna;
    private String nombre;
    private String apellidos;
    private Date fecha_nacimiento;
    private Date fecha_muerte;
    private String causa_muerte;
    private Servicio servicio;
    private Feretro feretro;
    private Urna urna;
    private ArrayList<Servicio> lista_servicios;

    public Difunto() {
        id = "";
        id_cliente = "";
        id_feretro = "";
        id_urna = "";
        nombre = "";
        apellidos = "";
        fecha_nacimiento = new Date();
        fecha_muerte = new Date();
        causa_muerte = "";
        servicio = Servicio.ENTIERRO;
        feretro = new Feretro();
        urna = new Urna();
        lista_servicios = new ArrayList<>();
    }

    public Difunto(Difunto d) {
        this.id = d.id;
        this.id_cliente = d.id_cliente;
        this.id_feretro = d.id_feretro;
        this.id_urna = d.id_urna;
        this.nombre = d.nombre;
        this.apellidos = d.apellidos;
        this.fecha_nacimiento = d.fecha_nacimiento;
        this.fecha_muerte = d.fecha_muerte;
        this.causa_muerte = d.causa_muerte;
        this.servicio = d.servicio;
        this.feretro = d.feretro;
        this.urna = d.urna;
        this.lista_servicios = d.lista_servicios;
    }

    public Difunto(String id, String n, String a, Date fn, Date fm, String c, Servicio s, Feretro f, Urna u, ArrayList<Servicio> l) {
        this.id = id;
        this.nombre = n;
        this.apellidos = a;
        this.fecha_nacimiento = fn;
        this.fecha_muerte = fm;
        this.causa_muerte = c;
        this.servicio = s;
        this.feretro = f;
        this.urna = u;
        this.lista_servicios = l;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_feretro() {
        return id_feretro;
    }

    public void setId_feretro(String id_feretro) {
        this.id_feretro = id_feretro;
    }

    public String getId_urna() {
        return id_urna;
    }

    public void setId_urna(String id_urna) {
        this.id_urna = id_urna;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreCompleto() {
        return (this.nombre + " " + this.apellidos).trim();
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public Date getFecha_muerte() {
        return fecha_muerte;
    }

    public void setFecha_muerte(Date fecha_muerte) {
        this.fecha_muerte = fecha_muerte;
    }

    public String getCausa_muerte() {
        return causa_muerte;
    }

    public void setCausa_muerte(String causa_muerte) {
        this.causa_muerte = causa_muerte;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Feretro getFeretro() {
        return feretro;
    }

    public void setFeretro(Feretro feretro) {
        this.feretro = feretro;
    }

    public Urna getUrna() {
        return urna;
    }

    public void setUrna(Urna urna) {
        this.urna = urna;
    }

    public ArrayList<Servicio> getLista_servicios() {
        return lista_servicios;
    }

    public void setLista_servicios(ArrayList<Servicio> lista_servicios) {
        this.lista_servicios = lista_servicios;
    }

    // toString()
    @Override
    public String toString() {
        return ("ID: " + id + " \n" + "Nombre: " + nombre + " " + apellidos + ", fecha de nacimiento: " + fecha_nacimiento + ", fecha de muerte: " + fecha_muerte + ", dirección: " + causa_muerte + ", servicio: " + servicio);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, apellidos, fecha_nacimiento, fecha_muerte, causa_muerte, servicio, feretro, urna, lista_servicios);
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
        Difunto other = (Difunto) obj;

        return nombre.equals(other.nombre);
    }

    @Override
    public int compareTo(Difunto other) {
        return this.nombre.compareTo(other.nombre);
    }
}
