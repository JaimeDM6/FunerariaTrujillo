package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Asignacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String idDifunto;
    private String idTrabajador;
    private String difuntoNombre;
    private String trabajadorNombre;
    private String estado;
    private Date fechaAsignacion;
    private Date fechaInicioTrabajo;
    private Date fechaFinalizacion;
    private String indicacionesFamilia;
    private boolean pasoIdentificacion;
    private boolean pasoConservacion;
    private boolean pasoEstetica;
    private String observaciones;
    private Date ultimaActualizacion;

    public Asignacion() {
        id = null;
        idDifunto = null;
        idTrabajador = null;
        difuntoNombre = "";
        trabajadorNombre = "";
        estado = "Pendiente";
        fechaAsignacion = null;
        fechaInicioTrabajo = null;
        fechaFinalizacion = null;
        indicacionesFamilia = "";
        pasoIdentificacion = false;
        pasoConservacion = false;
        pasoEstetica = false;
        observaciones = "";
        ultimaActualizacion = null;
    }

    public Asignacion(Asignacion a) {
        this.id = a.id;
        this.idDifunto = a.idDifunto;
        this.idTrabajador = a.idTrabajador;
        this.difuntoNombre = a.difuntoNombre;
        this.trabajadorNombre = a.trabajadorNombre;
        this.estado = a.estado;
        this.fechaAsignacion = copiarFecha(a.fechaAsignacion);
        this.fechaInicioTrabajo = copiarFecha(a.fechaInicioTrabajo);
        this.fechaFinalizacion = copiarFecha(a.fechaFinalizacion);
        this.indicacionesFamilia = a.indicacionesFamilia;
        this.pasoIdentificacion = a.pasoIdentificacion;
        this.pasoConservacion = a.pasoConservacion;
        this.pasoEstetica = a.pasoEstetica;
        this.observaciones = a.observaciones;
        this.ultimaActualizacion = copiarFecha(a.ultimaActualizacion);
    }

    private static Date copiarFecha(Date fecha) {
        Date copia = null;
        if (fecha != null) {
            copia = new Date(fecha.getTime());
        }
        return copia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdDifunto() {
        return idDifunto;
    }

    public void setIdDifunto(String idDifunto) {
        this.idDifunto = idDifunto;
    }

    public String getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(String idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getDifuntoNombre() {
        return difuntoNombre;
    }

    public void setDifuntoNombre(String difuntoNombre) {
        this.difuntoNombre = difuntoNombre;
    }

    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaAsignacion() {
        return copiarFecha(fechaAsignacion);
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = copiarFecha(fechaAsignacion);
    }

    public Date getFechaInicioTrabajo() {
        return copiarFecha(fechaInicioTrabajo);
    }

    public void setFechaInicioTrabajo(Date fechaInicioTrabajo) {
        this.fechaInicioTrabajo = copiarFecha(fechaInicioTrabajo);
    }

    public Date getFechaFinalizacion() {
        return copiarFecha(fechaFinalizacion);
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = copiarFecha(fechaFinalizacion);
    }

    public String getIndicacionesFamilia() {
        return indicacionesFamilia;
    }

    public void setIndicacionesFamilia(String indicacionesFamilia) {
        this.indicacionesFamilia = indicacionesFamilia;
    }

    public boolean isPasoIdentificacion() {
        return pasoIdentificacion;
    }

    public void setPasoIdentificacion(boolean pasoIdentificacion) {
        this.pasoIdentificacion = pasoIdentificacion;
    }

    public boolean isPasoConservacion() {
        return pasoConservacion;
    }

    public void setPasoConservacion(boolean pasoConservacion) {
        this.pasoConservacion = pasoConservacion;
    }

    public boolean isPasoEstetica() {
        return pasoEstetica;
    }

    public void setPasoEstetica(boolean pasoEstetica) {
        this.pasoEstetica = pasoEstetica;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getUltimaActualizacion() {
        return copiarFecha(ultimaActualizacion);
    }

    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = copiarFecha(ultimaActualizacion);
    }

    @Override
    public String toString() {
        return difuntoNombre + " - " + trabajadorNombre + " (" + estado + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        boolean iguales = false;

        if (this == obj) {
            iguales = true;
        } else if (obj instanceof Asignacion) {
            Asignacion otra = (Asignacion) obj;
            iguales = id != null && Objects.equals(id, otra.id);
        }

        return iguales;
    }
}
