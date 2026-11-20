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

/**
 * Controlador de la vista de servicios. Gestiona el listado con búsqueda, el alta, la edición y la
 * eliminación de los servicios que ofrece la funeraria.
 *
 * @author Iker
 */
public class ServiciosController implements Initializable {

    private final ServiciosDAO serviciosDAO;

    /**
     * Crea el controlador con el ServiciosDAO por defecto.
     */
    public ServiciosController() {
        this.serviciosDAO = new ServiciosDAO();
    }

    /**
     * Crea el controlador con el DAO indicado. Permite sustituir el acceso a la base de datos, por
     * ejemplo en las pruebas.
     *
     * @param serviciosDAO DAO que se utilizará para acceder a la base de datos
     */
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

    /**
     * Inicializa la vista de servicios: configura la tabla, el buscador y los formularios de alta y de
     * detalle, y carga los datos de la base de datos.
     *
     * @param location ubicación del documento FXML; no se utiliza
     * @param resources recursos de internacionalización; no se utilizan
     */
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
                        boolean coincide = true;

                        if (newValue != null && !newValue.isEmpty()) {
                            String lowerCaseFilter = newValue.toLowerCase();
                            String id = textoSinNulos(servicio.getId());
                            String nombre = textoSinNulos(servicio.getNombre());
                            String descripcion = textoSinNulos(servicio.getDescripcion());

                            coincide = id.toLowerCase().contains(lowerCaseFilter)
                                    || nombre.toLowerCase().contains(lowerCaseFilter)
                                    || descripcion.toLowerCase().contains(lowerCaseFilter);
                        }

                        return coincide;
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

    /**
     * Configura la columna de precio para mostrar los importes con dos decimales y el símbolo del
     * euro.
     */
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

    /**
     * Actualiza el contador y el texto de la tabla según el resultado de la búsqueda.
     *
     * @param textoBusqueda texto escrito en el buscador, que puede ser nulo
     */
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

    /**
     * Carga los servicios desde la base de datos en un hilo en segundo plano y actualiza la tabla al
     * terminar. Si la carga falla, avisa al usuario del error de conexión.
     */
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

    /**
     * Valida el formulario de alta y, si los datos son correctos, inserta el servicio en la base de
     * datos y recarga la tabla. Si no lo son, muestra un aviso con el motivo.
     */
    private void guardarNuevoServicio() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevaDescripcion.getStyleClass().remove("error");
        txtNuevoPrecio.getStyleClass().remove("error");

        String mensaje = validarNuevoServicio();

        if (mensaje == null) {
            Servicios nuevoServicio = new Servicios();
            nuevoServicio.setNombre(txtNuevoNombre.getText().trim());
            nuevoServicio.setDescripcion(txtNuevaDescripcion.getText().trim());
            nuevoServicio.setPrecio(convertirPrecio(txtNuevoPrecio.getText()));

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
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos no válidos");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
    }

    /**
     * Comprueba los campos del formulario de alta de servicio y marca como erróneos los que no son
     * válidos.
     *
     * @return mensaje con el primer error encontrado, o {@code null} si todos los datos son válidos
     */
    private String validarNuevoServicio() {
        String mensaje = null;
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
            mensaje = "Todos los campos son obligatorios.";
        } else {
            mensaje = validarPrecio(txtNuevoPrecio);

            if (mensaje == null && serviciosDAO.existeServicio(txtNuevoNombre.getText().trim(), null)) {
                txtNuevoNombre.getStyleClass().add("error");
                mensaje = "Ya existe un servicio con ese nombre.";
            }
        }

