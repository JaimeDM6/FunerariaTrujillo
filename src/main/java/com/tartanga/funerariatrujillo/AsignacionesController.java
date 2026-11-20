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
import javafx.collections.transformation.SortedList;
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

/**
 * Controlador de la vista de asignaciones. Muestra los servicios pendientes de asignar y el
 * trabajo ya asignado, permite asignar un tanatopractor a cada servicio, muestra la carga de
 * trabajo de cada tanatopractor y abre la ficha de servicio de una asignación.
 *
 * @author Aritz
 */
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

    /**
     * Crea el controlador con el acceso a datos de asignaciones por defecto.
     */
    public AsignacionesController() {
        this.asignacionDAO = new AsignacionesDAO();
    }

    /**
     * Crea el controlador con el DAO indicado. Permite sustituir el acceso a la base de datos, por
     * ejemplo en las pruebas.
     *
     * @param asignacionDAO DAO que se utilizará para acceder a la base de datos
     */
    public AsignacionesController(AsignacionesDAO asignacionDAO) {
        this.asignacionDAO = asignacionDAO;
    }

    /**
     * Establece el controlador principal, necesario para navegar a la ficha de servicio.
     *
     * @param mainController controlador principal de la aplicación
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Establece la acción que se ejecuta al abrir la ficha de una asignación. Si no se establece, la
     * ficha se abre en la propia vista.
     *
     * @param onAbrirFicha acción que recibe el identificador de la asignación
     */
    public void setOnAbrirFicha(Consumer<String> onAbrirFicha) {
        this.onAbrirFicha = onAbrirFicha;
    }

    /**
     * Agrupa los datos que se cargan en segundo plano al abrir la vista: servicios pendientes, trabajo
     * asignado y tanatopractores.
     */
    private static class DatosIniciales {

        final List<ResumenAsignacion> pendientes;
        final List<ResumenAsignacion> asignados;
        final List<TanatopractorResumen> tanatopractores;

        /**
         * Crea el conjunto de datos iniciales.
         *
         * @param pendientes servicios pendientes de asignar
         * @param asignados trabajo ya asignado
         * @param tanatopractores tanatopractores disponibles
         */
        DatosIniciales(List<ResumenAsignacion> pendientes,
                List<ResumenAsignacion> asignados,
                List<TanatopractorResumen> tanatopractores) {
            this.pendientes = pendientes;
            this.asignados = asignados;
            this.tanatopractores = tanatopractores;
        }
    }

    /**
     * Inicializa la vista: configura las tablas, el buscador y los eventos, y carga los datos de la
     * base de datos.
     *
     * @param location ubicación del documento FXML; no se utiliza
     * @param resources recursos de internacionalización; no se utilizan
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablas();
        configurarFiltros();
        configurarEventos();
        cargarDatos();
    }

    /**
     * Configura las columnas de las tablas de pendientes y de asignados, el formato de la fecha de
     * llegada y el distintivo de color del estado.
     */
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

    /**
     * Crea las listas filtradas y ordenables de las dos tablas, de modo que se puedan ordenar
     * pulsando en las cabeceras, y conecta el buscador para filtrarlas mientras se escribe.
     */
    private void configurarFiltros() {
        filteredPendientes = new FilteredList<>(masterPendientes, new Predicate<ResumenAsignacion>() {
            @Override
            public boolean test(ResumenAsignacion item) {
                return true;
            }
        });
        filteredAsignados = new FilteredList<>(masterAsignados, new Predicate<ResumenAsignacion>() {
            @Override
            public boolean test(ResumenAsignacion item) {
                return true;
            }
        });

        SortedList<ResumenAsignacion> sortedPendientes = new SortedList<>(filteredPendientes);
        sortedPendientes.comparatorProperty().bind(tablaPendientes.comparatorProperty());
        tablaPendientes.setItems(sortedPendientes);

        SortedList<ResumenAsignacion> sortedAsignados = new SortedList<>(filteredAsignados);
        sortedAsignados.comparatorProperty().bind(tablaAsignados.comparatorProperty());
        tablaAsignados.setItems(sortedAsignados);

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                aplicarFiltroBusqueda(newValue);
            }
        });
    }

    /**
     * Asigna las acciones de las tablas y de los botones: selección de filas, doble clic para abrir la
     * ficha, confirmar la asignación y abrir la ficha.
     */
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

    /**
     * Carga pendientes, asignados y tanatopractores desde la base de datos en un hilo en segundo plano
     * y actualiza las tablas al terminar. Si falla, avisa del error de conexión.
     */
    private void cargarDatos() {
        Task<DatosIniciales> taskCarga = new Task<DatosIniciales>() {
            @Override
            protected DatosIniciales call() {
                List<ResumenAsignacion> pendientes = asignacionDAO.obtenerPendientesDeAsignar();
                List<ResumenAsignacion> asignados = asignacionDAO.obtenerTrabajoAsignado();
                List<TanatopractorResumen> listaTanato = asignacionDAO.obtenerTanatopractores();
                if (pendientes == null) {
                    pendientes = new ArrayList<>();
                }
                if (asignados == null) {
                    asignados = new ArrayList<>();
                }
                if (listaTanato == null) {
                    listaTanato = new ArrayList<>();
                }
                return new DatosIniciales(pendientes, asignados, listaTanato);
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

    /**
     * Filtra las dos tablas por el texto buscado en el difunto, el código, el trabajo o el
     * responsable, y actualiza los contadores y los mensajes de las tablas vacías.
     *
     * @param texto texto escrito en el buscador, que puede ser nulo
     */
    private void aplicarFiltroBusqueda(String texto) {
        String textoFiltro = "";
        if (texto != null) {
            textoFiltro = texto.trim().toLowerCase();
        }
        final String filtro = textoFiltro;

        Predicate<ResumenAsignacion> predicado = new Predicate<ResumenAsignacion>() {
            @Override
            public boolean test(ResumenAsignacion item) {
                boolean coincide = true;

                if (!filtro.isEmpty()) {
                    String difunto = textoSeguro(item.getDifuntoNombre()).toLowerCase();
                    String codigo = textoSeguro(item.getCodigoServicio()).toLowerCase();
                    String trabajo = textoSeguro(item.getTrabajoSolicitado()).toLowerCase();
                    String responsable = textoSeguro(item.getResponsableDetalle()).toLowerCase();
                    coincide = difunto.contains(filtro)
                            || codigo.contains(filtro)
                            || trabajo.contains(filtro)
                            || responsable.contains(filtro);
                }

                return coincide;
            }
        };

        filteredPendientes.setPredicate(predicado);
        filteredAsignados.setPredicate(predicado);

        if (filteredPendientes.isEmpty()) {
            String mensajePendientes = "No se encontraron servicios pendientes";
            if (filtro.isEmpty()) {
                mensajePendientes = "No hay servicios pendientes de asignar";
            }
            tablaPendientes.setPlaceholder(new Label(mensajePendientes));
        }
        if (filteredAsignados.isEmpty()) {
            String mensajeAsignados = "No se encontraron trabajos asignados";
            if (filtro.isEmpty()) {
                mensajeAsignados = "No hay trabajos asignados";
            }
            tablaAsignados.setPlaceholder(new Label(mensajeAsignados));
        }

        actualizarContadores();
    }

    /**
     * Muestra en el panel lateral los datos de un servicio pendiente, o el texto de que no hay ninguno
     * seleccionado.
     *
     * @param item servicio pendiente seleccionado, o {@code null} si no hay ninguno
     */
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

    /**
     * Asigna el servicio pendiente seleccionado al tanatopractor elegido. Si falta alguno de los dos
     * avisa al usuario; si se guarda, pasa el servicio de pendientes a asignados.
     */
    private void confirmarAsignacion() {
        cmbTanatopractor.getStyleClass().remove("error");

        TanatopractorResumen tanatopractor = cmbTanatopractor.getValue();

        if (pendienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Servicio requerido",
                    "Selecciona primero un servicio pendiente de asignar.");
        } else if (tanatopractor == null) {
            if (!cmbTanatopractor.getStyleClass().contains("error")) {
                cmbTanatopractor.getStyleClass().add("error");
            }
            mostrarAlerta(Alert.AlertType.WARNING, "Tanatopractor requerido",
                    "Debes seleccionar un tanatopractor responsable antes de confirmar.");
        } else {
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
    }

    /**
     * Abre la ficha del trabajo asignado seleccionado. Si no hay selección, avisa al usuario.
     */
    private void abrirFichaSeleccionada() {
        if (asignadoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Selecciona un trabajo asignado para abrir su ficha del servicio.");
        } else {
            String idAsignacion = asignadoSeleccionado.getId();
            if (onAbrirFicha != null) {
                onAbrirFicha.accept(idAsignacion);
            } else {
                abrirFichaEnVista(idAsignacion);
            }
        }
    }

    /**
     * Carga la ficha de servicio de una asignación y la muestra en lugar de esta vista, con una acción
     * para volver a ella.
     *
     * @param idAsignacion identificador de la asignación
     */
    private void abrirFichaEnVista(String idAsignacion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/ficha_servicio.fxml"));
            Parent vistaFicha = loader.load();
            FichaServicioController fichaController = loader.getController();
            fichaController.setMainController(mainController);
            fichaController.cargarServicioDesdeBD(idAsignacion);

            if (rootAsignaciones != null && rootAsignaciones.getParent() instanceof StackPane) {
                StackPane contenedor = (StackPane) rootAsignaciones.getParent();
                fichaController.setOnVolver(new Runnable() {
                    @Override
                    public void run() {
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

    /**
     * Actualiza los contadores de servicios pendientes y de trabajo asignado según las filas visibles.
     */
    private void actualizarContadores() {
        lblBadgePendientes.setText("● Pendiente · " + filteredPendientes.size());
        lblTituloAsignados.setText("Trabajo asignado · " + filteredAsignados.size());
    }

    /**
     * Rellena el panel de carga de trabajo con cada tanatopractor y el número de servicios que tiene
     * en curso y asignados.
     */
    private void actualizarCargaTrabajo() {
        vboxCargaTrabajo.getChildren().clear();

        if (tanatopractores.isEmpty()) {
            Label vacio = new Label("Sin tanatopractores registrados");
            vacio.setFont(Font.font("Inter 18pt Regular", 12.0));
            vacio.setStyle("-fx-text-fill: #7a5230;");
            vboxCargaTrabajo.getChildren().add(vacio);
        } else {
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
    }

    /**
     * Construye el texto de carga de un tanatopractor.
     *
     * @param enCurso servicios en curso
     * @param pendientes servicios asignados que todavía no han empezado
     * @return texto con la carga, o {@code Disponible} si no tiene servicios
     */
    private String construirTextoCarga(int enCurso, int pendientes) {
        String resultado = "Disponible";

        if (enCurso > 0 || pendientes > 0) {
            List<String> partes = new ArrayList<>();
            if (enCurso > 0) {
                partes.add(enCurso + " en curso");
            }
            if (pendientes > 0) {
                String sufijo = " asignados";
                if (pendientes == 1) {
                    sufijo = " asignado";
                }
                partes.add(pendientes + sufijo);
            }
            resultado = String.join(" · ", partes);
        }

        return resultado;
    }

    /**
     * Da formato a la fecha de llegada: solo la hora si es de hoy, y fecha y hora en otro caso.
     *
     * @param fecha fecha de llegada
     * @return texto de la fecha
     */
    private String formatearFechaLlegada(Date fecha) {
        LocalDate fechaLocal = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String resultado = sdfFechaHora.format(fecha);

        if (fechaLocal.equals(LocalDate.now())) {
            resultado = "Hoy · " + sdfHora.format(fecha);
        }

        return resultado;
    }

    /**
     * Convierte un texto nulo en una cadena vacía.
     *
     * @param texto texto original, que puede ser nulo
     * @return el mismo texto, o una cadena vacía si era nulo
     */
    private String textoSeguro(String texto) {
        String resultado = "";
        if (texto != null) {
            resultado = texto;
        }
        return resultado;
    }

    /**
     * Muestra una ventana de aviso y espera a que el usuario la cierre.
     *
     * @param tipo tipo de aviso
     * @param titulo título de la ventana
     * @param mensaje mensaje que se muestra
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
