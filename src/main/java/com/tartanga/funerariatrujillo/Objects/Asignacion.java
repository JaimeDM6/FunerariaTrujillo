package com.tartanga.funerariatrujillo.Objects;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Modelo de una asignación de servicio: relaciona a un difunto con el trabajador responsable e
 * incluye el estado, las fechas y el seguimiento del trabajo. Se identifica por su identificador.
 *
 * @author Aritz
 */
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

    /**
     * Crea una asignación vacía en estado Pendiente.
     */
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

    /**
     * Crea una asignación copiando los datos de otra, incluidas las fechas.
     *
     * @param a asignación que se copia
     */
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

    /**
     * Devuelve una copia de una fecha para no compartir el objeto mutable.
     *
     * @param fecha fecha que se copia, que puede ser nula
     * @return copia de la fecha, o {@code null} si era nula
     */
    private static Date copiarFecha(Date fecha) {
        Date copia = null;
        if (fecha != null) {
            copia = new Date(fecha.getTime());
        }
        return copia;
    }

    /**
     * Devuelve el identificador de la asignación.
     *
     * @return El identificador de la asignación
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador de la asignación.
     *
     * @param id el identificador de la asignación
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Devuelve el identificador del difunto.
     *
     * @return El identificador del difunto
     */
    public String getIdDifunto() {
        return idDifunto;
    }

    /**
     * Establece el identificador del difunto.
     *
     * @param idDifunto el identificador del difunto
     */
    public void setIdDifunto(String idDifunto) {
        this.idDifunto = idDifunto;
    }

    /**
     * Devuelve el identificador del trabajador responsable.
     *
     * @return El identificador del trabajador responsable
     */
    public String getIdTrabajador() {
        return idTrabajador;
    }

    /**
     * Establece el identificador del trabajador responsable.
     *
     * @param idTrabajador el identificador del trabajador responsable
     */
    public void setIdTrabajador(String idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    /**
     * Devuelve el nombre del difunto.
     *
     * @return El nombre del difunto
     */
    public String getDifuntoNombre() {
        return difuntoNombre;
    }

    /**
     * Establece el nombre del difunto.
     *
     * @param difuntoNombre el nombre del difunto
     */
    public void setDifuntoNombre(String difuntoNombre) {
        this.difuntoNombre = difuntoNombre;
    }

    /**
     * Devuelve el nombre del trabajador responsable.
     *
     * @return El nombre del trabajador responsable
     */
    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    /**
     * Establece el nombre del trabajador responsable.
     *
     * @param trabajadorNombre el nombre del trabajador responsable
     */
    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    /**
     * Devuelve el estado de la asignación (Pendiente, En curso o Completado).
     *
     * @return El estado de la asignación (Pendiente, En curso o Completado)
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la asignación (Pendiente, En curso o Completado).
     *
     * @param estado el estado de la asignación (Pendiente, En curso o Completado)
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Devuelve la fecha de asignación.
     *
     * @return La fecha de asignación
     */
    public Date getFechaAsignacion() {
        return copiarFecha(fechaAsignacion);
    }

    /**
     * Establece la fecha de asignación.
     *
     * @param fechaAsignacion la fecha de asignación
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = copiarFecha(fechaAsignacion);
    }

    /**
     * Devuelve la fecha de inicio del trabajo.
     *
     * @return La fecha de inicio del trabajo
     */
    public Date getFechaInicioTrabajo() {
        return copiarFecha(fechaInicioTrabajo);
    }

    /**
     * Establece la fecha de inicio del trabajo.
     *
     * @param fechaInicioTrabajo la fecha de inicio del trabajo
     */
    public void setFechaInicioTrabajo(Date fechaInicioTrabajo) {
        this.fechaInicioTrabajo = copiarFecha(fechaInicioTrabajo);
    }

    /**
     * Devuelve la fecha de finalización del trabajo.
     *
     * @return La fecha de finalización del trabajo
     */
    public Date getFechaFinalizacion() {
        return copiarFecha(fechaFinalizacion);
    }

    /**
     * Establece la fecha de finalización del trabajo.
     *
     * @param fechaFinalizacion la fecha de finalización del trabajo
     */
    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = copiarFecha(fechaFinalizacion);
    }

    /**
     * Devuelve las indicaciones de la familia.
     *
     * @return Las indicaciones de la familia
     */
    public String getIndicacionesFamilia() {
        return indicacionesFamilia;
    }

    /**
     * Establece las indicaciones de la familia.
     *
     * @param indicacionesFamilia las indicaciones de la familia
     */
    public void setIndicacionesFamilia(String indicacionesFamilia) {
        this.indicacionesFamilia = indicacionesFamilia;
    }

    /**
     * Indica si el paso de identificación está completado.
     *
     * @return {@code true} si el paso de identificación está completado
     */
    public boolean isPasoIdentificacion() {
        return pasoIdentificacion;
    }

    /**
     * Establece si el paso de identificación está completado.
     *
     * @param pasoIdentificacion {@code true} si el paso de identificación está completado
     */
    public void setPasoIdentificacion(boolean pasoIdentificacion) {
        this.pasoIdentificacion = pasoIdentificacion;
    }

    /**
     * Indica si el paso de conservación está completado.
     *
     * @return {@code true} si el paso de conservación está completado
     */
    public boolean isPasoConservacion() {
        return pasoConservacion;
    }

    /**
     * Establece si el paso de conservación está completado.
     *
     * @param pasoConservacion {@code true} si el paso de conservación está completado
     */
    public void setPasoConservacion(boolean pasoConservacion) {
        this.pasoConservacion = pasoConservacion;
    }

    /**
     * Indica si el paso de estética está completado.
     *
     * @return {@code true} si el paso de estética está completado
     */
    public boolean isPasoEstetica() {
        return pasoEstetica;
    }

    /**
     * Establece si el paso de estética está completado.
     *
     * @param pasoEstetica {@code true} si el paso de estética está completado
     */
    public void setPasoEstetica(boolean pasoEstetica) {
        this.pasoEstetica = pasoEstetica;
    }

    /**
     * Devuelve las observaciones del seguimiento.
     *
     * @return Las observaciones del seguimiento
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones del seguimiento.
     *
     * @param observaciones las observaciones del seguimiento
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Devuelve la fecha de la última actualización del seguimiento.
     *
     * @return La fecha de la última actualización del seguimiento
     */
    public Date getUltimaActualizacion() {
        return copiarFecha(ultimaActualizacion);
    }

    /**
     * Establece la fecha de la última actualización del seguimiento.
     *
     * @param ultimaActualizacion la fecha de la última actualización del seguimiento
     */
    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = copiarFecha(ultimaActualizacion);
    }

    /**
     * Devuelve una descripción en texto de la asignación.
     *
     * @return difunto, trabajador y estado
     */
    @Override
    public String toString() {
        return difuntoNombre + " - " + trabajadorNombre + " (" + estado + ")";
    }

    /**
     * Calcula el código hash a partir del identificador.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Compara con otro objeto: dos asignaciones son iguales si tienen el mismo identificador.
     *
     * @param obj objeto con el que se compara
     * @return {@code true} si son iguales
     */
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
