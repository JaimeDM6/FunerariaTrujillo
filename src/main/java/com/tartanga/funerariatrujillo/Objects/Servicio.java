package com.tartanga.funerariatrujillo.Objects;

import java.util.Date;
import java.util.List;

/**
 * Modelo de la ficha de servicio. Reúne los datos de una asignación junto con los del trabajador,
 * el difunto, el cliente y lo contratado, además del seguimiento del trabajo realizado.
 *
 * @author Jaime
 */
public class Servicio {

    private String idAsignacion;
    private String idDifunto;
    private String idTrabajador;
    private String tituloServicio;
    private String estado;
    private Date fechaAsignacion;
    private Date fechaInicioTrabajo;
    private Date fechaFinalizacion;
    private String trabajadorNombre;
    private String trabajadorCargo;

    // Difunto
    private String difuntoNombre;
    private Date difuntoNacimiento;
    private Date difuntoFallecimiento;
    private String procedencia;
    private String causaMuerte;

    // Cliente
    private String clienteNombre;
    private String clienteRelacion;
    private String clienteTelefono;
    private String clienteCorreo;

    // Resumen Contratado
    private String tipoServicio;
    private String feretroInfo;
    private String urnaInfo;
    private List<String> listaServicios;

    // Seguimiento
    private String indicacionesFamilia;
    private boolean pasoIdentificacion;
    private boolean pasoConservacion;
    private boolean pasoEstetica;
    private String observaciones;
    private Date ultimaActualizacion;

    /**
     * Crea una ficha de servicio vacía.
     */
    public Servicio() {
    }

    /**
     * Devuelve el identificador de la asignación.
     *
     * @return El identificador de la asignación
     */
    public String getIdAsignacion() {
        return idAsignacion;
    }

