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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DifuntosController implements Initializable {

    private static final String NUEVO_CLIENTE = "Nuevo cliente";

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
    private TextField txtBuscar;
    @FXML
    private Label lblEstadoTabla;
    @FXML
    private TableColumn<Difunto, String> colNombre;
    @FXML
    private TableColumn<Difunto, Difunto.Servicio> colServicio;
    @FXML
    private TableColumn<Difunto, Date> colFechaMuerte;
    @FXML
    private TableColumn<Difunto, String> colCausa;

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
    private Button btnEliminar;
    @FXML
    private Button btnGuardarNuevo;

    @FXML
    private TextField txtNuevoNombre;
    @FXML
    private TextField txtNuevoApellidos;
    @FXML
    private DatePicker dpNuevoNacimiento;
    @FXML
    private DatePicker dpNuevoMuerte;
    @FXML
    private TextField txtNuevaCausa;
    @FXML
    private RadioButton rbNuevoEntierro;
    @FXML
    private RadioButton rbNuevoCremacion;
    @FXML
    private ComboBox<String> cmbNuevoMaterialFeretro;
    @FXML
    private ComboBox<String> cmbNuevoCliente;
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
    private TextField txtDetalleCausa;
    @FXML
    private RadioButton rbDetalleEntierro;
    @FXML
    private RadioButton rbDetalleCremacion;
    @FXML
    private ComboBox<String> cmbDetalleMaterialFeretro;
    @FXML
    private ComboBox<String> cmbDetalleMaterialUrna;

    private final ObservableList<Difunto> masterData = FXCollections.observableArrayList();
    private Difunto difuntoSeleccionado;
    private FilteredList<Difunto> filteredData;
    private final Map<String, String> idsClientes = new HashMap<String, String>();
    private final List<String> nombresClientes = new ArrayList<String>();
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
        configurarTabla();
        configurarBusqueda();
        configurarBotones();
        tablaDifuntos.setPlaceholder(new Label("Cargando difuntos..."));
        lblEstadoTabla.setText("Cargando difuntos...");
        cargarDatos();
    }

    private void configurarMateriales() {
        DifuntoDAO dao = new DifuntoDAO();
        cargarClientes(dao.obtenerClientes());
        cmbNuevoMaterialFeretro.getItems().setAll(dao.obtenerMaterialesFeretros());
        cmbNuevoMaterialUrna.getItems().setAll(dao.obtenerMaterialesUrnas());
        cmbDetalleMaterialFeretro.getItems().setAll(cmbNuevoMaterialFeretro.getItems());
        cmbDetalleMaterialUrna.getItems().setAll(cmbNuevoMaterialUrna.getItems());
        cmbNuevoCliente.setEditable(true);
        cmbNuevoCliente.setPromptText("Escribe para buscar un cliente");
        cmbNuevoCliente.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filtrarClientes(newValue);
            }
        });
        cmbNuevoCliente.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (NUEVO_CLIENTE.equals(cmbNuevoCliente.getValue()) && mainController != null) {
                    mainController.cargarVista("clientes.fxml");
                }
            }
        });
    }

    private void cargarClientes(List<String> clientes) {
        idsClientes.clear();
        nombresClientes.clear();
        cmbNuevoCliente.getItems().clear();

        for (String cliente : clientes) {
            int separador = cliente.indexOf(" - ");
            if (separador > 0) {
                String id = cliente.substring(0, separador);
                String nombre = cliente.substring(separador + 3);
                idsClientes.put(nombre, id);
                nombresClientes.add(nombre);
                cmbNuevoCliente.getItems().add(nombre);
            }
        }

        cmbNuevoCliente.getItems().add(NUEVO_CLIENTE);
    }

    private void filtrarClientes(String texto) {
        if (texto != null && !texto.equals(NUEVO_CLIENTE)) {
            String filtro = texto.toLowerCase().trim();
            cmbNuevoCliente.getItems().clear();

            for (String nombre : nombresClientes) {
                if (filtro.isEmpty() || nombre.toLowerCase().contains(filtro)) {
                    cmbNuevoCliente.getItems().add(nombre);
                }
            }

            cmbNuevoCliente.getItems().add(NUEVO_CLIENTE);
        }
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

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<Difunto, String>("nombreCompleto"));
        colServicio.setCellValueFactory(new PropertyValueFactory<Difunto, Difunto.Servicio>("servicio"));
        colCausa.setCellValueFactory(new PropertyValueFactory<Difunto, String>("causa_muerte"));
        colFechaMuerte.setCellValueFactory(new PropertyValueFactory<Difunto, Date>("fecha_muerte"));
        Utils.columnDate(colFechaMuerte);

        colNombre.setReorderable(false);
        colServicio.setReorderable(false);
        colFechaMuerte.setReorderable(false);
        colCausa.setReorderable(false);
    }

    private void configurarBusqueda() {
        filteredData = new FilteredList<Difunto>(masterData, new java.util.function.Predicate<Difunto>() {
            @Override
            public boolean test(Difunto difunto) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new java.util.function.Predicate<Difunto>() {
                    @Override
                    public boolean test(Difunto difunto) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String filtro = newValue.toLowerCase();
                        String nombre = difunto.getNombreCompleto();
                        String causa = difunto.getCausa_muerte();
                        String servicio = "";

                        if (nombre == null) {
                            nombre = "";
                        }
                        if (causa == null) {
                            causa = "";
                        }
                        if (difunto.getServicio() != null) {
                            servicio = difunto.getServicio().name();
                        }

                        return nombre.toLowerCase().contains(filtro)
                                || causa.toLowerCase().contains(filtro)
                                || servicio.toLowerCase().contains(filtro);
                    }
                });
                actualizarEstadoBusqueda(newValue);
            }
        });

        SortedList<Difunto> sortedData = new SortedList<Difunto>(filteredData);
        sortedData.comparatorProperty().bind(tablaDifuntos.comparatorProperty());
        tablaDifuntos.setItems(sortedData);
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

    private void configurarBotones() {
        btnNuevoDifunto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                abrirFormularioNuevo();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cerrarFormularioNuevo();
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

        btnEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                activarEdicion();
            }
        });

        btnGuardarEdicion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarEdicion();
            }
        });

        btnVolver.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                volverAListado();
            }
        });

        tablaDifuntos.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Difunto seleccion = tablaDifuntos.getSelectionModel().getSelectedItem();
                if (seleccion != null) {
                    mostrarDetalles(seleccion);
                }
            }
        });
    }

    private void cargarDatos() {
        Task<List<Difunto>> taskObtenerDifuntos = new Task<List<Difunto>>() {
            @Override
            protected List<Difunto> call() throws Exception {
                DifuntoDAO dao = new DifuntoDAO();
                return dao.obtenerDifuntos();
            }
        };

        taskObtenerDifuntos.setOnSucceeded(new EventHandler<javafx.concurrent.WorkerStateEvent>() {
            @Override
            public void handle(javafx.concurrent.WorkerStateEvent event) {
                masterData.setAll(taskObtenerDifuntos.getValue());
                tablaDifuntos.sort();
                actualizarEstadoBusqueda(txtBuscar.getText());
            }
        });

        taskObtenerDifuntos.setOnFailed(new EventHandler<javafx.concurrent.WorkerStateEvent>() {
            @Override
            public void handle(javafx.concurrent.WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se han podido cargar los difuntos desde MongoDB.");
            }
        });

        Thread hiloSecundario = new Thread(taskObtenerDifuntos);
        hiloSecundario.setDaemon(true);
        hiloSecundario.start();
    }

    private void abrirFormularioNuevo() {
        Utils.openForm(panelFormularioNuevo, spacerLateral);
    }

    private void cerrarFormularioNuevo() {
        Utils.closeForm(panelFormularioNuevo, spacerLateral);
    }

    private void guardarNuevoDifunto() {
        boolean datosIncompletos = txtNuevoNombre.getText().trim().isEmpty()
                || txtNuevoApellidos.getText().trim().isEmpty()
                || cmbNuevoCliente.getValue() == null
                || cmbNuevoMaterialFeretro.getValue() == null
                || dpNuevoNacimiento.getValue() == null
                || dpNuevoMuerte.getValue() == null;
        if (datosIncompletos) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Los campos marcados con * son obligatorios.");
        } else {
            boolean fechasValidas = validarFechas(dpNuevoNacimiento.getValue(), dpNuevoMuerte.getValue());
            if (fechasValidas) {
                Difunto nuevoDifunto = crearDifuntoDesdeFormularioNuevo();
                boolean referenciasValidas = !nuevoDifunto.getId_cliente().isEmpty()
                        && !nuevoDifunto.getId_feretro().isEmpty();

                if (!referenciasValidas) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Referencias obligatorias", "Debes seleccionar un cliente y un material de féretro válido.");
                } else {
                    DifuntoDAO dao = new DifuntoDAO();
                    if (dao.insertarDifunto(nuevoDifunto)) {
                        masterData.add(nuevoDifunto);
                        tablaDifuntos.sort();
                        lblEstadoTabla.setText(masterData.size() + " difuntos");
                        limpiarFormularioNuevo();
                        cerrarFormularioNuevo();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido guardar el difunto. Comprueba las referencias al cliente y al féretro.");
                    }
                }
            }
        }
    }

    private Difunto crearDifuntoDesdeFormularioNuevo() {
        Difunto difunto = new Difunto();
        DifuntoDAO dao = new DifuntoDAO();
        String cliente = cmbNuevoCliente.getValue();
        if (cliente != null && idsClientes.containsKey(cliente)) {
            difunto.setId_cliente(idsClientes.get(cliente));
        }
        difunto.setId_feretro(dao.obtenerIdPorMaterial("feretro", cmbNuevoMaterialFeretro.getValue()));
        difunto.setId_urna(dao.obtenerIdPorMaterial("urna", cmbNuevoMaterialUrna.getValue()));
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
        if (fecha == null) {
            return null;
        }
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private boolean validarFechas(LocalDate nacimiento, LocalDate muerte) {
        LocalDate hoy = LocalDate.now();

        if (nacimiento.isAfter(hoy)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fecha incorrecta", "La fecha de nacimiento no puede ser futura.");
            return false;
        }

        if (muerte.isAfter(hoy)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fecha incorrecta", "La fecha de fallecimiento no puede ser futura.");
            return false;
        }

        if (muerte.isBefore(nacimiento)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fechas incorrectas", "La fecha de fallecimiento debe ser posterior a la fecha de nacimiento.");
            return false;
        }

        return true;
    }

    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevoApellidos.clear();
        cmbNuevoCliente.setValue(null);
        dpNuevoNacimiento.setValue(null);
        dpNuevoMuerte.setValue(null);
        txtNuevaCausa.clear();
        cmbNuevoMaterialFeretro.setValue(null);
        cmbNuevoMaterialUrna.setValue(null);
        rbNuevoEntierro.setSelected(true);
    }

    private void mostrarDetalles(Difunto difunto) {
        difuntoSeleccionado = difunto;
        cargarDatosEnDetalle(difunto);

        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);
        vistaTabla.setVisible(false);
        vistaTabla.setManaged(false);
        vistaDetalles.setVisible(true);
        vistaDetalles.setManaged(true);
        tablaDifuntos.getSelectionModel().clearSelection();
        bloquearCamposEdicion();
    }

    private void cargarDatosEnDetalle(Difunto difunto) {
        txtDetalleNombre.setText(difunto.getNombre());
        txtDetalleApellidos.setText(difunto.getApellidos());
        dpDetalleNacimiento.setValue(convertirAFechaPicker(difunto.getFecha_nacimiento()));
        dpDetalleMuerte.setValue(convertirAFechaPicker(difunto.getFecha_muerte()));
        txtDetalleCausa.setText(difunto.getCausa_muerte());

        if (difunto.getFeretro() == null) {
            cmbDetalleMaterialFeretro.setValue(null);
        } else {
            cmbDetalleMaterialFeretro.setValue(difunto.getFeretro().getMaterial());
        }

        if (difunto.getUrna() == null) {
            cmbDetalleMaterialUrna.setValue(null);
        } else {
            cmbDetalleMaterialUrna.setValue(difunto.getUrna().getMaterial());
        }

        if (difunto.getServicio() == Difunto.Servicio.CREMACIÓN) {
            rbDetalleCremacion.setSelected(true);
        } else {
            rbDetalleEntierro.setSelected(true);
        }
    }

    private void activarEdicion() {
        cambiarEstadoCamposEdicion(true);
        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
        btnEliminar.setVisible(false);
        btnEliminar.setManaged(false);
        btnGuardarEdicion.setVisible(true);
        btnGuardarEdicion.setManaged(true);
    }

    private void guardarEdicion() {
        boolean datosIncompletos = difuntoSeleccionado == null
                || txtDetalleNombre.getText().trim().isEmpty()
                || txtDetalleApellidos.getText().trim().isEmpty()
                || dpDetalleNacimiento.getValue() == null
                || dpDetalleMuerte.getValue() == null;
        if (datosIncompletos) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Los campos marcados con * son obligatorios.");
        } else {
            boolean fechasValidas = validarFechas(dpDetalleNacimiento.getValue(), dpDetalleMuerte.getValue());
            if (fechasValidas) {
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

                DifuntoDAO dao = new DifuntoDAO();
                if (dao.actualizarDifunto(difuntoSeleccionado)) {
                    tablaDifuntos.refresh();
                    bloquearCamposEdicion();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se han podido actualizar los datos del difunto en la base de datos.");
                }
            }
        }
    }

    private void eliminarDifuntoSeleccionado() {
        if (difuntoSeleccionado != null) {
            DifuntoDAO dao = new DifuntoDAO();
            if (dao.eliminarDifunto(difuntoSeleccionado.getId())) {
                masterData.remove(difuntoSeleccionado);
                lblEstadoTabla.setText(masterData.size() + " difuntos");
                volverAListado();
                difuntoSeleccionado = null;
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido eliminar el difunto de la base de datos.");
            }
        }
    }

    private void volverAListado() {
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);
        bloquearCamposEdicion();
    }

    private void cambiarEstadoCamposEdicion(boolean editable) {
        txtDetalleNombre.setEditable(editable);
        txtDetalleApellidos.setEditable(editable);
        dpDetalleNacimiento.setDisable(!editable);
        dpDetalleMuerte.setDisable(!editable);
        txtDetalleCausa.setEditable(editable);
        rbDetalleEntierro.setDisable(!editable);
        rbDetalleCremacion.setDisable(!editable);
        cmbDetalleMaterialFeretro.setDisable(!editable);
        cmbDetalleMaterialUrna.setDisable(!editable);

        String estilo;
        if (editable) {
            estilo = "-fx-background-color: #FFFFFF; -fx-border-color: #C68E17; -fx-border-radius: 4;";
        } else {
            estilo = "-fx-background-color: #EDE7D9; -fx-border-color: #7A5230; -fx-border-radius: 4;";
        }

        txtDetalleNombre.setStyle(estilo);
        txtDetalleApellidos.setStyle(estilo);
        txtDetalleCausa.setStyle(estilo);
        cmbDetalleMaterialFeretro.setStyle(estilo);
        cmbDetalleMaterialUrna.setStyle(estilo);
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
