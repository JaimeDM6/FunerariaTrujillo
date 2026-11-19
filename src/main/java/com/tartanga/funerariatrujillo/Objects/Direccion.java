package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de una dirección postal formada por calle, ciudad y código postal. La utilizan clientes y
 * trabajadores.
 *
 * @author Aritz
 */
public class Direccion implements Comparable<Direccion>, Serializable {

    private static final long serialVersionUID = 1L;

    private String calle;
    private String ciudad;
    private int codigo_postal;

    /**
     * Crea una dirección con valores por defecto.
     */
    public Direccion() {
        calle = "";
        ciudad = "";
        codigo_postal = 0;
    }

    /**
     * Crea una dirección copiando los datos de otro.
     *
     * @param c objeto que se copia
     */
    public Direccion(Direccion c) {
        this.calle = c.calle;
        this.ciudad = c.ciudad;
        this.codigo_postal = c.codigo_postal;
    }

    /**
     * Crea una dirección con todos sus datos.
     *
     * @param ca la calle
     * @param ci la ciudad
     * @param co el código postal
     */
    public Direccion(String ca, String ci, int co) {
        this.calle = ca;
        this.ciudad = ci;
        this.codigo_postal = co;
    }

    // Getters and Setters
    /**
     * Devuelve la calle.
     *
     * @return La calle
     */
    public String getCalle() {
        return calle;
    }

    /**
     * Establece la calle.
     *
     * @param calle la calle
     */
    public void setCalle(String calle) {
        this.calle = calle;
    }

    /**
     * Devuelve la ciudad.
     *
     * @return La ciudad
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Establece la ciudad.
     *
     * @param ciudad la ciudad
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * Devuelve el código postal.
     *
     * @return El código postal
     */
    public int getCodigoPostal() {
        return codigo_postal;
    }

    /**
     * Establece el código postal.
     *
     * @param codigo_postal el código postal
     */
    public void setCodigoPostal(int codigo_postal) {
        this.codigo_postal = codigo_postal;
    }

    // toString()
    /**
     * Devuelve una descripción en texto de la dirección.
     *
     * @return texto con los datos principales
     */
    @Override
    public String toString() {
        return (calle + ", " + codigo_postal + " (" + ciudad + ")");
    }

    // hashCode
    /**
     * Calcula el código hash del objeto.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(calle, ciudad, codigo_postal);
    }

    // equals
    /**
     * Compara este objeto con otro: dos objetos son iguales si tienen el mismo valor de calle.
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
        Direccion other = (Direccion) obj;

        return calle.equals(other.calle);
    }

    /**
     * Compara este objeto con otro por calle.
     *
     * @param other objeto con el que se compara
     * @return valor negativo, cero o positivo según este objeto sea menor, igual o mayor que el otro
     */
    @Override
    public int compareTo(Direccion other) {
        return this.calle.compareTo(other.calle);
    }
}