    /**
     * Establece el identificador de la asignación.
     *
     * @param idAsignacion el identificador de la asignación
     */
    public void setIdAsignacion(String idAsignacion) {
        this.idAsignacion = idAsignacion;
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
     * Devuelve el identificador del trabajador.
     *
     * @return El identificador del trabajador
     */
    public String getIdTrabajador() {
        return idTrabajador;
    }

    /**
     * Establece el identificador del trabajador.
     *
     * @param idTrabajador el identificador del trabajador
     */
    public void setIdTrabajador(String idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    /**
     * Devuelve el título del servicio.
     *
     * @return El título del servicio
     */
    public String getTituloServicio() {
        return tituloServicio;
    }

    /**
     * Establece el título del servicio.
     *
     * @param tituloServicio el título del servicio
     */
    public void setTituloServicio(String tituloServicio) {
        this.tituloServicio = tituloServicio;
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
        return fechaAsignacion;
    }

    /**
     * Establece la fecha de asignación.
     *
     * @param fechaAsignacion la fecha de asignación
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Devuelve la fecha de inicio del trabajo.
     *
     * @return La fecha de inicio del trabajo
     */
    public Date getFechaInicioTrabajo() {
        return fechaInicioTrabajo;
    }

    /**
     * Establece la fecha de inicio del trabajo.
     *
     * @param fechaInicioTrabajo la fecha de inicio del trabajo
     */
    public void setFechaInicioTrabajo(Date fechaInicioTrabajo) {
        this.fechaInicioTrabajo = fechaInicioTrabajo;
    }

    /**
     * Devuelve la fecha de finalización del trabajo.
     *
     * @return La fecha de finalización del trabajo
     */
    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    /**
     * Establece la fecha de finalización del trabajo.
     *
     * @param fechaFinalizacion la fecha de finalización del trabajo
     */
    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    /**
     * Devuelve el nombre completo del trabajador asignado.
     *
     * @return El nombre completo del trabajador asignado
     */
    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    /**
     * Establece el nombre completo del trabajador asignado.
     *
     * @param trabajadorNombre el nombre completo del trabajador asignado
     */
    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    /**
     * Devuelve el cargo del trabajador asignado.
     *
     * @return El cargo del trabajador asignado
     */
    public String getTrabajadorCargo() {
        return trabajadorCargo;
    }

    /**
     * Establece el cargo del trabajador asignado.
     *
     * @param trabajadorCargo el cargo del trabajador asignado
     */
    public void setTrabajadorCargo(String trabajadorCargo) {
        this.trabajadorCargo = trabajadorCargo;
    }

    /**
     * Devuelve el nombre completo del difunto.
     *
     * @return El nombre completo del difunto
     */
    public String getDifuntoNombre() {
        return difuntoNombre;
    }

    /**
     * Establece el nombre completo del difunto.
     *
     * @param difuntoNombre el nombre completo del difunto
     */
    public void setDifuntoNombre(String difuntoNombre) {
        this.difuntoNombre = difuntoNombre;
    }

    /**
     * Devuelve la fecha de nacimiento del difunto.
     *
     * @return La fecha de nacimiento del difunto
     */
    public Date getDifuntoNacimiento() {
        return difuntoNacimiento;
    }

    /**
     * Establece la fecha de nacimiento del difunto.
     *
     * @param difuntoNacimiento la fecha de nacimiento del difunto
     */
    public void setDifuntoNacimiento(Date difuntoNacimiento) {
        this.difuntoNacimiento = difuntoNacimiento;
    }

    /**
     * Devuelve la fecha de fallecimiento del difunto.
     *
     * @return La fecha de fallecimiento del difunto
     */
    public Date getDifuntoFallecimiento() {
        return difuntoFallecimiento;
    }

    /**
     * Establece la fecha de fallecimiento del difunto.
     *
     * @param difuntoFallecimiento la fecha de fallecimiento del difunto
     */
    public void setDifuntoFallecimiento(Date difuntoFallecimiento) {
        this.difuntoFallecimiento = difuntoFallecimiento;
    }

    /**
     * Devuelve la procedencia del difunto.
     *
     * @return La procedencia del difunto
     */
    public String getProcedencia() {
        return procedencia;
    }

    /**
     * Establece la procedencia del difunto.
     *
     * @param procedencia la procedencia del difunto
     */
    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    /**
     * Devuelve la causa de la muerte.
     *
     * @return La causa de la muerte
     */
    public String getCausaMuerte() {
        return causaMuerte;
    }

    /**
     * Establece la causa de la muerte.
     *
     * @param causaMuerte la causa de la muerte
     */
    public void setCausaMuerte(String causaMuerte) {
        this.causaMuerte = causaMuerte;
    }

    /**
     * Devuelve el nombre completo del cliente.
     *
     * @return El nombre completo del cliente
     */
    public String getClienteNombre() {
        return clienteNombre;
    }

    /**
     * Establece el nombre completo del cliente.
     *
     * @param clienteNombre el nombre completo del cliente
     */
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    /**
     * Devuelve la relación del cliente con el difunto.
     *
     * @return La relación del cliente con el difunto
     */
    public String getClienteRelacion() {
        return clienteRelacion;
    }

    /**
     * Establece la relación del cliente con el difunto.
     *
     * @param clienteRelacion la relación del cliente con el difunto
     */
    public void setClienteRelacion(String clienteRelacion) {
        this.clienteRelacion = clienteRelacion;
    }

    /**
     * Devuelve el teléfono del cliente.
     *
     * @return El teléfono del cliente
     */
    public String getClienteTelefono() {
        return clienteTelefono;
    }

    /**
     * Establece el teléfono del cliente.
     *
     * @param clienteTelefono el teléfono del cliente
     */
    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    /**
     * Devuelve el correo electrónico del cliente.
     *
     * @return El correo electrónico del cliente
     */
    public String getClienteCorreo() {
        return clienteCorreo;
    }

    /**
     * Establece el correo electrónico del cliente.
     *
     * @param clienteCorreo el correo electrónico del cliente
     */
    public void setClienteCorreo(String clienteCorreo) {
        this.clienteCorreo = clienteCorreo;
    }

    /**
     * Devuelve el tipo de servicio contratado.
     *
     * @return El tipo de servicio contratado
     */
    public String getTipoServicio() {
        return tipoServicio;
    }

    /**
     * Establece el tipo de servicio contratado.
     *
     * @param tipoServicio el tipo de servicio contratado
     */
    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    /**
     * Devuelve la descripción del féretro contratado.
     *
     * @return La descripción del féretro contratado
     */
    public String getFeretroInfo() {
        return feretroInfo;
    }

    /**
     * Establece la descripción del féretro contratado.
     *
     * @param feretroInfo la descripción del féretro contratado
     */
    public void setFeretroInfo(String feretroInfo) {
        this.feretroInfo = feretroInfo;
    }

    /**
     * Devuelve la descripción de la urna contratada.
     *
     * @return La descripción de la urna contratada
     */
    public String getUrnaInfo() {
        return urnaInfo;
    }

    /**
     * Establece la descripción de la urna contratada.
     *
     * @param urnaInfo la descripción de la urna contratada
     */
    public void setUrnaInfo(String urnaInfo) {
        this.urnaInfo = urnaInfo;
    }

    /**
     * Devuelve la lista de servicios adicionales contratados.
     *
     * @return La lista de servicios adicionales contratados
     */
    public List<String> getListaServicios() {
        return listaServicios;
    }

    /**
     * Establece la lista de servicios adicionales contratados.
     *
     * @param listaServicios la lista de servicios adicionales contratados
     */
    public void setListaServicios(List<String> listaServicios) {
        this.listaServicios = listaServicios;
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
        return ultimaActualizacion;
    }

    /**
     * Establece la fecha de la última actualización del seguimiento.
     *
     * @param ultimaActualizacion la fecha de la última actualización del seguimiento
     */
    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
}
