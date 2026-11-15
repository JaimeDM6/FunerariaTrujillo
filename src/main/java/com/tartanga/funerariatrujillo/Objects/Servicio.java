package com.tartanga.funerariatrujillo.Objects;

import java.util.Date;
import java.util.List;

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

    public Servicio() {
    }

    public String getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(String idAsignacion) {
        this.idAsignacion = idAsignacion;
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

    public String getTituloServicio() {
        return tituloServicio;
    }

    public void setTituloServicio(String tituloServicio) {
        this.tituloServicio = tituloServicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Date getFechaInicioTrabajo() {
        return fechaInicioTrabajo;
    }

    public void setFechaInicioTrabajo(Date fechaInicioTrabajo) {
        this.fechaInicioTrabajo = fechaInicioTrabajo;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public String getTrabajadorNombre() {
        return trabajadorNombre;
    }

    public void setTrabajadorNombre(String trabajadorNombre) {
        this.trabajadorNombre = trabajadorNombre;
    }

    public String getTrabajadorCargo() {
        return trabajadorCargo;
    }

    public void setTrabajadorCargo(String trabajadorCargo) {
        this.trabajadorCargo = trabajadorCargo;
    }

    public String getDifuntoNombre() {
        return difuntoNombre;
    }

    public void setDifuntoNombre(String difuntoNombre) {
        this.difuntoNombre = difuntoNombre;
    }

    public Date getDifuntoNacimiento() {
        return difuntoNacimiento;
    }

    public void setDifuntoNacimiento(Date difuntoNacimiento) {
        this.difuntoNacimiento = difuntoNacimiento;
    }

    public Date getDifuntoFallecimiento() {
        return difuntoFallecimiento;
    }

    public void setDifuntoFallecimiento(Date difuntoFallecimiento) {
        this.difuntoFallecimiento = difuntoFallecimiento;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getCausaMuerte() {
        return causaMuerte;
    }

    public void setCausaMuerte(String causaMuerte) {
        this.causaMuerte = causaMuerte;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteRelacion() {
        return clienteRelacion;
    }

    public void setClienteRelacion(String clienteRelacion) {
        this.clienteRelacion = clienteRelacion;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    public String getClienteCorreo() {
        return clienteCorreo;
    }

    public void setClienteCorreo(String clienteCorreo) {
        this.clienteCorreo = clienteCorreo;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getFeretroInfo() {
        return feretroInfo;
    }

    public void setFeretroInfo(String feretroInfo) {
        this.feretroInfo = feretroInfo;
    }

    public String getUrnaInfo() {
        return urnaInfo;
    }

    public void setUrnaInfo(String urnaInfo) {
        this.urnaInfo = urnaInfo;
    }

    public List<String> getListaServicios() {
        return listaServicios;
    }

    public void setListaServicios(List<String> listaServicios) {
        this.listaServicios = listaServicios;
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
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
}
