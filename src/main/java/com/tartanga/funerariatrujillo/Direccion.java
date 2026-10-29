package com.tartanga.funerariatrujillo;

import java.io.Serializable;
import java.util.Objects;

public class Direccion implements Comparable<Direccion>, Serializable {

    private static final long serialVersionUID = 1L;

    private String calle;
    private String ciudad;
    private int codigo_postal;

    public Direccion() {
        calle = "";
        ciudad = "";
        codigo_postal = 0;
    }

    public Direccion(Direccion c) {
        this.calle = c.calle;
        this.ciudad = c.ciudad;
        this.codigo_postal = c.codigo_postal;
    }

    public Direccion(String ca, String ci, int co) {
        this.calle = ca;
        this.ciudad = ci;
        this.codigo_postal = co;
    }

    // Getters and Setters
    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getCodigo_postal() {
        return codigo_postal;
    }

    public void setCodigo_postal(int codigo_postal) {
        this.codigo_postal = codigo_postal;
    }

    // toString()
    @Override
    public String toString() {
        return ("Calle " + calle + ", " + codigo_postal + "(" + ciudad + ")");
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(calle, ciudad, codigo_postal);
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
        Direccion other = (Direccion) obj;

        return calle.equals(other.calle);
    }

    @Override
    public int compareTo(Direccion other) {
        return this.calle.compareTo(other.calle);
    }
}
