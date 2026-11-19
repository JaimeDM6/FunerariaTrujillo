package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Modelo de un cliente de la funeraria, es decir, la persona que contrata los servicios. Se
 * identifica por su DNI.
 *
 * @author Jaime
 */
public class Cliente implements Comparable<Cliente>, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String correo_electronico;
    private Direccion direccion;
    private String telefono;
    private Date fecha_registro;

    /**
     * Crea un cliente con valores por defecto.
     */
    public Cliente() {
        id = "";
        nombre = "";
        apellidos = "";
        dni = "";
        correo_electronico = "";
        direccion = new Direccion();
        telefono = "";
        fecha_registro = new Date();
    }

    /**
     * Crea un cliente copiando los datos de otro.
     *
     * @param c objeto que se copia
     */
    public Cliente(Cliente c) {
        this.id = c.id;
        this.nombre = c.nombre;
        this.apellidos = c.apellidos;
        this.dni = c.dni;
        this.correo_electronico = c.correo_electronico;
        this.direccion = c.direccion;
        this.telefono = c.telefono;
        this.fecha_registro = c.fecha_registro;
    }

    /**
     * Crea un cliente con todos sus datos.
     *
     * @param i el identificador
     * @param n el nombre
     * @param a los apellidos
     * @param d el DNI
     * @param c el correo electrónico
     * @param di la dirección
     * @param t el teléfono
     * @param f la fecha de registro
     */
    public Cliente(String i, String n, String a, String d, String c, Direccion di, String t, Date f) {
        this.id = i;
        this.nombre = n;
        this.apellidos = a;
        this.dni = d;
        this.correo_electronico = c;
        this.direccion = di;
        this.telefono = t;
        this.fecha_registro = f;
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
     * Devuelve el nombre y los apellidos del cliente juntos, sin espacios sobrantes en los extremos.
     *
     * @return nombre completo
     */
    public String getNombreCompleto() {
        return (this.nombre + " " + this.apellidos).trim();
    }

    /**
     * Devuelve el DNI.
     *
     * @return El DNI
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI.
     *
     * @param dni el DNI
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Devuelve el correo electrónico.
     *
     * @return El correo electrónico
     */
    public String getCorreo_electronico() {
        return correo_electronico;
    }

    /**
     * Establece el correo electrónico.
     *
     * @param correo_electronico el correo electrónico
     */
    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    /**
     * Devuelve la dirección.
     *
     * @return La dirección
     */
    public Direccion getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección.
     *
     * @param direccion la dirección
     */
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    /**
     * Devuelve el teléfono.
     *
     * @return El teléfono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono.
     *
     * @param telefono el teléfono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Devuelve la fecha de registro.
     *
     * @return La fecha de registro
     */
    public Date getFecha_registro() {
        return fecha_registro;
    }

    /**
     * Establece la fecha de registro.
     *
     * @param fecha_registro la fecha de registro
     */
    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    // toString()
    /**
     * Devuelve una descripción en texto de un cliente.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return ("Nombre: " + nombre + " " + apellidos + ", DNI: " + dni + ", correo electrónico: " + correo_electronico + ", dirección: " + direccion + ", teléfono: " + telefono + ", fecha de registro: " + fecha_registro);
    }

    // hashCode
    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellidos, dni, correo_electronico, direccion, telefono, fecha_registro);
    }

    // equals
    /**
     * Compara este objeto con otro: dos objetos son iguales si tienen el mismo valor de DNI.
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
        Cliente other = (Cliente) obj;

        return dni.equals(other.dni);
    }

    /**
     * Compara este objeto con otro por DNI.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Cliente other) {
        return this.nombre.compareTo(other.nombre);
    }
}