        return mensaje;
    }

    /**
     * Comprueba los campos del panel de detalle antes de guardar la edición del servicio.
     *
     * @return mensaje con el primer error encontrado, o {@code null} si todos los datos son válidos
     */
    private String validarDetalleServicio() {
        String mensaje = null;
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
            mensaje = "No puedes dejar en blanco los campos obligatorios.";
        } else {
            mensaje = validarPrecio(txtDetallePrecio);

            String idActual = null;
            if (servicioSeleccionado != null) {
                idActual = servicioSeleccionado.getId();
            }

            if (mensaje == null && serviciosDAO.existeServicio(txtDetalleNombre.getText().trim(), idActual)) {
                txtDetalleNombre.getStyleClass().add("error");
                mensaje = "Ya existe un servicio con ese nombre.";
            }
        }

        return mensaje;
    }

    /**
     * Comprueba que el precio sea un número válido y no negativo, y marca el campo como erróneo si no
     * lo es.
     *
     * @param campoPrecio campo de texto que contiene el precio
     * @return mensaje de error, o {@code null} si el precio es válido
     */
    private String validarPrecio(TextField campoPrecio) {
        String mensaje = null;

        try {
            Double precio = convertirPrecio(campoPrecio.getText());
            if (precio < 0) {
                campoPrecio.getStyleClass().add("error");
                mensaje = "El precio no puede ser negativo.";
            }
        } catch (NumberFormatException ex) {
            campoPrecio.getStyleClass().add("error");
            mensaje = "El precio debe ser un número válido, por ejemplo: 450.00.";
        }

        return mensaje;
    }

    /**
     * Convierte un texto nulo en una cadena vacía.
     *
     * @param texto texto original, que puede ser nulo
     * @return el mismo texto, o una cadena vacía si era nulo
     */
    private String textoSinNulos(String texto) {
        String resultado = "";
        if (texto != null) {
            resultado = texto;
        }
        return resultado;
    }

    /**
     * Convierte un texto en un precio, admitiendo la coma como separador decimal.
     *
     * @param texto texto con el precio
     * @return precio como número
     * @throws NumberFormatException si el texto no es un número
     */
    private Double convertirPrecio(String texto) {
        return Double.valueOf(texto.trim().replace(",", "."));
    }

    /**
     * Vacía los campos del formulario de alta.
     */
    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevaDescripcion.clear();
        txtNuevoPrecio.clear();
    }

    /**
     * Elimina de la base de datos el servicio seleccionado en la tabla y recarga los datos.
     */
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

    /**
     * Abre el panel lateral del formulario de alta.
     */
    private void openForm() {
        Utils.openForm(panelFormularioNuevo, spacerLateral);
    }

    /**
     * Cierra el panel lateral del formulario de alta.
     */
    private void closeForm() {
        Utils.closeForm(panelFormularioNuevo, spacerLateral);
    }

    /**
     * Configura la selección de la tabla: al elegir un servicio se muestran sus datos en el panel de
     * detalle.
     */
    private void configurarSeleccionDeTabla() {
        tablaServicios.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Servicios newSelection = tablaServicios.getSelectionModel().getSelectedItem();

                if (newSelection != null) {
                    servicioSeleccionado = newSelection;

                    try {
                        String precio = "";
                        if (newSelection.getPrecio() != null) {
                            precio = String.valueOf(newSelection.getPrecio());
                        }

                        txtDetalleNombre.setText(textoSinNulos(newSelection.getNombre()));
                        txtDetalleDescripcion.setText(textoSinNulos(newSelection.getDescripcion()));
                        txtDetallePrecio.setText(precio);

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

    /**
     * Configura los botones del panel de detalle para editar el servicio seleccionado y guardar o
     * descartar los cambios.
     */
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

                String mensaje = validarDetalleServicio();

                if (mensaje == null) {
                    if (servicioSeleccionado != null) {
                        servicioSeleccionado.setNombre(txtDetalleNombre.getText().trim());
                        servicioSeleccionado.setDescripcion(txtDetalleDescripcion.getText().trim());
                        servicioSeleccionado.setPrecio(convertirPrecio(txtDetallePrecio.getText()));

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

    /**
     * Habilita o deshabilita la edición de los campos del panel de detalle.
     *
     * @param editable {@code true} para permitir la edición; {@code false} para dejar los campos en
     *         solo lectura
     */
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

    /**
     * Deja los campos del panel de detalle en modo solo lectura.
     */
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