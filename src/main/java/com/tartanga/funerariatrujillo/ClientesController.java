package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.ClienteDAO;
import com.tartanga.funerariatrujillo.Objects.Cliente;
import com.tartanga.funerariatrujillo.Objects.Direccion;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Controlador de la vista de clientes. Gestiona el listado con búsqueda, el alta, la edición y la
 * eliminación de clientes.
 *
 * @author Jaime
 */
public class ClientesController implements Initializable {

    private final ClienteDAO clienteDAO;

    /**
     * Crea el controlador con el ClienteDAO por defecto.
     */
    public ClientesController() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Crea el controlador con el DAO indicado. Permite sustituir el acceso a la base de datos, por
     * ejemplo en las pruebas.
     *
     * @param clienteDAO DAO que se utilizará para acceder a la base de datos
     */
    public ClientesController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
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
    private TableView<Cliente> tablaClientes;

    @FXML
    private Button btnNuevoCliente;
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
    private TextField txtNuevoDni;
    @FXML
    private TextField txtNuevoTelefono;
    @FXML
    private TextField txtNuevoCorreo;
    @FXML
    private TextField txtNuevoCalle;
    @FXML
    private TextField txtNuevoCiudad;
    @FXML
    private TextField txtNuevoCP;

    @FXML
    private TextField txtDetalleNombre;
    @FXML
    private TextField txtDetalleApellidos;
    @FXML
    private TextField txtDetalleDni;
    @FXML
    private TextField txtDetalleTelefono;
    @FXML
    private TextField txtDetalleCorreo;
    @FXML
    private TextField txtDetalleCalle;
    @FXML
    private TextField txtDetalleCiudad;
    @FXML
    private TextField txtDetalleCP;

    @FXML
    private Label lblEstadoTabla;
    @FXML
    private TableColumn<Cliente, String> colID;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colDNI;
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, Date> colFechaRegistro;

