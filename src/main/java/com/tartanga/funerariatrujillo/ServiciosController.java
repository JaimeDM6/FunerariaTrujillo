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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    private final ServiciosDAO serviciosDAO;

    public ServiciosController() {
        this.serviciosDAO = new ServiciosDAO();
    }

    public ServiciosController(ServiciosDAO serviciosDAO) {
        this.serviciosDAO = serviciosDAO;
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
    private TableView<Servicios> tablaServicios;

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
    private Button btnGuardarNuevo;
    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtBuscar;
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

    private final ObservableList<Servicios> masterData = FXCollections.observableArrayList();
    private Servicios servicioSeleccionado;
    private FilteredList<Servicios> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);

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

        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarServicioSeleccionado();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        filteredData = new FilteredList<>(masterData, new Predicate<Servicios>() {
            @Override
            public boolean test(Servicios servicio) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Servicios>() {
                    @Override
                    public boolean test(Servicios servicio) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        String id = servicio.getId() != null ? servicio.getId() : "";
                        String nombre = servicio.getNombre() != null ? servicio.getNombre() : "";
                        String descripcion = servicio.getDescripcion() != null ? servicio.getDescripcion() : "";

                        return id.toLowerCase().contains(lowerCaseFilter)
                                || nombre.toLowerCase().contains(lowerCaseFilter)
                                || descripcion.toLowerCase().contains(lowerCaseFilter);
                    }
                });
                actualizarEstadoBusqueda(newValue);
            }
        });

        SortedList<Servicios> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaServicios.comparatorProperty());
        tablaServicios.setItems(sortedData);

        if (colID != null) {
            colID.setReorderable(false);
            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            Utils.columnID(colID);
        }
        colNombre.setReorderable(false);
        colDescripcion.setReorderable(false);
        colPrecio.setReorderable(false);

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        configurarColumnaPrecio();

        tablaServicios.setPlaceholder(new Label("Cargando servicios..."));
        lblEstadoTabla.setText("Cargando servicios...");

        cargarDatos();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
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

    private void actualizarEstadoBusqueda(String textoBusqueda) {
        if (filteredData.size() > 0) {
            lblEstadoTabla.setText(filteredData.size() + " servicios");
            tablaServicios.setPlaceholder(new Label("No hay servicios"));
        } else if (masterData.isEmpty()) {
            lblEstadoTabla.setText("0 servicios");
            tablaServicios.setPlaceholder(new Label("No hay servicios"));
        } else if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            lblEstadoTabla.setText("0 servicios");
            tablaServicios.setPlaceholder(new Label("No hay servicios"));
        } else {
            lblEstadoTabla.setText("Servicio no encontrado");
            tablaServicios.setPlaceholder(new Label("Servicio no encontrado"));
        }
    }

    private void cargarDatos() {
        Task<List<Servicios>> taskObtenerServicios = new Task<List<Servicios>>() {
            @Override
            protected List<Servicios> call() throws Exception {
                return serviciosDAO.obtenerServicios();
            }
        };

        taskObtenerServicios.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                masterData.setAll(taskObtenerServicios.getValue());
                tablaServicios.sort();
                actualizarEstadoBusqueda(txtBuscar.getText());
            }
        });

        taskObtenerServicios.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de conexión");
                alert.setHeaderText(null);
                alert.setContentText("No se han podido cargar los servicios desde la base de datos.");
                alert.showAndWait();
            }
        });

        Thread hilo = new Thread(taskObtenerServicios);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void guardarNuevoServicio() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevaDescripcion.getStyleClass().remove("error");
        txtNuevoPrecio.getStyleClass().remove("error");

        boolean hayError = false;

        if (txtNuevoNombre.getText().isBlank()) {
            txtNuevoNombre.getStyleClass().add("error");
            hayError = true;
        }

        if (txtNuevaDescripcion.getText().isBlank()) {
            txtNuevaDescripcion.getStyleClass().add("error");
            hayError = true;
        }

        if (txtNuevoPrecio.getText().isBlank()) {
            txtNuevoPrecio.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Faltan datos");
            alert.setHeaderText(null);
            alert.setContentText("Todos los campos son obligatorios.");
            alert.showAndWait();
            return;
        }

        try {
            Double precio = convertirPrecio(txtNuevoPrecio.getText());
            if (precio < 0) {
                txtNuevoPrecio.getStyleClass().add("error");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Precio incorrecto");
                alert.setHeaderText(null);
                alert.setContentText("El precio no puede ser negativo.");
                alert.showAndWait();
                return;
            }

            Servicios nuevoServicio = new Servicios();
            nuevoServicio.setNombre(txtNuevoNombre.getText().trim());
            nuevoServicio.setDescripcion(txtNuevaDescripcion.getText().trim());
            nuevoServicio.setPrecio(precio);

            if (serviciosDAO.insertarServicio(nuevoServicio)) {
                masterData.add(nuevoServicio);
                tablaServicios.sort();
                actualizarEstadoBusqueda(txtBuscar.getText());

                limpiarFormularioNuevo();
                closeForm();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al guardar el servicio en la base de datos.");
                alert.showAndWait();
            }
        } catch (NumberFormatException ex) {
            txtNuevoPrecio.getStyleClass().add("error");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Precio incorrecto");
            alert.setHeaderText(null);
            alert.setContentText("El precio debe ser un número válido, por ejemplo: 450.00.");
            alert.showAndWait();
        }
    }

    private Double convertirPrecio(String texto) {
        return Double.valueOf(texto.trim().replace(",", "."));
    }

    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevaDescripcion.clear();
        txtNuevoPrecio.clear();
    }

    private void eliminarServicioSeleccionado() {
        if (servicioSeleccionado != null) {
            if (serviciosDAO.eliminarServicio(servicioSeleccionado.getId())) {
                masterData.remove(servicioSeleccionado);
                actualizarEstadoBusqueda(txtBuscar.getText());

                vistaDetalles.setVisible(false);
                vistaDetalles.setManaged(false);
                vistaTabla.setVisible(true);
                vistaTabla.setManaged(true);

                servicioSeleccionado = null;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al eliminar el servicio de la base de datos.");
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
        tablaServicios.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Servicios newSelection = tablaServicios.getSelectionModel().getSelectedItem();

                if (newSelection != null) {
                    servicioSeleccionado = newSelection;

                    try {
                        txtDetalleNombre.setText(newSelection.getNombre() != null ? newSelection.getNombre() : "");
                        txtDetalleDescripcion
                                .setText(newSelection.getDescripcion() != null ? newSelection.getDescripcion() : "");
                        txtDetallePrecio.setText(
                                newSelection.getPrecio() != null ? String.valueOf(newSelection.getPrecio()) : "");

                        panelFormularioNuevo.setTranslateX(400);
                        panelFormularioNuevo.setVisible(false);
                        spacerLateral.setPrefWidth(0);
                        spacerLateral.setMinWidth(0);
                        vistaTabla.setVisible(false);
                        vistaTabla.setManaged(false);
                        vistaDetalles.setVisible(true);
                        vistaDetalles.setManaged(true);
                        tablaServicios.getSelectionModel().clearSelection();

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
                txtDetalleDescripcion.getStyleClass().remove("error");
                txtDetallePrecio.getStyleClass().remove("error");

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
                txtDetalleDescripcion.getStyleClass().remove("error");
                txtDetallePrecio.getStyleClass().remove("error");

                boolean hayError = false;

                if (txtDetalleNombre.getText().isBlank()) {
                    txtDetalleNombre.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleDescripcion.getText().isBlank()) {
                    txtDetalleDescripcion.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetallePrecio.getText().isBlank()) {
                    txtDetallePrecio.getStyleClass().add("error");
                    hayError = true;
                }

                if (hayError) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Datos incompletos");
                    alert.setHeaderText(null);
                    alert.setContentText("No puedes dejar en blanco los campos obligatorios.");
                    alert.showAndWait();
                    return;
                }

                try {
                    Double precio = convertirPrecio(txtDetallePrecio.getText());
                    if (precio < 0) {
                        txtDetallePrecio.getStyleClass().add("error");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Precio incorrecto");
                        alert.setHeaderText(null);
                        alert.setContentText("El precio no puede ser negativo.");
                        alert.showAndWait();
                        return;
                    }

                    if (servicioSeleccionado != null) {
                        servicioSeleccionado.setNombre(txtDetalleNombre.getText().trim());
                        servicioSeleccionado.setDescripcion(txtDetalleDescripcion.getText().trim());
                        servicioSeleccionado.setPrecio(precio);

                        if (serviciosDAO.actualizarServicio(servicioSeleccionado)) {
                            tablaServicios.refresh();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Hubo un error al actualizar los datos en la base de datos.");
                            alert.showAndWait();
                        }
                    }

                    bloquearCamposEdicion();
                } catch (NumberFormatException ex) {
                    txtDetallePrecio.getStyleClass().add("error");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Precio incorrecto");
                    alert.setHeaderText(null);
                    alert.setContentText("El precio debe ser un número válido, por ejemplo: 450.00.");
                    alert.showAndWait();
                }
            }
        });
    }

    private void cambiarEstadoCamposEdicion(boolean editable) {
        txtDetalleNombre.setEditable(editable);
        txtDetalleDescripcion.setEditable(editable);
        txtDetallePrecio.setEditable(editable);

        String estilo;
        if (editable) {
            estilo = "-fx-background-color: #FFFFFF; -fx-border-color: #C68E17; -fx-border-radius: 4;";
        } else {
            estilo = "-fx-background-color: #EDE7D9; -fx-border-color: #7A5230; -fx-border-radius: 4;";
        }

        txtDetalleNombre.setStyle(estilo);
        txtDetalleDescripcion.setStyle(estilo);
        txtDetallePrecio.setStyle(estilo);
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
