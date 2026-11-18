package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.DifuntoDAO;
import com.tartanga.funerariatrujillo.Objects.Difunto;
import com.tartanga.funerariatrujillo.Objects.Feretro;
import com.tartanga.funerariatrujillo.Objects.Urna;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.scene.control.Control;

public class DifuntosController implements Initializable {

    private static final String NUEVO_CLIENTE = "Nuevo cliente";

    private final DifuntoDAO difuntoDAO;

    public DifuntosController() {
        this.difuntoDAO = new DifuntoDAO();
    }

    public DifuntosController(DifuntoDAO difuntoDAO) {
        this.difuntoDAO = difuntoDAO;
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
    private TableView<Difunto> tablaDifuntos;

    @FXML
    private Button btnNuevoDifunto;
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
    private TextField txtNuevoNombre;
    @FXML
    private TextField txtNuevoApellidos;
    @FXML
    private TextField txtNuevoCliente;
    @FXML
    private ListView<String> listaClientes;
    @FXML
    private DatePicker dpNuevoNacimiento;
    @FXML
    private DatePicker dpNuevoMuerte;
    @FXML
    private RadioButton rbNuevoEntierro;
    @FXML
    private RadioButton rbNuevoCremacion;
    @FXML
    private TextField txtNuevaCausa;
    @FXML
    private ComboBox<String> cmbNuevoMaterialFeretro;
    @FXML
    private ComboBox<String> cmbNuevoMaterialUrna;

    @FXML
    private TextField txtDetalleNombre;
    @FXML
    private TextField txtDetalleApellidos;
    @FXML
    private DatePicker dpDetalleNacimiento;
    @FXML
    private DatePicker dpDetalleMuerte;
    @FXML
    private RadioButton rbDetalleEntierro;
    @FXML
    private RadioButton rbDetalleCremacion;
    @FXML
    private TextField txtDetalleCausa;
    @FXML
    private ComboBox<String> cmbDetalleMaterialFeretro;
    @FXML
    private ComboBox<String> cmbDetalleMaterialUrna;

    @FXML
    private Label lblEstadoTabla;
    @FXML
    private TableColumn<Difunto, String> colID;
    @FXML
    private TableColumn<Difunto, String> colNombre;
    @FXML
    private TableColumn<Difunto, Difunto.Servicio> colServicio;
    @FXML
    private TableColumn<Difunto, Date> colFechaMuerte;
    @FXML
    private TableColumn<Difunto, String> colCausa;

    private final ObservableList<Difunto> masterData = FXCollections.observableArrayList();
    private Difunto difuntoSeleccionado;
    private FilteredList<Difunto> filteredData;
    private final Map<String, String> idsClientes = new HashMap<String, String>();
    private final ObservableList<String> nombresClientes = FXCollections.observableArrayList();
    private FilteredList<String> clientesFiltrados;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);

        configurarGruposDeServicio();
        configurarMateriales();
        establecerFechasPorDefecto();

