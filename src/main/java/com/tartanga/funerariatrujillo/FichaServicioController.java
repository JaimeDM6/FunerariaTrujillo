package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.FichaServicioDAO;
import com.tartanga.funerariatrujillo.Objects.Servicio;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class FichaServicioController implements Initializable {

    private final FichaServicioDAO servicioDAO;
    private Servicio servicioActual;

    private final SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy · HH:mm");
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    private Label lblTituloFicha, lblHeaderServicio, lblHeaderFecha, lblHeaderTanatopractor, lblHeaderEstado;
    @FXML
    private Label lblDifuntoNombre, lblDifuntoNacimiento, lblDifuntoFallecimiento, lblDifuntoProcedencia, lblDifuntoCausa;
    @FXML
    private Label lblClienteNombre, lblClienteRelacion, lblClienteTelefono, lblClienteCorreo;
    @FXML
    private Label lblResumen1, lblResumen2, lblResumen3;
    @FXML
    private Label lblIndicaciones, lblFechaAsignado, lblFechaInicio;

    @FXML
    private Label lblPanelTrabajador;
    @FXML
    private ComboBox<String> cmbEstadoServicio;
    @FXML
    private CheckBox chkPaso1, chkPaso2, chkPaso3;
    @FXML
    private TextField txtObservaciones;
    @FXML
    private Label lblUltimaActualizacion;

    @FXML
    private Button btnVolver, btnGuardarCambios;

    public FichaServicioController() {
        this.servicioDAO = new FichaServicioDAO();
    }

    public FichaServicioController(FichaServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbEstadoServicio.setItems(FXCollections.observableArrayList("Pendiente", "En curso", "Completado"));

        btnVolver.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Navegando de vuelta a Asignaciones...");
            }
        });

        btnGuardarCambios.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarSeguimiento();
            }
        });
    }

    public void cargarServicioDesdeBD(String idAsignacion) {
        Servicio s = servicioDAO.obtenerServicioPorId(idAsignacion);
        if (s != null) {
            this.servicioActual = s;
            cargarDatosUI();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se encontró el servicio solicitado.");
            alert.showAndWait();
        }
    }

    private void cargarDatosUI() {
        if (servicioActual == null) {
            return;
        }

        lblTituloFicha.setText("Ficha del servicio · " + (servicioActual.getTituloServicio() != null ? servicioActual.getTituloServicio() : ""));
        lblHeaderServicio.setText("Servicio de " + (servicioActual.getTipoServicio() != null ? servicioActual.getTipoServicio().toLowerCase() : "general"));
        lblHeaderFecha.setText(servicioActual.getFechaAsignacion() != null ? "Asignación: " + sdfDateTime.format(servicioActual.getFechaAsignacion()) : "-");
        lblHeaderTanatopractor.setText(servicioActual.getTrabajadorNombre() + " · " + servicioActual.getTrabajadorCargo());

        lblHeaderEstado.setText("● " + servicioActual.getEstado());
        if ("Completado".equals(servicioActual.getEstado())) {
            lblHeaderEstado.setStyle("-fx-background-color: #E8F5E9; -fx-padding: 5 10 5 10; -fx-background-radius: 4; -fx-text-fill: #2E7D32;");
        } else if ("Pendiente".equals(servicioActual.getEstado())) {
            lblHeaderEstado.setStyle("-fx-background-color: #FFEBEE; -fx-padding: 5 10 5 10; -fx-background-radius: 4; -fx-text-fill: #C62828;");
        } else {
            lblHeaderEstado.setStyle("-fx-background-color: #FDF4E5; -fx-padding: 5 10 5 10; -fx-background-radius: 4; -fx-text-fill: #C68E17;");
        }

        // Difunto
        lblDifuntoNombre.setText(servicioActual.getDifuntoNombre() != null ? servicioActual.getDifuntoNombre() : "-");

        if (servicioActual.getDifuntoNacimiento() != null && servicioActual.getDifuntoFallecimiento() != null) {
            long diffMillis = Math.abs(servicioActual.getDifuntoFallecimiento().getTime() - servicioActual.getDifuntoNacimiento().getTime());
            long diffAge = diffMillis / (1000L * 60 * 60 * 24 * 365);
            lblDifuntoNacimiento.setText(sdfDate.format(servicioActual.getDifuntoNacimiento()) + " · " + diffAge + " años");
        } else {
            lblDifuntoNacimiento.setText("-");
        }

        lblDifuntoFallecimiento.setText(servicioActual.getDifuntoFallecimiento() != null ? sdfDateTime.format(servicioActual.getDifuntoFallecimiento()) : "-");
        lblDifuntoProcedencia.setText(servicioActual.getProcedencia() != null ? servicioActual.getProcedencia() : "-");
        lblDifuntoCausa.setText("Causa de muerte: " + (servicioActual.getCausaMuerte() != null ? servicioActual.getCausaMuerte() : "-"));

        // Cliente
        lblClienteNombre.setText(servicioActual.getClienteNombre() != null ? servicioActual.getClienteNombre() : "-");
        lblClienteRelacion.setText(servicioActual.getClienteRelacion() != null ? servicioActual.getClienteRelacion() : "-");
        lblClienteTelefono.setText(servicioActual.getClienteTelefono() != null ? servicioActual.getClienteTelefono() : "-");
        lblClienteCorreo.setText(servicioActual.getClienteCorreo() != null ? servicioActual.getClienteCorreo() : "-");

        // Servicios Contratados
        String difIdStr = servicioActual.getIdDifunto() != null ? "DIF-" + servicioActual.getIdDifunto().substring(servicioActual.getIdDifunto().length() - 4).toUpperCase() : "-";
        lblResumen1.setText(difIdStr + " · Servicio: " + (servicioActual.getTipoServicio() != null ? servicioActual.getTipoServicio() : "-"));
        lblResumen2.setText("Féretro: " + servicioActual.getFeretroInfo() + " · Urna: " + servicioActual.getUrnaInfo());

        String listaStr = servicioActual.getListaServicios() != null ? String.join(", ", servicioActual.getListaServicios()) : "-";
        lblResumen3.setText("Servicios extra: " + listaStr);

        lblIndicaciones.setText(servicioActual.getIndicacionesFamilia() != null ? servicioActual.getIndicacionesFamilia() : "-");
        lblFechaAsignado.setText(servicioActual.getFechaAsignacion() != null ? sdfDateTime.format(servicioActual.getFechaAsignacion()) : "-");
        lblFechaInicio.setText(servicioActual.getFechaInicioTrabajo() != null ? sdfDateTime.format(servicioActual.getFechaInicioTrabajo()) : "-");

        lblPanelTrabajador.setText(servicioActual.getTrabajadorNombre() + " · " + servicioActual.getTrabajadorCargo());
        cmbEstadoServicio.setValue(servicioActual.getEstado());
        chkPaso1.setSelected(servicioActual.isPasoIdentificacion());
        chkPaso2.setSelected(servicioActual.isPasoConservacion());
        chkPaso3.setSelected(servicioActual.isPasoEstetica());
        txtObservaciones.setText(servicioActual.getObservaciones() != null ? servicioActual.getObservaciones() : "");
        lblUltimaActualizacion.setText(servicioActual.getUltimaActualizacion() != null ? "Última actualización: " + sdfDateTime.format(servicioActual.getUltimaActualizacion()) : "Sin actualizaciones previas");
    }

    private void guardarSeguimiento() {
        if (servicioActual != null) {
            String nuevoEstado = cmbEstadoServicio.getValue();

            if ("En curso".equals(nuevoEstado) && servicioActual.getFechaInicioTrabajo() == null) {
                servicioActual.setFechaInicioTrabajo(new Date());
            }
            if ("Completado".equals(nuevoEstado) && !"Completado".equals(servicioActual.getEstado())) {
                servicioActual.setFechaFinalizacion(new Date());
            }

            servicioActual.setEstado(nuevoEstado);
            servicioActual.setPasoIdentificacion(chkPaso1.isSelected());
            servicioActual.setPasoConservacion(chkPaso2.isSelected());
            servicioActual.setPasoEstetica(chkPaso3.isSelected());
            servicioActual.setObservaciones(txtObservaciones.getText());
            servicioActual.setUltimaActualizacion(new Date());

            if (servicioDAO.actualizarSeguimientoServicio(servicioActual)) {
                cargarDatosUI();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("El seguimiento del servicio ha sido actualizado.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error al guardar los cambios en la base de datos.");
                alert.showAndWait();
            }
        }
    }
}
