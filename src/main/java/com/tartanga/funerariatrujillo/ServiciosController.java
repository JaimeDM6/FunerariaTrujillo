package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.ServiciosDAO;
import com.tartanga.funerariatrujillo.Objects.Servicios;
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
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ServiciosController implements Initializable {

    @FXML
    private VBox vistaTabla;
    @FXML
    private VBox vistaDetalles;
    @FXML
    private VBox panelFormularioNuevo;
    @FXML
    private Region spacerLateral;
    @FXML
    private TableView<Servicios> tablaServicios;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblEstadoTabla;

    @FXML
    private TableColumn<Servicios, String> colID;
    @FXML
    private TableColumn<Servicios, String> colNombre;
    @FXML
    private TableColumn<Servicios, String> colDescripcion;
    @FXML
    private TableColumn<Servicios, Double> colPrecio;

    @FXML
    private Button btnNuevoServicio;
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
    private TextArea txtNuevaDescripcion;
    @FXML
    private TextField txtNuevoPrecio;

    @FXML
    private TextField txtDetalleNombre;
    @FXML
    private TextArea txtDetalleDescripcion;
    @FXML
    private TextField txtDetallePrecio;

    private final ObservableList<Servicios> masterData = FXCollections.observableArrayList();
    private Servicios servicioSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);

        configurarBotones();
        configurarBusqueda();
        configurarTabla();
        cargarDatos();
    }

    private void configurarBotones() {
        btnNuevoServicio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openForm();
            }
        });

        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevoServicio();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarServicioSeleccionado();
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

        tablaServicios.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Servicios seleccion = tablaServicios.getSelectionModel().getSelectedItem();
                if (seleccion != null) {
                    mostrarDetalles(seleccion);
                }
            }
        });
    }

    private void configurarBusqueda() {
        final FilteredList<Servicios> filteredData = new FilteredList<Servicios>(masterData, new Predicate<Servicios>() {
            @Override
            public boolean test(Servicios servicio) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                filteredData.setPredicate(new Predicate<Servicios>() {
                    @Override
                    public boolean test(Servicios servicio) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String filtro = newValue.toLowerCase();
                        String id = servicio.getId();
                        String nombre = servicio.getNombre();
                        String descripcion = servicio.getDescripcion();

                        if (id == null) {
                            id = "";
                        }
                        if (nombre == null) {
                            nombre = "";
                        }
                        if (descripcion == null) {
                            descripcion = "";
                        }

                        if (id.toLowerCase().contains(filtro)) {
                            return true;
                        }
                        if (nombre.toLowerCase().contains(filtro)) {
                            return true;
                        }
                        return descripcion.toLowerCase().contains(filtro);
                    }
                });
                lblEstadoTabla.setText(filteredData.size() + " servicios");
            }
        });

        SortedList<Servicios> sortedData = new SortedList<Servicios>(filteredData);
        sortedData.comparatorProperty().bind(tablaServicios.comparatorProperty());
        tablaServicios.setItems(sortedData);
    }

    private void configurarTabla() {
        colID.setCellValueFactory(new PropertyValueFactory<Servicios, String>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Servicios, String>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Servicios, String>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<Servicios, Double>("precio"));

        Utils.columnID(colID);
        configurarColumnaPrecio();

        colID.setReorderable(false);
        colNombre.setReorderable(false);
        colDescripcion.setReorderable(false);
        colPrecio.setReorderable(false);
    }

    private void configurarColumnaPrecio() {
        colPrecio.setCellFactory(new Callback<TableColumn<Servicios, Double>, TableCell<Servicios, Double>>() {
            @Override
            public TableCell<Servicios, Double> call(TableColumn<Servicios, Double> column) {
                return new TableCell<Servicios, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%.2f €", item));
                        }
                    }
                };
            }
        });
    }

    private void cargarDatos() {
        Task<List<Servicios>> taskObtenerServicios = new Task<List<Servicios>>() {
            @Override
            protected List<Servicios> call() throws Exception {
                ServiciosDAO dao = new ServiciosDAO();
                return dao.obtenerServicios();
            }
        };

        taskObtenerServicios.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                List<Servicios> resultado = taskObtenerServicios.getValue();
                masterData.setAll(resultado);
                tablaServicios.sort();
                lblEstadoTabla.setText(masterData.size() + " servicios");
            }
        });

        taskObtenerServicios.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se han podido cargar los servicios desde MongoDB.");
            }
        });

        Thread hiloSecundario = new Thread(taskObtenerServicios);
        hiloSecundario.setDaemon(true);
        hiloSecundario.start();
    }

    private void guardarNuevoServicio() {
        if (txtNuevoNombre.getText().trim().isEmpty()
                || txtNuevaDescripcion.getText().trim().isEmpty()
                || txtNuevoPrecio.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Debes completar el nombre, la descripción y el precio.");
            return;
        }

        try {
            Servicios nuevoServicio = new Servicios();
            nuevoServicio.setNombre(txtNuevoNombre.getText().trim());
            nuevoServicio.setDescripcion(txtNuevaDescripcion.getText().trim());
            Double precio = convertirPrecio(txtNuevoPrecio.getText());
            if (precio < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "El precio no puede ser negativo.");
                return;
            }
            nuevoServicio.setPrecio(precio);

            ServiciosDAO dao = new ServiciosDAO();
            if (dao.insertarServicio(nuevoServicio)) {
                masterData.add(nuevoServicio);
                tablaServicios.sort();
                lblEstadoTabla.setText(masterData.size() + " servicios");
                limpiarFormularioNuevo();
                closeForm();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido guardar el servicio en la base de datos.");
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "El precio debe ser un número, por ejemplo: 450.00.");
        }
    }

    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevaDescripcion.clear();
        txtNuevoPrecio.clear();
    }

    private void eliminarServicioSeleccionado() {
        if (servicioSeleccionado != null) {
            ServiciosDAO dao = new ServiciosDAO();

            if (dao.eliminarServicio(servicioSeleccionado.getId())) {
                masterData.remove(servicioSeleccionado);
                lblEstadoTabla.setText(masterData.size() + " servicios");
                volverAListado();
                servicioSeleccionado = null;
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido eliminar el servicio de la base de datos.");
            }
        }
    }

    private void openForm() {
        Utils.openForm(panelFormularioNuevo, spacerLateral);
    }

    private void closeForm() {
        Utils.closeForm(panelFormularioNuevo, spacerLateral);
    }

    private void mostrarDetalles(Servicios servicio) {
        servicioSeleccionado = servicio;
        txtDetalleNombre.setText(servicio.getNombre());
        txtDetalleDescripcion.setText(servicio.getDescripcion());
        txtDetallePrecio.setText(String.valueOf(servicio.getPrecio()));

        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);
        vistaTabla.setVisible(false);
        vistaTabla.setManaged(false);
        vistaDetalles.setVisible(true);
        vistaDetalles.setManaged(true);
        tablaServicios.getSelectionModel().clearSelection();
        bloquearCamposEdicion();
    }

    private void activarEdicion() {
        txtDetalleNombre.setEditable(true);
        txtDetalleDescripcion.setEditable(true);
        txtDetallePrecio.setEditable(true);

        String estiloEditable = "-fx-background-color: #FFFFFF; -fx-border-color: #C68E17; -fx-border-radius: 4;";
        txtDetalleNombre.setStyle(estiloEditable);
        txtDetalleDescripcion.setStyle(estiloEditable);
        txtDetallePrecio.setStyle(estiloEditable);

        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
        btnEliminar.setVisible(false);
        btnEliminar.setManaged(false);
        btnGuardarEdicion.setVisible(true);
        btnGuardarEdicion.setManaged(true);
    }

    private void guardarEdicion() {
        if (servicioSeleccionado == null) {
            return;
        }

        if (txtDetalleNombre.getText().trim().isEmpty()
                || txtDetalleDescripcion.getText().trim().isEmpty()
                || txtDetallePrecio.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Debes completar el nombre, la descripción y el precio.");
            return;
        }

        try {
            servicioSeleccionado.setNombre(txtDetalleNombre.getText().trim());
            servicioSeleccionado.setDescripcion(txtDetalleDescripcion.getText().trim());
            Double precio = convertirPrecio(txtDetallePrecio.getText());
            if (precio < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "El precio no puede ser negativo.");
                return;
            }
            servicioSeleccionado.setPrecio(precio);

            ServiciosDAO dao = new ServiciosDAO();
            if (dao.actualizarServicio(servicioSeleccionado)) {
                tablaServicios.refresh();
                bloquearCamposEdicion();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido actualizar el servicio en la base de datos.");
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "El precio debe ser un número, por ejemplo: 450.00.");
        }
    }

    private Double convertirPrecio(String texto) {
        return Double.valueOf(texto.trim().replace(",", "."));
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void volverAListado() {
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);
        bloquearCamposEdicion();
    }

    private void bloquearCamposEdicion() {
        txtDetalleNombre.setEditable(false);
        txtDetalleDescripcion.setEditable(false);
        txtDetallePrecio.setEditable(false);

        String estiloLectura = "-fx-background-color: #EDE7D9; -fx-border-color: #7A5230; -fx-border-radius: 4;";
        txtDetalleNombre.setStyle(estiloLectura);
        txtDetalleDescripcion.setStyle(estiloLectura);
        txtDetallePrecio.setStyle(estiloLectura);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);
    }
}
