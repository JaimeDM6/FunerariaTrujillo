package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Modelo de un difunto atendido por la funeraria, con el cliente responsable, el féretro, la urna
 * y los servicios contratados. Se identifica por su nombre.
 *
 * @author Iker
 */
public class Difunto implements Comparable<Difunto>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Tipo de servicio que se presta a un difunto.
     */
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

    /**
     * Crea un difunto con valores por defecto.
     */
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

    /**
     * Crea un difunto copiando los datos de otro.
     *
     * @param d objeto que se copia
     */
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

    /**
     * Crea un difunto con todos sus datos.
     *
     * @param id el identificador
     * @param n el nombre
     * @param a los apellidos
     * @param fn la fecha de nacimiento
     * @param fm la fecha de fallecimiento
     * @param c la causa de la muerte
     * @param s el tipo de servicio (entierro o cremación)
     * @param f el féretro
     * @param u la urna
     * @param l la lista de servicios contratados
     */
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
     * Devuelve el identificador del cliente responsable.
     *
     * @return El identificador del cliente responsable
     */
    public String getId_cliente() {
        return id_cliente;
    }

    /**
     * Establece el identificador del cliente responsable.
     *
     * @param id_cliente el identificador del cliente responsable
     */
    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    /**
     * Devuelve el identificador del féretro.
     *
     * @return El identificador del féretro
     */
    public String getId_feretro() {
        return id_feretro;
    }

    /**
     * Establece el identificador del féretro.
     *
     * @param id_feretro el identificador del féretro
     */
    public void setId_feretro(String id_feretro) {
        this.id_feretro = id_feretro;
    }

    /**
     * Devuelve el identificador de la urna.
     *
     * @return El identificador de la urna
     */
    public String getId_urna() {
        return id_urna;
    }

    /**
     * Establece el identificador de la urna.
     *
     * @param id_urna el identificador de la urna
     */
    public void setId_urna(String id_urna) {
        this.id_urna = id_urna;
    }

    /**
     * Devuelve el nombre.
     *
     * @return El nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre.
     *
     * @param nombre el nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el nombre y los apellidos del difunto juntos, sin espacios sobrantes en los extremos.
     *
     * @return nombre completo
     */
    public String getNombreCompleto() {
        return (this.nombre + " " + this.apellidos).trim();
    }

    /**
     * Devuelve los apellidos.
     *
     * @return Los apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos.
     *
     * @param apellidos los apellidos
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Devuelve la fecha de nacimiento.
     *
     * @return La fecha de nacimiento
     */
    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    /**
     * Establece la fecha de nacimiento.
     *
     * @param fecha_nacimiento la fecha de nacimiento
     */
    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    /**
     * Devuelve la fecha de fallecimiento.
     *
     * @return La fecha de fallecimiento
     */
    public Date getFecha_muerte() {
        return fecha_muerte;
    }

    /**
     * Establece la fecha de fallecimiento.
     *
     * @param fecha_muerte la fecha de fallecimiento
     */
    public void setFecha_muerte(Date fecha_muerte) {
        this.fecha_muerte = fecha_muerte;
    }

    /**
     * Devuelve la causa de la muerte.
     *
     * @return La causa de la muerte
     */
    public String getCausa_muerte() {
        return causa_muerte;
    }

    /**
     * Establece la causa de la muerte.
     *
     * @param causa_muerte la causa de la muerte
     */
    public void setCausa_muerte(String causa_muerte) {
        this.causa_muerte = causa_muerte;
    }

    /**
     * Devuelve el tipo de servicio (entierro o cremación).
     *
     * @return El tipo de servicio (entierro o cremación)
     */
    public Servicio getServicio() {
        return servicio;
    }

    /**
     * Establece el tipo de servicio (entierro o cremación).
     *
     * @param servicio el tipo de servicio (entierro o cremación)
     */
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    /**
     * Devuelve el féretro.
     *
     * @return El féretro
     */
    public Feretro getFeretro() {
        return feretro;
    }

    /**
     * Establece el féretro.
     *
     * @param feretro el féretro
     */
    public void setFeretro(Feretro feretro) {
        this.feretro = feretro;
    }

    /**
     * Devuelve la urna.
     *
     * @return La urna
     */
    public Urna getUrna() {
        return urna;
    }

    /**
     * Establece la urna.
     *
     * @param urna la urna
     */
    public void setUrna(Urna urna) {
        this.urna = urna;
    }

    /**
     * Devuelve la lista de servicios contratados.
     *
     * @return La lista de servicios contratados
     */
    public ArrayList<Servicio> getLista_servicios() {
        return lista_servicios;
    }

    /**
     * Establece la lista de servicios contratados.
     *
     * @param lista_servicios la lista de servicios contratados
     */
    public void setLista_servicios(ArrayList<Servicio> lista_servicios) {
        this.lista_servicios = lista_servicios;
    }

    // toString()
    /**
     * Devuelve una descripción en texto de un difunto.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return ("ID: " + id + " \n" + "Nombre: " + nombre + " " + apellidos + ", fecha de nacimiento: " + fecha_nacimiento + ", fecha de muerte: " + fecha_muerte + ", dirección: " + causa_muerte + ", servicio: " + servicio);
    }

    // hashCode
    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, apellidos, fecha_nacimiento, fecha_muerte, causa_muerte, servicio, feretro, urna, lista_servicios);
    }

    // equals
    /**
     * Compara este objeto con otro: dos objetos son iguales si tienen el mismo valor de nombre.
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
        Difunto other = (Difunto) obj;

        return nombre.equals(other.nombre);
    }

    /**
     * Compara este objeto con otro por nombre.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Difunto other) {
        return this.nombre.compareTo(other.nombre);
    }
}
