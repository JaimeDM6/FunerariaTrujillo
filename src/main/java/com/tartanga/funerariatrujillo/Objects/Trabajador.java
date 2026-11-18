package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de un trabajador de la funeraria, con sus datos personales, su usuario de acceso, su
 * cargo y su rol en la aplicación. Se identifica por su DNI.
 *
 * @author Iker
 */
public class Trabajador implements Comparable<Trabajador>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Cargo que desempeña un trabajador en la funeraria.
     */
    public enum Cargo {
        TANATOPRACTOR("Tanatopractor"),
        AGENTE_FUNERARIO("Agente Funerario"),
        RECEPCIONISTA("Recepcionista"),
        DIRECTOR("Director"),
        CONDUCTOR("Conductor");

        private final String nombreVisible;

        /**
         * Crea un cargo con su nombre visible.
         *
         * @param nombreVisible nombre que se muestra al usuario
         */
        Cargo(String nombreVisible) {
            this.nombreVisible = nombreVisible;
        }

        /**
         * Devuelve el nombre del cargo tal como se muestra al usuario.
         *
         * @return nombre visible del cargo
         */
        @Override
        public String toString() {
            return nombreVisible;
        }
    }

    /**
     * Rol de un trabajador en la aplicación, que determina los módulos a los que tiene acceso.
     */
    public enum Rol {
        AGENTE_FUNERARIO("Agente Funerario"),
        CONTABILIDAD("Contabilidad"),
        TANATOPRAXIA("Tanatopraxia"),
        ADMINISTRACION("Administración"),
        TRANSPORTE("Transporte");

        private final String nombreVisible;

        /**
         * Crea un rol con su nombre visible.
         *
         * @param nombreVisible nombre que se muestra al usuario
         */
        Rol(String nombreVisible) {
            this.nombreVisible = nombreVisible;
        }

        /**
         * Devuelve el nombre del rol tal como se muestra al usuario.
         *
         * @return nombre visible del rol
         */
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
    private String password;
    private Direccion direccion;
    private String numero_ss;
    private String iban;
    private Cargo cargo;
    private Rol rol;
    private Double salario;
    private String tipo_jornada;

    /**
     * Crea un trabajador con valores por defecto.
     */
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

    /**
     * Crea un trabajador copiando los datos de otro.
     *
     * @param t objeto que se copia
     */
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

    /**
     * Crea un trabajador con todos sus datos.
     *
     * @param n el nombre
     * @param a los apellidos
     * @param d el DNI
     * @param c el correo electrónico
     * @param u el nombre de usuario
     * @param p la contraseña (cifrada con BCrypt cuando procede de la base de datos)
     * @param di la dirección
     * @param nu el número de la Seguridad Social
     * @param i el IBAN
     * @param ca el cargo
     * @param s el salario
     * @param t el tipo de jornada
     */
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
     * Devuelve el nombre y los apellidos del trabajador juntos, sin espacios sobrantes en los
     * extremos.
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
     * Devuelve el nombre de usuario.
     *
     * @return El nombre de usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param usuario el nombre de usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Devuelve la contraseña (cifrada con BCrypt cuando procede de la base de datos).
     *
     * @return La contraseña (cifrada con BCrypt cuando procede de la base de datos)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña (cifrada con BCrypt cuando procede de la base de datos).
     *
     * @param password la contraseña (cifrada con BCrypt cuando procede de la base de datos)
     */
    public void setPassword(String password) {
        this.password = password;
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
     * Devuelve el número de la Seguridad Social.
     *
     * @return El número de la Seguridad Social
     */
    public String getNumero_ss() {
        return numero_ss;
    }

    /**
     * Establece el número de la Seguridad Social.
     *
     * @param numero_ss el número de la Seguridad Social
     */
    public void setNumero_ss(String numero_ss) {
        this.numero_ss = numero_ss;
    }

    /**
     * Devuelve el IBAN.
     *
     * @return El IBAN
     */
    public String getIban() {
        return iban;
    }

    /**
     * Establece el IBAN.
     *
     * @param iban el IBAN
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Devuelve el cargo.
     *
     * @return El cargo
     */
    public Cargo getCargo() {
        return cargo;
    }

    /**
     * Establece el cargo.
     *
     * @param cargo el cargo
     */
    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    /**
     * Devuelve el rol en la aplicación.
     *
     * @return El rol en la aplicación
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * Establece el rol en la aplicación.
     *
     * @param rol el rol en la aplicación
     */
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * Devuelve el salario.
     *
     * @return El salario
     */
    public Double getSalario() {
        return salario;
    }

    /**
     * Establece el salario.
     *
     * @param salario el salario
     */
    public void setSalario(Double salario) {
        this.salario = salario;
    }

    /**
     * Devuelve el tipo de jornada.
     *
     * @return El tipo de jornada
     */
    public String getTipo_jornada() {
        return tipo_jornada;
    }

    /**
     * Establece el tipo de jornada.
     *
     * @param tipo_jornada el tipo de jornada
     */
    public void setTipo_jornada(String tipo_jornada) {
        this.tipo_jornada = tipo_jornada;
    }

    // toString()
    /**
     * Devuelve una descripción en texto de el trabajador.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return ("Nombre: " + nombre + " " + apellidos + ", DNI: " + dni + ", correo electrónico: " + correo_electronico
                + ", usuario: " + usuario + ", dirección: " + direccion + ", número de la SS: " + numero_ss + ", IBAN: "
                + iban + ", cargo: " + cargo + ", salario: " + salario + ", tipo de jornada: " + tipo_jornada);
    }

    // hashCode
    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellidos, dni, correo_electronico, usuario, password, direccion, numero_ss, iban,
                cargo, salario, tipo_jornada);
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
        Trabajador other = (Trabajador) obj;

        return dni.equals(other.dni);
    }

    /**
     * Compara este objeto con otro por DNI.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Trabajador other) {
        return this.nombre.compareTo(other.nombre);
    }
}