    private final ObservableList<Cliente> masterData = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    /**
     * Inicializa la vista de clientes: configura la tabla, el buscador y los formularios de alta y de
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

        btnNuevoCliente.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openForm();
            }
        });

        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevoCliente();
            }
        });

        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarClienteSeleccionado();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        FilteredList<Cliente> filteredData = new FilteredList<>(masterData, new Predicate<Cliente>() {
            @Override
            public boolean test(Cliente cliente) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Cliente>() {
                    @Override
                    public boolean test(Cliente cliente) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        if (cliente.getNombreCompleto().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else if (cliente.getDni().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else if (String.valueOf(cliente.getTelefono()).toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                    }
                });
                lblEstadoTabla.setText(filteredData.size() + " clientes");
            }
        });

        SortedList<Cliente> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaClientes.comparatorProperty());
        tablaClientes.setItems(sortedData);

        colFechaRegistro.setSortType(TableColumn.SortType.DESCENDING);
        tablaClientes.getSortOrder().add(colFechaRegistro);

        colID.setReorderable(false);
        colNombre.setReorderable(false);
        colDNI.setReorderable(false);
        colCorreo.setReorderable(false);
        colDireccion.setReorderable(false);
        colTelefono.setReorderable(false);
        colFechaRegistro.setReorderable(false);

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo_electronico"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fecha_registro"));

        Utils.columnID(colID);
        Utils.columnDate(colFechaRegistro);

        cargarDatos();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
    }

    /**
     * Carga los clientes desde la base de datos en un hilo en segundo plano y actualiza la tabla al
     * terminar. Si la carga falla, avisa al usuario del error de conexión.
     */
    private void cargarDatos() {
        Task<List<Cliente>> taskObtenerClientes = new Task<List<Cliente>>() {
            @Override
            protected List<Cliente> call() throws Exception {
                return clienteDAO.obtenerClientes();
            }
        };

        taskObtenerClientes.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                List<Cliente> resultado = taskObtenerClientes.getValue();
                masterData.setAll(resultado);
                tablaClientes.sort();
                lblEstadoTabla.setText(masterData.size() + " clientes");
            }
        });

        taskObtenerClientes.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de conexión");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo cargar la lista de clientes desde la base de datos.");
                alert.showAndWait();
            }
        });

        Thread hilo = new Thread(taskObtenerClientes);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Valida el formulario de alta y, si los datos son correctos, inserta el cliente en la base de
     * datos y recarga la tabla. Si no lo son, muestra un aviso con el motivo.
     */
    private void guardarNuevoCliente() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevoApellidos.getStyleClass().remove("error");
        txtNuevoDni.getStyleClass().remove("error");
        txtNuevoTelefono.getStyleClass().remove("error");
        txtNuevoCorreo.getStyleClass().remove("error");
        txtNuevoCalle.getStyleClass().remove("error");
        txtNuevoCiudad.getStyleClass().remove("error");
        txtNuevoCP.getStyleClass().remove("error");

        String mensaje = validarNuevoCliente();

        if (mensaje == null) {
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombre(txtNuevoNombre.getText().trim());
            nuevoCliente.setApellidos(txtNuevoApellidos.getText().trim());
            nuevoCliente.setDni(txtNuevoDni.getText().trim().toUpperCase());
            nuevoCliente.setTelefono(txtNuevoTelefono.getText().trim());
            nuevoCliente.setCorreo_electronico(txtNuevoCorreo.getText().trim());
            nuevoCliente.setFecha_registro(new Date());

            Direccion dir = new Direccion();
            dir.setCalle(txtNuevoCalle.getText().trim());
            dir.setCiudad(txtNuevoCiudad.getText().trim());
            dir.setCodigoPostal(Integer.parseInt(txtNuevoCP.getText().trim()));
            nuevoCliente.setDireccion(dir);

            if (clienteDAO.insertarCliente(nuevoCliente)) {
                masterData.add(nuevoCliente);
                tablaClientes.sort();
                lblEstadoTabla.setText(masterData.size() + " clientes");

                limpiarFormularioNuevo();
                closeForm();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al guardar el cliente en la base de datos.");
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
     * Comprueba los campos del formulario de alta de cliente y marca como erróneos los que no son
     * válidos.
     *
     * @return mensaje con el primer error encontrado, o {@code null} si todos los datos son válidos
     */
    private String validarNuevoCliente() {
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
        if (txtNuevoDni.getText().isBlank()) {
            txtNuevoDni.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoTelefono.getText().isBlank()) {
            txtNuevoTelefono.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCorreo.getText().isBlank()) {
            txtNuevoCorreo.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCalle.getText().isBlank()) {
            txtNuevoCalle.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCiudad.getText().isBlank()) {
            txtNuevoCiudad.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCP.getText().isBlank()) {
            txtNuevoCP.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje = "Todos los campos son obligatorios.";
        } else if (!Utils.dniValido(txtNuevoDni.getText().trim())) {
            txtNuevoDni.getStyleClass().add("error");
            mensaje = "El DNI debe tener 8 números y una letra (ej. 12345678A).";
        } else if (!Utils.correoValido(txtNuevoCorreo.getText().trim())) {
            txtNuevoCorreo.getStyleClass().add("error");
            mensaje = "El correo electrónico no tiene un formato válido.";
        } else if (!Utils.telefonoValido(txtNuevoTelefono.getText().trim())) {
            txtNuevoTelefono.getStyleClass().add("error");
            mensaje = "El teléfono debe tener 9 números.";
        } else if (!Utils.cpValido(txtNuevoCP.getText().trim())) {
            txtNuevoCP.getStyleClass().add("error");
            mensaje = "El código postal debe tener 5 números.";
        }

        return mensaje;
    }

    /**
     * Vacía los campos del formulario de alta.
     */
    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevoApellidos.clear();
        txtNuevoDni.clear();
        txtNuevoTelefono.clear();
        txtNuevoCorreo.clear();
        txtNuevoCalle.clear();
        txtNuevoCiudad.clear();
        txtNuevoCP.clear();
    }

    /**
     * Elimina de la base de datos el cliente seleccionado en la tabla y recarga los datos.
     */
    private void eliminarClienteSeleccionado() {
        if (clienteSeleccionado != null) {
            if (clienteDAO.eliminarCliente(clienteSeleccionado.getId())) {
                masterData.remove(clienteSeleccionado);
                lblEstadoTabla.setText(masterData.size() + " clientes");

                vistaDetalles.setVisible(false);
                vistaDetalles.setManaged(false);
                vistaTabla.setVisible(true);
                vistaTabla.setManaged(true);

                clienteSeleccionado = null;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al eliminar el cliente de la base de datos.");
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
     * Configura la selección de la tabla: al elegir un cliente se muestran sus datos en el panel de
     * detalle.
     */
    private void configurarSeleccionDeTabla() {
        tablaClientes.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Cliente newSelection = tablaClientes.getSelectionModel().getSelectedItem();

                if (newSelection != null) {
                    clienteSeleccionado = newSelection;

                    try {
                        txtDetalleNombre.setText(newSelection.getNombre());
                        txtDetalleApellidos.setText(newSelection.getApellidos());
                        txtDetalleDni.setText(newSelection.getDni());
                        txtDetalleTelefono.setText(newSelection.getTelefono());
                        txtDetalleCorreo.setText(newSelection.getCorreo_electronico());

                        if (newSelection.getDireccion() != null) {
                            txtDetalleCalle.setText(newSelection.getDireccion().getCalle() != null
                                    ? newSelection.getDireccion().getCalle()
                                    : "");
                            txtDetalleCiudad.setText(newSelection.getDireccion().getCiudad() != null
                                    ? newSelection.getDireccion().getCiudad()
                                    : "");
                            txtDetalleCP.setText(newSelection.getDireccion().getCodigoPostal() == 0 ? ""
                                    : String.valueOf(newSelection.getDireccion().getCodigoPostal()));
                        } else {
                            txtDetalleCalle.setText("");
                            txtDetalleCiudad.setText("");
                            txtDetalleCP.setText("");
                        }

                        panelFormularioNuevo.setTranslateX(400);
                        panelFormularioNuevo.setVisible(false);
                        spacerLateral.setPrefWidth(0);
                        spacerLateral.setMinWidth(0);
                        vistaTabla.setVisible(false);
                        vistaTabla.setManaged(false);
                        vistaDetalles.setVisible(true);
                        vistaDetalles.setManaged(true);
                        tablaClientes.getSelectionModel().clearSelection();

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
     * Configura los botones del panel de detalle para editar el cliente seleccionado y guardar o
     * descartar los cambios.
     */
    private void configurarModoEdicion() {
        btnEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                txtDetalleNombre.setEditable(true);
                txtDetalleApellidos.setEditable(true);
                txtDetalleDni.setEditable(true);
                txtDetalleTelefono.setEditable(true);
                txtDetalleCorreo.setEditable(true);
                txtDetalleCalle.setEditable(true);
                txtDetalleCiudad.setEditable(true);
                txtDetalleCP.setEditable(true);

                txtDetalleNombre.getStyleClass().remove("error");
                txtDetalleApellidos.getStyleClass().remove("error");
                txtDetalleDni.getStyleClass().remove("error");
                txtDetalleTelefono.getStyleClass().remove("error");
                txtDetalleCalle.getStyleClass().remove("error");
                txtDetalleCiudad.getStyleClass().remove("error");
                txtDetalleCP.getStyleClass().remove("error");

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
                txtDetalleDni.getStyleClass().remove("error");
                txtDetalleTelefono.getStyleClass().remove("error");
                txtDetalleCorreo.getStyleClass().remove("error");
                txtDetalleCalle.getStyleClass().remove("error");
                txtDetalleCiudad.getStyleClass().remove("error");
                txtDetalleCP.getStyleClass().remove("error");

                boolean hayError = false;

                if (txtDetalleNombre.getText().isBlank()) {
                    txtDetalleNombre.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleApellidos.getText().isBlank()) {
                    txtDetalleApellidos.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleDni.getText().isBlank()) {
                    txtDetalleDni.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleTelefono.getText().isBlank()) {
                    txtDetalleTelefono.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleCorreo.getText().isBlank()) {
                    txtDetalleCorreo.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleCalle.getText().isBlank()) {
                    txtDetalleCalle.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleCiudad.getText().isBlank()) {
                    txtDetalleCiudad.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleCP.getText().isBlank()) {
                    txtDetalleCP.getStyleClass().add("error");
                    hayError = true;
                }

                if (hayError) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Datos incompletos");
                    alert.setHeaderText(null);
                    alert.setContentText("No puedes dejar en blanco los campos.");
                    alert.showAndWait();
                    return;
                }

                if (clienteSeleccionado != null) {
                    clienteSeleccionado.setNombre(txtDetalleNombre.getText());
                    clienteSeleccionado.setApellidos(txtDetalleApellidos.getText());
                    clienteSeleccionado.setDni(txtDetalleDni.getText());
                    clienteSeleccionado.setTelefono(txtDetalleTelefono.getText());
                    clienteSeleccionado.setCorreo_electronico(txtDetalleCorreo.getText());

                    Direccion dir = clienteSeleccionado.getDireccion();
                    if (dir == null) {
                        dir = new Direccion();
                    }
                    dir.setCalle(txtDetalleCalle.getText().trim());
                    dir.setCiudad(txtDetalleCiudad.getText().trim());
                    try {
                        dir.setCodigoPostal(txtDetalleCP.getText().trim().isEmpty() ? 0
                                : Integer.parseInt(txtDetalleCP.getText().trim()));
                    } catch (NumberFormatException ex) {
                        dir.setCodigoPostal(0);
                    }
                    clienteSeleccionado.setDireccion(dir);

                    if (clienteDAO.actualizarCliente(clienteSeleccionado)) {
                        tablaClientes.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Hubo un error al actualizar los datos en la base de datos.");
                        alert.showAndWait();
                    }
                }

                bloquearCamposEdicion();
            }
        });
    }

    /**
     * Deja los campos del panel de detalle en modo solo lectura.
     */
    private void bloquearCamposEdicion() {
        txtDetalleNombre.setEditable(false);
        txtDetalleApellidos.setEditable(false);
        txtDetalleDni.setEditable(false);
        txtDetalleTelefono.setEditable(false);
        txtDetalleCorreo.setEditable(false);
        txtDetalleCalle.setEditable(false);
        txtDetalleCiudad.setEditable(false);
        txtDetalleCP.setEditable(false);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);
    }
}