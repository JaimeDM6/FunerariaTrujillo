package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO;
import com.tartanga.funerariatrujillo.Objects.Asignacion;
import com.tartanga.funerariatrujillo.Utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class AsignacionesController implements Initializable {

    private final AsignacionesDAO asignacionesDAO;

    public AsignacionesController() {
        this.asignacionesDAO = new AsignacionesDAO();
    }

    public AsignacionesController(AsignacionesDAO asignacionesDAO) {
        this.asignacionesDAO = asignacionesDAO;
    }

    @FXML
    private VBox vistaTabla;
    @FXML
    private VBox vistaDetalles;
    @FXML
    private VBox panelFormularioNuevo;
    @FXML
    private Region spacerLateral;
    @FXML
    private TableView<Asignacion> tablaAsignaciones;

    @FXML
    private Button btnNuevaAsignacion;
    @FXML
    private Button btnCerrarFormulario;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnGuardarEdicion;
    @FXML
    private Button btnGuardarNuevo;
    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblEstadoTabla;

    @FXML
    private TableColumn<Asignacion, String> colID;
    @FXML
    private TableColumn<Asignacion, String> colDifunto;
    @FXML
    private TableColumn<Asignacion, String> colTrabajador;
    @FXML
    private TableColumn<Asignacion, String> colEstado;
    @FXML
    private TableColumn<Asignacion, Date> colFechaAsignacion;

    @FXML
    private ComboBox<String> cmbNuevoDifunto;
    @FXML
    private ComboBox<String> cmbNuevoTrabajador;
    @FXML
    private TextArea txtNuevasIndicaciones;

    @FXML
    private ComboBox<String> cmbDetalleDifunto;
    @FXML
    private ComboBox<String> cmbDetalleTrabajador;
    @FXML
    private ComboBox<String> cmbDetalleEstado;
    @FXML
    private Label lblDetalleFechas;
    @FXML
    private TextArea txtDetalleIndicaciones;
    @FXML
    private CheckBox chkDetalleIdentificacion;
    @FXML
    private CheckBox chkDetalleConservacion;
    @FXML
    private CheckBox chkDetalleEstetica;
    @FXML
    private TextArea txtDetalleObservaciones;

    private final ObservableList<Asignacion> masterData = FXCollections.observableArrayList();
    private final Map<String, String> idsReferencias = new HashMap<String, String>();
    private final Map<String, String> textosPorId = new HashMap<String, String>();
    private FilteredList<Asignacion> filteredData;
    private Asignacion asignacionSeleccionada;
    private boolean gestionar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gestionar = asignacionesDAO.puedeGestionar();

        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);

        btnNuevaAsignacion.setVisible(gestionar);
        btnNuevaAsignacion.setManaged(gestionar);

        btnNuevaAsignacion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openForm();
            }
        });

        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevaAsignacion();
            }
        });

        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarAsignacionSeleccionada();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        filteredData = new FilteredList<>(masterData, new Predicate<Asignacion>() {
            @Override
            public boolean test(Asignacion asignacion) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Asignacion>() {
                    @Override
                    public boolean test(Asignacion asignacion) {
                        boolean coincide = true;

                        if (newValue != null && !newValue.trim().isEmpty()) {
                            String filtro = newValue.trim().toLowerCase();

                            coincide = textoSinNulos(asignacion.getId()).toLowerCase().contains(filtro)
                                    || codigoCorto(asignacion.getId()).toLowerCase().contains(filtro)
                                    || textoSinNulos(asignacion.getDifuntoNombre()).toLowerCase().contains(filtro)
                                    || textoSinNulos(asignacion.getTrabajadorNombre()).toLowerCase().contains(filtro)
                                    || textoSinNulos(asignacion.getEstado()).toLowerCase().contains(filtro);
                        }

                        return coincide;
                    }
                });
                actualizarEstadoBusqueda(newValue);
            }
        });

        SortedList<Asignacion> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaAsignaciones.comparatorProperty());
        tablaAsignaciones.setItems(sortedData);

        colFechaAsignacion.setSortType(TableColumn.SortType.DESCENDING);
        tablaAsignaciones.getSortOrder().add(colFechaAsignacion);

        colID.setReorderable(false);
        colDifunto.setReorderable(false);
        colTrabajador.setReorderable(false);
        colEstado.setReorderable(false);
        colFechaAsignacion.setReorderable(false);

        colID.setCellValueFactory(new PropertyValueFactory<Asignacion, String>("id"));
        colDifunto.setCellValueFactory(new PropertyValueFactory<Asignacion, String>("difuntoNombre"));
        colTrabajador.setCellValueFactory(new PropertyValueFactory<Asignacion, String>("trabajadorNombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Asignacion, String>("estado"));
        colFechaAsignacion.setCellValueFactory(new PropertyValueFactory<Asignacion, Date>("fechaAsignacion"));

        Utils.columnID(colID);
        Utils.columnDate(colFechaAsignacion);

        tablaAsignaciones.setPlaceholder(new Label("Cargando asignaciones..."));
        lblEstadoTabla.setText("Cargando asignaciones...");

        cmbDetalleEstado.getItems().setAll("Pendiente", "En curso", "Completado");

        cargarReferencias();
        cargarDatos();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
        bloquearCamposEdicion();
    }

    private void cargarReferencias() {
        idsReferencias.clear();
        textosPorId.clear();

        List<String> difuntos = registrarReferencias(asignacionesDAO.obtenerDifuntos());
        List<String> trabajadores = registrarReferencias(asignacionesDAO.obtenerTrabajadores());

        cmbNuevoDifunto.getItems().setAll(difuntos);
        cmbNuevoTrabajador.getItems().setAll(trabajadores);
        cmbDetalleDifunto.getItems().setAll(difuntos);
        cmbDetalleTrabajador.getItems().setAll(trabajadores);
    }

    private List<String> registrarReferencias(List<String> referencias) {
        List<String> textos = new ArrayList<>();

        for (String referencia : referencias) {
            int separador = referencia.indexOf(" - ");

            if (separador > 0) {
                String id = referencia.substring(0, separador);
                String nombre = referencia.substring(separador + 3);
                String texto = crearTextoReferencia(id, nombre);

                idsReferencias.put(texto, id);
                textosPorId.put(id, texto);
                textos.add(texto);
            }
        }

        return textos;
    }

    private String crearTextoReferencia(String id, String nombre) {
        return nombre + " (" + codigoCorto(id) + ")";
    }

    private String obtenerTextoReferencia(String id, String nombre) {
        String texto = textosPorId.get(id);

        if (texto == null) {
            texto = crearTextoReferencia(id, nombre);
        }

        return texto;
    }

    private String obtenerNombreDeReferencia(String texto) {
        String nombre = textoSinNulos(texto);
        int posicion = nombre.lastIndexOf(" (");

        if (posicion > 0) {
            nombre = nombre.substring(0, posicion);
        }

        return nombre;
    }

    private String codigoCorto(String id) {
        String codigo = textoSinNulos(id);

        if (codigo.length() > 6) {
            codigo = codigo.substring(codigo.length() - 6).toUpperCase();
        }

        return codigo;
    }

    private String textoSinNulos(String texto) {
        String resultado = "";
        if (texto != null) {
            resultado = texto;
        }
        return resultado;
    }

    private void actualizarEstadoBusqueda(String textoBusqueda) {
        if (filteredData.size() > 0) {
            lblEstadoTabla.setText(filteredData.size() + " asignaciones");
            tablaAsignaciones.setPlaceholder(new Label("No hay asignaciones"));
        } else if (masterData.isEmpty()) {
            lblEstadoTabla.setText("0 asignaciones");
            tablaAsignaciones.setPlaceholder(new Label("No hay asignaciones"));
        } else if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            lblEstadoTabla.setText("0 asignaciones");
            tablaAsignaciones.setPlaceholder(new Label("No hay asignaciones"));
        } else {
            lblEstadoTabla.setText("Asignación no encontrada");
            tablaAsignaciones.setPlaceholder(new Label("Asignación no encontrada"));
        }
    }

    private void cargarDatos() {
        Task<List<Asignacion>> taskObtenerAsignaciones = new Task<List<Asignacion>>() {
            @Override
            protected List<Asignacion> call() throws Exception {
                return asignacionesDAO.obtenerAsignaciones();
            }
        };

        taskObtenerAsignaciones.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                masterData.setAll(taskObtenerAsignaciones.getValue());
                tablaAsignaciones.sort();
                actualizarEstadoBusqueda(txtBuscar.getText());
            }
        });

        taskObtenerAsignaciones.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                        "No se pudo cargar la lista de asignaciones desde la base de datos.");
            }
        });

        Thread hiloSecundario = new Thread(taskObtenerAsignaciones);
        hiloSecundario.setDaemon(true);
        hiloSecundario.start();
    }

    private void guardarNuevaAsignacion() {
        cmbNuevoDifunto.getStyleClass().remove("error");
        cmbNuevoTrabajador.getStyleClass().remove("error");

        String mensaje = validarNuevaAsignacion();

        if (mensaje == null) {
            Asignacion nueva = new Asignacion();
            nueva.setIdDifunto(idsReferencias.get(cmbNuevoDifunto.getValue()));
            nueva.setIdTrabajador(idsReferencias.get(cmbNuevoTrabajador.getValue()));
            nueva.setDifuntoNombre(obtenerNombreDeReferencia(cmbNuevoDifunto.getValue()));
            nueva.setTrabajadorNombre(obtenerNombreDeReferencia(cmbNuevoTrabajador.getValue()));
            nueva.setIndicacionesFamilia(txtNuevasIndicaciones.getText().trim());

            if (asignacionesDAO.insertarAsignacion(nueva)) {
                masterData.add(nueva);
                tablaAsignaciones.sort();
                actualizarEstadoBusqueda(txtBuscar.getText());

                limpiarFormularioNuevo();
                closeForm();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Hubo un problema al guardar la asignación en la base de datos.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
        }
    }

    private String validarNuevaAsignacion() {
        String mensaje = null;
        boolean hayError = false;

        if (cmbNuevoDifunto.getValue() == null || !idsReferencias.containsKey(cmbNuevoDifunto.getValue())) {
            cmbNuevoDifunto.getStyleClass().add("error");
            hayError = true;
        }

        if (cmbNuevoTrabajador.getValue() == null || !idsReferencias.containsKey(cmbNuevoTrabajador.getValue())) {
            cmbNuevoTrabajador.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje = "Debes seleccionar un difunto y un trabajador.";
        }

        return mensaje;
    }

    private String validarDetalleAsignacion() {
        String mensaje = null;
        boolean hayError = false;

        if (gestionar) {
            if (cmbDetalleDifunto.getValue() == null) {
                cmbDetalleDifunto.getStyleClass().add("error");
                hayError = true;
            }

            if (cmbDetalleTrabajador.getValue() == null) {
                cmbDetalleTrabajador.getStyleClass().add("error");
                hayError = true;
            }
        }

        if (cmbDetalleEstado.getValue() == null) {
            cmbDetalleEstado.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje = "No puedes dejar en blanco los campos obligatorios.";
        }

        return mensaje;
    }

    private void limpiarFormularioNuevo() {
        cmbNuevoDifunto.getStyleClass().remove("error");
        cmbNuevoTrabajador.getStyleClass().remove("error");
        cmbNuevoDifunto.setValue(null);
        cmbNuevoTrabajador.setValue(null);
        txtNuevasIndicaciones.clear();
    }

    private void eliminarAsignacionSeleccionada() {
        if (asignacionSeleccionada != null && gestionar) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Eliminar asignación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Seguro que quieres eliminar la asignación de "
                    + asignacionSeleccionada.getDifuntoNombre()
                    + "? Se eliminará también su seguimiento. El difunto y el trabajador no se verán afectados.");

            Optional<ButtonType> respuesta = confirmacion.showAndWait();

            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                String id = asignacionSeleccionada.getId();

                if (asignacionesDAO.eliminarAsignacion(id)) {
                    int posicion = buscarPosicionPorId(id);
                    if (posicion >= 0) {
                        masterData.remove(posicion);
                    }
                    actualizarEstadoBusqueda(txtBuscar.getText());

                    vistaDetalles.setVisible(false);
                    vistaDetalles.setManaged(false);
                    vistaTabla.setVisible(true);
                    vistaTabla.setManaged(true);

                    asignacionSeleccionada = null;
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "Hubo un problema al eliminar la asignación de la base de datos.");
                }
            }
        }
    }

    private int buscarPosicionPorId(String id) {
        int posicion = -1;
        int i = 0;

        while (posicion == -1 && i < masterData.size()) {
            if (id != null && id.equals(masterData.get(i).getId())) {
                posicion = i;
            }
            i++;
        }

        return posicion;
    }

    private void openForm() {
        Utils.openForm(panelFormularioNuevo, spacerLateral);
    }

    private void closeForm() {
        Utils.closeForm(panelFormularioNuevo, spacerLateral);
    }

    private String formatearFecha(Date fecha) {
        String texto = "Sin registrar";

        if (fecha != null) {
            texto = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
        }

        return texto;
    }

    private void mostrarDetalles(Asignacion asignacion) {
        asignacionSeleccionada = asignacion;

        cmbDetalleDifunto.setValue(obtenerTextoReferencia(asignacion.getIdDifunto(), asignacion.getDifuntoNombre()));
        cmbDetalleTrabajador.setValue(obtenerTextoReferencia(asignacion.getIdTrabajador(), asignacion.getTrabajadorNombre()));
        cmbDetalleEstado.setValue(asignacion.getEstado());
        txtDetalleIndicaciones.setText(textoSinNulos(asignacion.getIndicacionesFamilia()));
        chkDetalleIdentificacion.setSelected(asignacion.isPasoIdentificacion());
        chkDetalleConservacion.setSelected(asignacion.isPasoConservacion());
        chkDetalleEstetica.setSelected(asignacion.isPasoEstetica());
        txtDetalleObservaciones.setText(textoSinNulos(asignacion.getObservaciones()));
        lblDetalleFechas.setText("Fecha de asignación: " + formatearFecha(asignacion.getFechaAsignacion())
                + "\nInicio del trabajo: " + formatearFecha(asignacion.getFechaInicioTrabajo())
                + "\nFinalización: " + formatearFecha(asignacion.getFechaFinalizacion())
                + "\nÚltima actualización: " + formatearFecha(asignacion.getUltimaActualizacion()));

        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);
        vistaTabla.setVisible(false);
        vistaTabla.setManaged(false);
        vistaDetalles.setVisible(true);
        vistaDetalles.setManaged(true);
        tablaAsignaciones.getSelectionModel().clearSelection();

        bloquearCamposEdicion();
    }

    private void configurarSeleccionDeTabla() {
        tablaAsignaciones.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Asignacion newSelection = tablaAsignaciones.getSelectionModel().getSelectedItem();

                if (newSelection != null) {
                    mostrarDetalles(newSelection);
                }
            }
        });

        btnVolver.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                vistaDetalles.setVisible(false);
                vistaDetalles.setManaged(false);

                vistaTabla.setVisible(true);
                vistaTabla.setManaged(true);

                bloquearCamposEdicion();
            }
        });
    }

    private void configurarModoEdicion() {
        btnEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cambiarEstadoCamposEdicion(true);

                cmbDetalleDifunto.getStyleClass().remove("error");
                cmbDetalleTrabajador.getStyleClass().remove("error");
                cmbDetalleEstado.getStyleClass().remove("error");

                btnEditar.setVisible(false);
                btnEditar.setManaged(false);
                btnEliminar.setVisible(false);
                btnEliminar.setManaged(false);
                btnGuardarEdicion.setVisible(true);
                btnGuardarEdicion.setManaged(true);
            }
        });

        btnGuardarEdicion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarEdicion();
            }
        });
    }

    private void guardarEdicion() {
        cmbDetalleDifunto.getStyleClass().remove("error");
        cmbDetalleTrabajador.getStyleClass().remove("error");
        cmbDetalleEstado.getStyleClass().remove("error");

        String mensaje = validarDetalleAsignacion();

        if (mensaje == null) {
            if (asignacionSeleccionada != null) {
                Asignacion modificada = new Asignacion(asignacionSeleccionada);
                modificada.setEstado(cmbDetalleEstado.getValue());
                modificada.setPasoIdentificacion(chkDetalleIdentificacion.isSelected());
                modificada.setPasoConservacion(chkDetalleConservacion.isSelected());
                modificada.setPasoEstetica(chkDetalleEstetica.isSelected());
                modificada.setObservaciones(txtDetalleObservaciones.getText().trim());

                if (gestionar) {
                    aplicarReferenciasEditadas(modificada);
                    modificada.setIndicacionesFamilia(txtDetalleIndicaciones.getText().trim());
                }

                if (asignacionesDAO.actualizarAsignacion(modificada)) {
                    int posicion = buscarPosicionPorId(modificada.getId());
                    if (posicion >= 0) {
                        masterData.set(posicion, modificada);
                    }
                    tablaAsignaciones.sort();
                    actualizarEstadoBusqueda(txtBuscar.getText());
                    mostrarDetalles(modificada);
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "Hubo un error al actualizar los datos en la base de datos.");
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
        }
    }

    private void aplicarReferenciasEditadas(Asignacion modificada) {
        String idDifunto = idsReferencias.get(cmbDetalleDifunto.getValue());
        String idTrabajador = idsReferencias.get(cmbDetalleTrabajador.getValue());

        if (idDifunto != null) {
            modificada.setIdDifunto(idDifunto);
            modificada.setDifuntoNombre(obtenerNombreDeReferencia(cmbDetalleDifunto.getValue()));
        }

        if (idTrabajador != null) {
            modificada.setIdTrabajador(idTrabajador);
            modificada.setTrabajadorNombre(obtenerNombreDeReferencia(cmbDetalleTrabajador.getValue()));
        }
    }

    private void cambiarEstadoCamposEdicion(boolean editar) {
        boolean editarReferencias = editar && gestionar;

        cmbDetalleDifunto.setDisable(!editarReferencias);
        cmbDetalleTrabajador.setDisable(!editarReferencias);
        txtDetalleIndicaciones.setEditable(editarReferencias);
        cmbDetalleEstado.setDisable(!editar);
        chkDetalleIdentificacion.setDisable(!editar);
        chkDetalleConservacion.setDisable(!editar);
        chkDetalleEstetica.setDisable(!editar);
        txtDetalleObservaciones.setEditable(editar);
    }

    private void bloquearCamposEdicion() {
        cambiarEstadoCamposEdicion(false);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnEliminar.setVisible(gestionar);
        btnEliminar.setManaged(gestionar);
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}