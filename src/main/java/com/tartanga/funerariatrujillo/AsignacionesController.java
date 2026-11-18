package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO;
import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO.ResumenAsignacion;
import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO.TanatopractorResumen;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AsignacionesController implements Initializable {

    private final AsignacionesDAO asignacionDAO;
    private MainController mainController;
    private Consumer<String> onAbrirFicha;

    private final SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat sdfFechaHora = new SimpleDateFormat("dd/MM/yyyy · HH:mm");

    @FXML
    private StackPane rootAsignaciones;
    @FXML
    private TextField txtBuscar;

    @FXML
    private Label lblBadgePendientes;
    @FXML
    private TableView<ResumenAsignacion> tablaPendientes;
    @FXML
    private TableColumn<ResumenAsignacion, String> colPendDifunto;
    @FXML
    private TableColumn<ResumenAsignacion, String> colPendServicio;
    @FXML
    private TableColumn<ResumenAsignacion, String> colPendTrabajo;
    @FXML
    private TableColumn<ResumenAsignacion, Date> colPendLlegada;
    @FXML
    private Label lblEstadoPendientes;

    @FXML
    private Label lblTituloAsignados;
    @FXML
    private Button btnAbrirFicha;
    @FXML
    private TableView<ResumenAsignacion> tablaAsignados;
    @FXML
    private TableColumn<ResumenAsignacion, String> colAsigDifunto;
    @FXML
    private TableColumn<ResumenAsignacion, String> colAsigServicio;
    @FXML
    private TableColumn<ResumenAsignacion, String> colAsigResponsable;
    @FXML
    private TableColumn<ResumenAsignacion, String> colAsigEstado;
    @FXML
    private Label lblEstadoAsignados;

    @FXML
    private Label lblServicioSeleccionado;
    @FXML
    private Label lblPanelDifunto;
    @FXML
    private Label lblPanelCliente;
    @FXML
    private Label lblPanelTrabajo;
    @FXML
    private ComboBox<TanatopractorResumen> cmbTanatopractor;
    @FXML
    private Button btnConfirmarAsignacion;

    @FXML
    private VBox vboxCargaTrabajo;

    private final ObservableList<ResumenAsignacion> masterPendientes = FXCollections.observableArrayList();
    private final ObservableList<ResumenAsignacion> masterAsignados = FXCollections.observableArrayList();
    private final ObservableList<TanatopractorResumen> tanatopractores = FXCollections.observableArrayList();

    private FilteredList<ResumenAsignacion> filteredPendientes;
    private FilteredList<ResumenAsignacion> filteredAsignados;

    private ResumenAsignacion pendienteSeleccionado;
    private ResumenAsignacion asignadoSeleccionado;

    public AsignacionesController() {
        this.asignacionDAO = new AsignacionesDAO();
    }

    public AsignacionesController(AsignacionesDAO asignacionDAO) {
        this.asignacionDAO = asignacionDAO;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setOnAbrirFicha(Consumer<String> onAbrirFicha) {
        this.onAbrirFicha = onAbrirFicha;
    }

    private static class DatosIniciales {

        final List<ResumenAsignacion> pendientes;
        final List<ResumenAsignacion> asignados;
        final List<TanatopractorResumen> tanatopractores;

        DatosIniciales(List<ResumenAsignacion> pendientes,
                List<ResumenAsignacion> asignados,
                List<TanatopractorResumen> tanatopractores) {
            this.pendientes = pendientes;
            this.asignados = asignados;
            this.tanatopractores = tanatopractores;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablas();
        configurarFiltros();
        configurarEventos();
        cargarDatos();
    }

    private void configurarTablas() {
        colPendDifunto.setReorderable(false);
        colPendServicio.setReorderable(false);
        colPendTrabajo.setReorderable(false);
        colPendLlegada.setReorderable(false);

        colPendDifunto.setCellValueFactory(new PropertyValueFactory<>("difuntoNombre"));
        colPendServicio.setCellValueFactory(new PropertyValueFactory<>("codigoServicio"));
        colPendTrabajo.setCellValueFactory(new PropertyValueFactory<>("trabajoSolicitado"));
        colPendLlegada.setCellValueFactory(new PropertyValueFactory<>("fechaLlegada"));

        colPendLlegada.setCellFactory(new Callback<TableColumn<ResumenAsignacion, Date>, TableCell<ResumenAsignacion, Date>>() {
            @Override
            public TableCell<ResumenAsignacion, Date> call(TableColumn<ResumenAsignacion, Date> param) {
                return new TableCell<ResumenAsignacion, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatearFechaLlegada(item));
                        }
                    }
                };
            }
        });

        colAsigDifunto.setReorderable(false);
        colAsigServicio.setReorderable(false);
        colAsigResponsable.setReorderable(false);
        colAsigEstado.setReorderable(false);

        colAsigDifunto.setCellValueFactory(new PropertyValueFactory<>("difuntoNombre"));
        colAsigServicio.setCellValueFactory(new PropertyValueFactory<>("codigoServicio"));
        colAsigResponsable.setCellValueFactory(new PropertyValueFactory<>("responsableDetalle"));
        colAsigEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colAsigEstado.setCellFactory(new Callback<TableColumn<ResumenAsignacion, String>, TableCell<ResumenAsignacion, String>>() {
            @Override
            public TableCell<ResumenAsignacion, String> call(TableColumn<ResumenAsignacion, String> param) {
                return new TableCell<ResumenAsignacion, String>() {
                    @Override
                    protected void updateItem(String estado, boolean empty) {
                        super.updateItem(estado, empty);
                        if (empty || estado == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Label badge = new Label("● " + estado);
                            badge.setFont(Font.font("Inter 18pt Bold", 11.0));
                            if ("Completado".equalsIgnoreCase(estado)) {
                                badge.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; -fx-padding: 3 8 3 8; -fx-background-radius: 10;");
                            } else if ("En curso".equalsIgnoreCase(estado)) {
                                badge.setStyle("-fx-background-color: #FDF4E5; -fx-text-fill: #C68E17; -fx-padding: 3 8 3 8; -fx-background-radius: 10;");
                            } else {
                                badge.setStyle("-fx-background-color: #EDE3D0; -fx-text-fill: #7A5230; -fx-padding: 3 8 3 8; -fx-background-radius: 10;");
                            }
                            setText(null);
                            setGraphic(badge);
                        }
                    }
                };
            }
        });

        tablaPendientes.setPlaceholder(new Label("Cargando servicios pendientes..."));
        tablaAsignados.setPlaceholder(new Label("Cargando trabajo asignado..."));
    }

    private void configurarFiltros() {
        filteredPendientes = new FilteredList<>(masterPendientes, item -> true);
        filteredAsignados = new FilteredList<>(masterAsignados, item -> true);

        tablaPendientes.setItems(filteredPendientes);
        tablaAsignados.setItems(filteredAsignados);

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                aplicarFiltroBusqueda(newValue);
            }
        });
    }

    private void configurarEventos() {
        tablaPendientes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ResumenAsignacion>() {
            @Override
            public void changed(ObservableValue<? extends ResumenAsignacion> observable,
                    ResumenAsignacion oldValue,
                    ResumenAsignacion newValue) {
                pendienteSeleccionado = newValue;
                mostrarPendienteEnPanel(newValue);
            }
        });

        tablaAsignados.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ResumenAsignacion>() {
            @Override
            public void changed(ObservableValue<? extends ResumenAsignacion> observable,
                    ResumenAsignacion oldValue,
                    ResumenAsignacion newValue) {
                asignadoSeleccionado = newValue;
            }
        });

        tablaAsignados.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && asignadoSeleccionado != null) {
                    abrirFichaSeleccionada();
                }
            }
        });

        cmbTanatopractor.valueProperty().addListener(new ChangeListener<TanatopractorResumen>() {
            @Override
            public void changed(ObservableValue<? extends TanatopractorResumen> observable,
                    TanatopractorResumen oldValue,
                    TanatopractorResumen newValue) {
                if (newValue != null) {
                    cmbTanatopractor.getStyleClass().remove("error");
                }
            }
        });

        btnConfirmarAsignacion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                confirmarAsignacion();
            }
        });

        btnAbrirFicha.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                abrirFichaSeleccionada();
            }
        });
    }

    private void cargarDatos() {
        Task<DatosIniciales> taskCarga = new Task<DatosIniciales>() {
            @Override
            protected DatosIniciales call() {
                List<ResumenAsignacion> pendientes = asignacionDAO.obtenerPendientesDeAsignar();
                List<ResumenAsignacion> asignados = asignacionDAO.obtenerTrabajoAsignado();
                List<TanatopractorResumen> listaTanato = asignacionDAO.obtenerTanatopractores();
                return new DatosIniciales(
                        pendientes != null ? pendientes : new ArrayList<>(),
                        asignados != null ? asignados : new ArrayList<>(),
                        listaTanato != null ? listaTanato : new ArrayList<>()
                );
            }
        };

        taskCarga.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                DatosIniciales datos = taskCarga.getValue();
                masterPendientes.setAll(datos.pendientes);
                masterAsignados.setAll(datos.asignados);
                tanatopractores.setAll(datos.tanatopractores);
                cmbTanatopractor.setItems(tanatopractores);

                tablaPendientes.setPlaceholder(new Label("No hay servicios pendientes de asignar"));
                tablaAsignados.setPlaceholder(new Label("No hay trabajos asignados"));

                actualizarContadores();
                actualizarCargaTrabajo();

                if (!filteredPendientes.isEmpty()) {
                    tablaPendientes.getSelectionModel().selectFirst();
                } else {
                    mostrarPendienteEnPanel(null);
                }
            }
        });

        taskCarga.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                tablaPendientes.setPlaceholder(new Label("Error al cargar servicios"));
                tablaAsignados.setPlaceholder(new Label("Error al cargar asignaciones"));
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                        "No se han podido cargar las asignaciones desde la base de datos.");
            }
        });

        Thread hilo = new Thread(taskCarga);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void aplicarFiltroBusqueda(String texto) {
        final String filtro = texto == null ? "" : texto.trim().toLowerCase();

        Predicate<ResumenAsignacion> predicado = new Predicate<ResumenAsignacion>() {
            @Override
            public boolean test(ResumenAsignacion item) {
                if (filtro.isEmpty()) {
                    return true;
                }
                String difunto = textoSeguro(item.getDifuntoNombre()).toLowerCase();
                String codigo = textoSeguro(item.getCodigoServicio()).toLowerCase();
                String trabajo = textoSeguro(item.getTrabajoSolicitado()).toLowerCase();
                String responsable = textoSeguro(item.getResponsableDetalle()).toLowerCase();
                return difunto.contains(filtro)
                        || codigo.contains(filtro)
                        || trabajo.contains(filtro)
                        || responsable.contains(filtro);
            }
        };

        filteredPendientes.setPredicate(predicado);
        filteredAsignados.setPredicate(predicado);

        if (filteredPendientes.isEmpty()) {
            tablaPendientes.setPlaceholder(new Label(filtro.isEmpty()
                    ? "No hay servicios pendientes de asignar"
                    : "No se encontraron servicios pendientes"));
        }
        if (filteredAsignados.isEmpty()) {
            tablaAsignados.setPlaceholder(new Label(filtro.isEmpty()
                    ? "No hay trabajos asignados"
                    : "No se encontraron trabajos asignados"));
        }

        actualizarContadores();
    }

    private void mostrarPendienteEnPanel(ResumenAsignacion item) {
        if (item == null) {
            lblServicioSeleccionado.setText("Ningún servicio seleccionado");
            lblPanelDifunto.setText("-");
            lblPanelCliente.setText("-");
            lblPanelTrabajo.setText("-");
            lblEstadoPendientes.setText("Ningún servicio pendiente seleccionado");
        } else {
            lblServicioSeleccionado.setText("Servicio seleccionado · " + item.getCodigoServicio());
            lblPanelDifunto.setText(textoSeguro(item.getDifuntoNombre()));
            lblPanelCliente.setText(textoSeguro(item.getClienteInfo()));
            lblPanelTrabajo.setText(textoSeguro(item.getTrabajoSolicitado()));
            lblEstadoPendientes.setText("Seleccionado: " + item.getCodigoServicio() + " · Sin responsable");
        }
    }

    private void confirmarAsignacion() {
        cmbTanatopractor.getStyleClass().remove("error");

        if (pendienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Servicio requerido",
                    "Selecciona primero un servicio pendiente de asignar.");
            return;
        }

        TanatopractorResumen tanatopractor = cmbTanatopractor.getValue();
        if (tanatopractor == null) {
            if (!cmbTanatopractor.getStyleClass().contains("error")) {
                cmbTanatopractor.getStyleClass().add("error");
            }
            mostrarAlerta(Alert.AlertType.WARNING, "Tanatopractor requerido",
                    "Debes seleccionar un tanatopractor responsable antes de confirmar.");
            return;
        }

        ResumenAsignacion servicioAAsignar = pendienteSeleccionado;
        if (asignacionDAO.confirmarAsignacion(servicioAAsignar, tanatopractor)) {
            masterPendientes.remove(servicioAAsignar);
            masterAsignados.add(0, servicioAAsignar);

            cmbTanatopractor.setValue(null);
            actualizarContadores();
            actualizarCargaTrabajo();

            if (!filteredPendientes.isEmpty()) {
                tablaPendientes.getSelectionModel().selectFirst();
            } else {
                tablaPendientes.getSelectionModel().clearSelection();
                mostrarPendienteEnPanel(null);
            }
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar la asignación en la base de datos.");
        }
    }

    private void abrirFichaSeleccionada() {
        if (asignadoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Selecciona un trabajo asignado para abrir su ficha del servicio.");
            return;
        }

        String idAsignacion = asignadoSeleccionado.getId();
        if (onAbrirFicha != null) {
            onAbrirFicha.accept(idAsignacion);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/ficha_servicio.fxml"));
            Parent vistaFicha = loader.load();
            FichaServicioController fichaController = loader.getController();
            fichaController.setMainController(mainController);
            fichaController.cargarServicioDesdeBD(idAsignacion);

            if (rootAsignaciones != null && rootAsignaciones.getParent() instanceof StackPane) {
                StackPane contenedor = (StackPane) rootAsignaciones.getParent();
                fichaController.setOnVolver(() -> {
                    try {
                        FXMLLoader asigLoader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/asignaciones.fxml"));
                        Parent vistaAsig = asigLoader.load();
                        AsignacionesController asigCtrl = asigLoader.getController();
                        asigCtrl.setMainController(mainController);
                        contenedor.getChildren().setAll(vistaAsig);
                    } catch (IOException ex) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación",
                                "No se pudo volver a asignaciones: " + ex.getMessage());
                    }
                });
                contenedor.getChildren().setAll(vistaFicha);
            } else if (rootAsignaciones != null && rootAsignaciones.getScene() != null) {
                rootAsignaciones.getScene().setRoot(vistaFicha);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación",
                    "No se pudo abrir la ficha del servicio: " + e.getMessage());
        }
    }

    private void actualizarContadores() {
        lblBadgePendientes.setText("● Pendiente · " + filteredPendientes.size());
        lblTituloAsignados.setText("Trabajo asignado · " + filteredAsignados.size());
    }

    private void actualizarCargaTrabajo() {
        vboxCargaTrabajo.getChildren().clear();

        if (tanatopractores.isEmpty()) {
            Label vacio = new Label("Sin tanatopractores registrados");
            vacio.setFont(Font.font("Inter 18pt Regular", 12.0));
            vacio.setStyle("-fx-text-fill: #7a5230;");
            vboxCargaTrabajo.getChildren().add(vacio);
            return;
        }

        for (TanatopractorResumen tanato : tanatopractores) {
            int enCurso = 0;
            int pendientes = 0;

            for (ResumenAsignacion asig : masterAsignados) {
                if (tanato.getId() != null && tanato.getId().equals(asig.getIdTrabajador())) {
                    if ("En curso".equalsIgnoreCase(asig.getEstado())) {
                        enCurso++;
                    } else if (!"Completado".equalsIgnoreCase(asig.getEstado())) {
                        pendientes++;
                    }
                }
            }

            HBox fila = new HBox(10.0);
            fila.setAlignment(Pos.CENTER_LEFT);

            Label lblNombre = new Label(tanato.getNombreCompleto());
            lblNombre.setFont(Font.font("Inter 18pt Bold", 13.0));
            lblNombre.setStyle("-fx-text-fill: #2e1a0e;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblResumen = new Label(construirTextoCarga(enCurso, pendientes));
            lblResumen.setFont(Font.font("Inter 18pt Regular", 12.0));
            lblResumen.setStyle("-fx-text-fill: #7a5230;");

            fila.getChildren().addAll(lblNombre, spacer, lblResumen);
            vboxCargaTrabajo.getChildren().add(fila);
        }
    }

    private String construirTextoCarga(int enCurso, int pendientes) {
        if (enCurso == 0 && pendientes == 0) {
            return "Disponible";
        }
        List<String> partes = new ArrayList<>();
        if (enCurso > 0) {
            partes.add(enCurso + " en curso");
        }
        if (pendientes > 0) {
            partes.add(pendientes + (pendientes == 1 ? " asignado" : " asignados"));
        }
        return String.join(" · ", partes);
    }

    private String formatearFechaLlegada(Date fecha) {
        LocalDate fechaLocal = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (fechaLocal.equals(LocalDate.now())) {
            return "Hoy · " + sdfHora.format(fecha);
        }
        return sdfFechaHora.format(fecha);
    }

    private String textoSeguro(String texto) {
        return texto != null ? texto : "";
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