        btnNuevoDifunto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openForm();
            }
        });

        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevoDifunto();
            }
        });

        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarDifuntoSeleccionado();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        filteredData = new FilteredList<>(masterData, new Predicate<Difunto>() {
            @Override
            public boolean test(Difunto difunto) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Difunto>() {
                    @Override
                    public boolean test(Difunto difunto) {
                        boolean coincide = true;

                        if (newValue != null && !newValue.isEmpty()) {
                            String lowerCaseFilter = newValue.toLowerCase();
                            String nombre = textoSinNulos(difunto.getNombreCompleto());
                            String causa = textoSinNulos(difunto.getCausa_muerte());
                            String servicio = "";
                            if (difunto.getServicio() != null) {
                                servicio = difunto.getServicio().name();
                            }

                            coincide = nombre.toLowerCase().contains(lowerCaseFilter)
                                    || causa.toLowerCase().contains(lowerCaseFilter)
                                    || servicio.toLowerCase().contains(lowerCaseFilter);
                        }

                        return coincide;
                    }
                });
                actualizarEstadoBusqueda(newValue);
            }
        });

        SortedList<Difunto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaDifuntos.comparatorProperty());
        tablaDifuntos.setItems(sortedData);

        colFechaMuerte.setSortType(TableColumn.SortType.DESCENDING);
        tablaDifuntos.getSortOrder().add(colFechaMuerte);

        if (colID != null) {
            colID.setReorderable(false);
            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            Utils.columnID(colID);
        }
        colNombre.setReorderable(false);
        colServicio.setReorderable(false);
        colFechaMuerte.setReorderable(false);
        colCausa.setReorderable(false);

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colFechaMuerte.setCellValueFactory(new PropertyValueFactory<>("fecha_muerte"));
        colCausa.setCellValueFactory(new PropertyValueFactory<>("causa_muerte"));

        Utils.columnDate(colFechaMuerte);

        tablaDifuntos.setPlaceholder(new Label("Cargando difuntos..."));
        lblEstadoTabla.setText("Cargando difuntos...");

        cargarDatos();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
    }

    private void configurarMateriales() {
        cargarClientes(difuntoDAO.obtenerClientes());
        cmbNuevoMaterialFeretro.getItems().setAll(difuntoDAO.obtenerMaterialesFeretros());
        cmbNuevoMaterialUrna.getItems().setAll(difuntoDAO.obtenerMaterialesUrnas());
        cmbDetalleMaterialFeretro.getItems().setAll(cmbNuevoMaterialFeretro.getItems());
        cmbDetalleMaterialUrna.getItems().setAll(cmbNuevoMaterialUrna.getItems());

        clientesFiltrados = new FilteredList<>(nombresClientes, new Predicate<String>() {
            @Override
            public boolean test(String nombre) {
                return true;
            }
        });
        listaClientes.setItems(clientesFiltrados);

        txtNuevoCliente.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filtrarClientes(newValue);
                actualizarListaClientes();
            }
        });

        txtNuevoCliente.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                actualizarListaClientes();
            }
        });

        ChangeListener<Boolean> focoListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue && observable == txtNuevoCliente.focusedProperty()) {
                    actualizarListaClientes();
                } else if (!newValue) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!txtNuevoCliente.isFocused() && !listaClientes.isFocused()) {
                                ocultarListaClientes();
                            }
                        }
                    });
                }
            }
        };

        txtNuevoCliente.focusedProperty().addListener(focoListener);
        listaClientes.focusedProperty().addListener(focoListener);

        listaClientes.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String elegido = listaClientes.getSelectionModel().getSelectedItem();

                if (elegido != null) {
                    if (NUEVO_CLIENTE.equals(elegido)) {
                        if (mainController != null) {
                            mainController.cargarVista("clientes.fxml");
                        }
                    } else {
                        txtNuevoCliente.setText(elegido);
                    }
                    listaClientes.getSelectionModel().clearSelection();
                    ocultarListaClientes();
                }
            }
        });
    }

    private void actualizarListaClientes() {
        boolean mostrar = txtNuevoCliente.isFocused()
                && !idsClientes.containsKey(txtNuevoCliente.getText().trim());
        listaClientes.setVisible(mostrar);
        listaClientes.setManaged(mostrar);
    }

    private void ocultarListaClientes() {
        listaClientes.setVisible(false);
        listaClientes.setManaged(false);
    }

    private void cargarClientes(List<String> clientes) {
        idsClientes.clear();
        nombresClientes.clear();

        for (String cliente : clientes) {
            int separador = cliente.indexOf(" - ");
            if (separador > 0) {
                String id = cliente.substring(0, separador);
                String nombre = cliente.substring(separador + 3);
                idsClientes.put(nombre, id);
                nombresClientes.add(nombre);
            }
        }

        nombresClientes.add(NUEVO_CLIENTE);
    }

    private void filtrarClientes(String texto) {
        String textoFiltro = "";
        if (texto != null) {
            textoFiltro = texto.toLowerCase().trim();
        }
        String filtro = textoFiltro;

        clientesFiltrados.setPredicate(new Predicate<String>() {
            @Override
            public boolean test(String nombre) {
                return nombre.equals(NUEVO_CLIENTE) || nombre.toLowerCase().contains(filtro);
            }
        });
    }

    private void establecerFechasPorDefecto() {
        LocalDate hoy = LocalDate.now();
        dpNuevoNacimiento.setValue(hoy);
        dpNuevoMuerte.setValue(hoy);
    }

    private void configurarGruposDeServicio() {
        ToggleGroup grupoNuevo = new ToggleGroup();
        rbNuevoEntierro.setToggleGroup(grupoNuevo);
        rbNuevoCremacion.setToggleGroup(grupoNuevo);
        rbNuevoEntierro.setSelected(true);

        ToggleGroup grupoDetalle = new ToggleGroup();
        rbDetalleEntierro.setToggleGroup(grupoDetalle);
        rbDetalleCremacion.setToggleGroup(grupoDetalle);
    }

    private void actualizarEstadoBusqueda(String textoBusqueda) {
        if (filteredData.size() > 0) {
            lblEstadoTabla.setText(filteredData.size() + " difuntos");
            tablaDifuntos.setPlaceholder(new Label("No hay difuntos"));
        } else if (masterData.isEmpty()) {
            lblEstadoTabla.setText("0 difuntos");
            tablaDifuntos.setPlaceholder(new Label("No hay difuntos"));
        } else if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            lblEstadoTabla.setText("0 difuntos");
            tablaDifuntos.setPlaceholder(new Label("No hay difuntos"));
        } else {
            lblEstadoTabla.setText("Difunto no encontrado");
            tablaDifuntos.setPlaceholder(new Label("Difunto no encontrado"));
        }
    }

    private void cargarDatos() {
        Task<List<Difunto>> taskObtenerDifuntos = new Task<List<Difunto>>() {
            @Override
            protected List<Difunto> call() throws Exception {
                return difuntoDAO.obtenerDifuntos();
            }
        };

        taskObtenerDifuntos.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                masterData.setAll(taskObtenerDifuntos.getValue());
                tablaDifuntos.sort();
                actualizarEstadoBusqueda(txtBuscar.getText());
            }
        });

        taskObtenerDifuntos.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de conexión");
                alert.setHeaderText(null);
                alert.setContentText("No se han podido cargar los difuntos desde la base de datos.");
                alert.showAndWait();
            }
        });

        Thread hilo = new Thread(taskObtenerDifuntos);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void guardarNuevoDifunto() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevoApellidos.getStyleClass().remove("error");
        txtNuevoCliente.getStyleClass().remove("error");
        cmbNuevoMaterialFeretro.getStyleClass().remove("error");
        dpNuevoNacimiento.getStyleClass().remove("error");
        dpNuevoMuerte.getStyleClass().remove("error");

        String mensaje = validarNuevoDifunto();

        if (mensaje == null) {
            Difunto nuevoDifunto = crearDifuntoDesdeFormularioNuevo();

            if (nuevoDifunto.getId_cliente() == null || nuevoDifunto.getId_cliente().isEmpty()
                    || nuevoDifunto.getId_feretro() == null || nuevoDifunto.getId_feretro().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Referencias obligatorias");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un cliente y un material de féretro válido.");
                alert.showAndWait();
            } else if (difuntoDAO.insertarDifunto(nuevoDifunto)) {
                masterData.add(nuevoDifunto);
                tablaDifuntos.sort();
                actualizarEstadoBusqueda(txtBuscar.getText());

                limpiarFormularioNuevo();
                closeForm();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al guardar el difunto en la base de datos.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos no válidos");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
    }

    private String validarNuevoDifunto() {
        String mensaje = null;
        boolean hayError = false;

        if (txtNuevoNombre.getText().isBlank()) {
            txtNuevoNombre.getStyleClass().add("error");
            hayError = true;
        }

        if (txtNuevoApellidos.getText().isBlank()) {
            txtNuevoApellidos.getStyleClass().add("error");
            hayError = true;
        }

        if (txtNuevoCliente.getText().isBlank()) {
            txtNuevoCliente.getStyleClass().add("error");
            hayError = true;
        }

        if (cmbNuevoMaterialFeretro.getValue() == null || cmbNuevoMaterialFeretro.getValue().isBlank()) {
            cmbNuevoMaterialFeretro.getStyleClass().add("error");
            hayError = true;
        }

        if (dpNuevoNacimiento.getValue() == null) {
            dpNuevoNacimiento.getStyleClass().add("error");
            hayError = true;
        }

        if (dpNuevoMuerte.getValue() == null) {
            dpNuevoMuerte.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje = "Los campos obligatorios no pueden estar vacíos.";
        } else {
            mensaje = validarFechas(dpNuevoNacimiento.getValue(), dpNuevoMuerte.getValue());
        }

        return mensaje;
    }

    private Difunto crearDifuntoDesdeFormularioNuevo() {
        Difunto difunto = new Difunto();
        String cliente = txtNuevoCliente.getText().trim();
        if (idsClientes.containsKey(cliente)) {
            difunto.setId_cliente(idsClientes.get(cliente));
        }
        difunto.setId_feretro(difuntoDAO.obtenerIdPorMaterial("feretro", cmbNuevoMaterialFeretro.getValue()));
        difunto.setId_urna(difuntoDAO.obtenerIdPorMaterial("urna", cmbNuevoMaterialUrna.getValue()));
        difunto.setNombre(txtNuevoNombre.getText().trim());
        difunto.setApellidos(txtNuevoApellidos.getText().trim());
        difunto.setFecha_nacimiento(convertirAFecha(dpNuevoNacimiento.getValue()));
        difunto.setFecha_muerte(convertirAFecha(dpNuevoMuerte.getValue()));
        difunto.setCausa_muerte(txtNuevaCausa.getText().trim());

        if (rbNuevoCremacion.isSelected()) {
            difunto.setServicio(Difunto.Servicio.CREMACIÓN);
        } else {
            difunto.setServicio(Difunto.Servicio.ENTIERRO);
        }

        Feretro feretro = new Feretro();
        feretro.setMaterial(cmbNuevoMaterialFeretro.getValue());
        difunto.setFeretro(feretro);

        Urna urna = new Urna();
        urna.setMaterial(cmbNuevoMaterialUrna.getValue());
        difunto.setUrna(urna);
        return difunto;
    }

    private Date convertirAFecha(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate convertirAFechaPicker(Date fecha) {
        LocalDate resultado = null;
        if (fecha != null) {
            resultado = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return resultado;
    }

    private String textoSinNulos(String texto) {
        String resultado = "";
        if (texto != null) {
            resultado = texto;
        }
        return resultado;
    }

    private String validarFechas(LocalDate nacimiento, LocalDate muerte) {
        LocalDate hoy = LocalDate.now();
        String mensaje = null;

        if (nacimiento.isAfter(hoy)) {
            mensaje = "La fecha de nacimiento no puede ser futura.";
        } else if (muerte.isAfter(hoy)) {
            mensaje = "La fecha de fallecimiento no puede ser futura.";
        } else if (muerte.isBefore(nacimiento)) {
            mensaje = "La fecha de fallecimiento debe ser posterior a la fecha de nacimiento.";
        }

        return mensaje;
    }

    private String validarDetalleDifunto() {
        String mensaje = null;
        boolean hayError = false;

        if (txtDetalleNombre.getText().isBlank()) {
            txtDetalleNombre.getStyleClass().add("error");
            hayError = true;
        }

        if (txtDetalleApellidos.getText().isBlank()) {
            txtDetalleApellidos.getStyleClass().add("error");
            hayError = true;
        }

        if (dpDetalleNacimiento.getValue() == null) {
            hayError = true;
        }

        if (dpDetalleMuerte.getValue() == null) {
            hayError = true;
        }

        if (cmbDetalleMaterialFeretro.getValue() == null || cmbDetalleMaterialFeretro.getValue().isBlank()) {
            cmbDetalleMaterialFeretro.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje = "No puedes dejar en blanco los campos obligatorios.";
        } else {
            mensaje = validarFechas(dpDetalleNacimiento.getValue(), dpDetalleMuerte.getValue());
        }

        return mensaje;
    }

    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevoApellidos.clear();
        txtNuevoCliente.clear();
        listaClientes.setVisible(false);
        listaClientes.setManaged(false);
        establecerFechasPorDefecto();
        txtNuevaCausa.clear();
        cmbNuevoMaterialFeretro.setValue(null);
        cmbNuevoMaterialUrna.setValue(null);
        rbNuevoEntierro.setSelected(true);
    }

    private void eliminarDifuntoSeleccionado() {
        if (difuntoSeleccionado != null) {
            if (difuntoDAO.eliminarDifunto(difuntoSeleccionado.getId())) {
                masterData.remove(difuntoSeleccionado);
                actualizarEstadoBusqueda(txtBuscar.getText());

                vistaDetalles.setVisible(false);
                vistaDetalles.setManaged(false);
                vistaTabla.setVisible(true);
                vistaTabla.setManaged(true);

                difuntoSeleccionado = null;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al eliminar el difunto de la base de datos.");
                alert.showAndWait();
            }
        }
    }

    private void openForm() {
        Utils.openForm(panelFormularioNuevo, spacerLateral);
    }

    private void closeForm() {
        Utils.closeForm(panelFormularioNuevo, spacerLateral);
    }

    private void configurarSeleccionDeTabla() {
        tablaDifuntos.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Difunto newSelection = tablaDifuntos.getSelectionModel().getSelectedItem();

                if (newSelection != null) {
                    difuntoSeleccionado = newSelection;

                    try {
                        txtDetalleNombre.setText(textoSinNulos(newSelection.getNombre()));
                        txtDetalleApellidos.setText(textoSinNulos(newSelection.getApellidos()));
                        dpDetalleNacimiento.setValue(convertirAFechaPicker(newSelection.getFecha_nacimiento()));
                        dpDetalleMuerte.setValue(convertirAFechaPicker(newSelection.getFecha_muerte()));
                        txtDetalleCausa.setText(textoSinNulos(newSelection.getCausa_muerte()));

                        if (newSelection.getFeretro() != null) {
                            cmbDetalleMaterialFeretro.setValue(newSelection.getFeretro().getMaterial());
                        } else {
                            cmbDetalleMaterialFeretro.setValue(null);
                        }

                        if (newSelection.getUrna() != null) {
                            cmbDetalleMaterialUrna.setValue(newSelection.getUrna().getMaterial());
                        } else {
                            cmbDetalleMaterialUrna.setValue(null);
                        }

                        if (newSelection.getServicio() == Difunto.Servicio.CREMACIÓN) {
                            rbDetalleCremacion.setSelected(true);
                        } else {
                            rbDetalleEntierro.setSelected(true);
                        }

                        panelFormularioNuevo.setTranslateX(400);
                        panelFormularioNuevo.setVisible(false);
                        spacerLateral.setPrefWidth(0);
                        spacerLateral.setMinWidth(0);
                        vistaTabla.setVisible(false);
                        vistaTabla.setManaged(false);
                        vistaDetalles.setVisible(true);
                        vistaDetalles.setManaged(true);
                        tablaDifuntos.getSelectionModel().clearSelection();

                    } catch (Exception ex) {
                        System.err.println("Error al cargar la ficha: " + ex.getMessage());
                    }
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

                txtDetalleNombre.getStyleClass().remove("error");
                txtDetalleApellidos.getStyleClass().remove("error");
                txtDetalleCausa.getStyleClass().remove("error");
                dpDetalleNacimiento.getStyleClass().remove("error");
                dpDetalleMuerte.getStyleClass().remove("error");
                cmbDetalleMaterialFeretro.getStyleClass().remove("error");

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
                txtDetalleNombre.getStyleClass().remove("error");
                txtDetalleApellidos.getStyleClass().remove("error");
                txtDetalleCausa.getStyleClass().remove("error");
                cmbDetalleMaterialFeretro.getStyleClass().remove("error");

                String mensaje = validarDetalleDifunto();

                if (mensaje == null) {
                    if (difuntoSeleccionado != null) {
                        difuntoSeleccionado.setNombre(txtDetalleNombre.getText().trim());
                        difuntoSeleccionado.setApellidos(txtDetalleApellidos.getText().trim());
                        difuntoSeleccionado.setFecha_nacimiento(convertirAFecha(dpDetalleNacimiento.getValue()));
                        difuntoSeleccionado.setFecha_muerte(convertirAFecha(dpDetalleMuerte.getValue()));
                        difuntoSeleccionado.setCausa_muerte(txtDetalleCausa.getText().trim());

                        if (rbDetalleCremacion.isSelected()) {
                            difuntoSeleccionado.setServicio(Difunto.Servicio.CREMACIÓN);
                        } else {
                            difuntoSeleccionado.setServicio(Difunto.Servicio.ENTIERRO);
                        }

                        if (difuntoSeleccionado.getFeretro() == null) {
                            difuntoSeleccionado.setFeretro(new Feretro());
                        }
                        difuntoSeleccionado.getFeretro().setMaterial(cmbDetalleMaterialFeretro.getValue());

                        if (difuntoSeleccionado.getUrna() == null) {
                            difuntoSeleccionado.setUrna(new Urna());
                        }
                        difuntoSeleccionado.getUrna().setMaterial(cmbDetalleMaterialUrna.getValue());

                        if (difuntoDAO.actualizarDifunto(difuntoSeleccionado)) {
                            tablaDifuntos.refresh();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Hubo un error al actualizar los datos en la base de datos.");
                            alert.showAndWait();
                        }
                    }

                    bloquearCamposEdicion();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Datos no válidos");
                    alert.setHeaderText(null);
                    alert.setContentText(mensaje);
                    alert.showAndWait();
                }
            }
        });
    }

    private void cambiarEstadoCamposEdicion(boolean editable) {
        txtDetalleNombre.setEditable(editable);
        txtDetalleApellidos.setEditable(editable);
        txtDetalleCausa.setEditable(editable);

        dpDetalleNacimiento.setDisable(!editable);
        dpDetalleMuerte.setDisable(!editable);
        rbDetalleEntierro.setDisable(!editable);
        rbDetalleCremacion.setDisable(!editable);
        cmbDetalleMaterialFeretro.setDisable(!editable);
        cmbDetalleMaterialUrna.setDisable(!editable);

        Control[] campos = {
            txtDetalleNombre, txtDetalleApellidos, txtDetalleCausa,
            dpDetalleNacimiento, dpDetalleMuerte,
            cmbDetalleMaterialFeretro, cmbDetalleMaterialUrna
        };

        for (Control campo : campos) {
            if (editable) {
                if (!campo.getStyleClass().contains("campo-edicion")) {
                    campo.getStyleClass().add("campo-edicion");
                }
            } else {
                campo.getStyleClass().remove("campo-edicion");
            }
        }
    }

    private void bloquearCamposEdicion() {
        cambiarEstadoCamposEdicion(false);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);
    }
}
