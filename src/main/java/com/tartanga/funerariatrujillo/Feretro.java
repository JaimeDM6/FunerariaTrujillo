package com.tartanga.funerariatrujillo;

import java.io.Serializable;
import java.util.Objects;

public class Feretro implements Comparable<Feretro>, Serializable {

    private static final long serialVersionUID = 1L;

    private String medidas;
    private String material;
    private Double peso;
    private Double precio;

    public Feretro() {
        medidas = "";
        material = "";
        peso = 0.0;
        precio = 0.0;
    }

    public Feretro(Feretro f) {
        this.medidas = f.medidas;
        this.material = f.material;
        this.peso = f.peso;
        this.precio = f.precio;
    }

    public Feretro(String me, String ma, Double pe, Double pr) {
        this.medidas = me;
        this.material = ma;
        this.peso = pe;
        this.precio = pr;
    }

    // Getters and Setters
    public String getMedidas() {
        return medidas;
    }

    public void setMedidas(String medidas) {
        this.medidas = medidas;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    // toString()
    @Override
    public String toString() {
        return ("Medidas: " + medidas + ", material: " + material + ", peso: " + peso + " kg, precio: " + precio + "€");
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(medidas, material, peso, precio);
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
        Feretro other = (Feretro) obj;

        return medidas.equals(other.medidas);
    }

    @Override
    public int compareTo(Feretro other) {
        return this.medidas.compareTo(other.medidas);
    }
}
