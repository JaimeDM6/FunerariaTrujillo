package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.ClienteDAO;
import com.tartanga.funerariatrujillo.Objects.Cliente;
import com.tartanga.funerariatrujillo.Objects.Direccion;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientesController implements Initializable {

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
    private TextField txtDetalleDireccion;

    @FXML
    private Label lblEstadoTabla;
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

    private ObservableList<Cliente> masterData = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;
    @FXML
    private Button btnEliminar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);

        btnNuevoCliente.setOnAction(e -> openForm());
        btnGuardarNuevo.setOnAction(e -> guardarNuevoCliente());
        btnEliminar.setOnAction(e -> eliminarClienteSeleccionado());
        btnCerrarFormulario.setOnAction(e -> closeForm());

        FilteredList<Cliente> filteredData = new FilteredList<>(masterData, b -> true);

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cliente -> {
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
            });

            lblEstadoTabla.setText(filteredData.size() + " clientes");
        });

        SortedList<Cliente> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaClientes.comparatorProperty());
        tablaClientes.setItems(sortedData);

        colFechaRegistro.setSortType(TableColumn.SortType.DESCENDING);
        tablaClientes.getSortOrder().add(colFechaRegistro);

        colNombre.setReorderable(false);
        colDNI.setReorderable(false);
        colCorreo.setReorderable(false);
        colDireccion.setReorderable(false);
        colTelefono.setReorderable(false);
        colFechaRegistro.setReorderable(false);

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colFechaRegistro.setCellFactory(column -> {
            return new TableCell<Cliente, Date>() {
                private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
        });

        cargarDatos();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
    }

    private void cargarDatos() {
        Task<List<Cliente>> taskObtenerClientes = new Task<List<Cliente>>() {
            @Override
            protected List<Cliente> call() throws Exception {
                ClienteDAO dao = new ClienteDAO();
                return dao.obtenerClientes();
            }
        };

        taskObtenerClientes.setOnSucceeded(event -> {
            List<Cliente> resultado = taskObtenerClientes.getValue();
            masterData.setAll(resultado);
            tablaClientes.sort();
            lblEstadoTabla.setText(masterData.size() + " clientes");
        });

        taskObtenerClientes.setOnFailed(event -> {
            System.out.println("Error al cargar datos de MongoDB");
            lblEstadoTabla.setText("Error de conexión");
        });

        Thread hiloSecundario = new Thread(taskObtenerClientes);
        hiloSecundario.setDaemon(true);
        hiloSecundario.start();
    }

    private void openForm() {
        if (panelFormularioNuevo.getTranslateX() == 0) {
            return;
        }

        panelFormularioNuevo.setVisible(true);

        Timeline slide = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(panelFormularioNuevo.translateXProperty(), 400),
                        new KeyValue(spacerLateral.prefWidthProperty(), 0),
                        new KeyValue(spacerLateral.minWidthProperty(), 0)
                ),
                new KeyFrame(Duration.millis(350),
                        new KeyValue(panelFormularioNuevo.translateXProperty(), 0),
                        new KeyValue(spacerLateral.prefWidthProperty(), 370),
                        new KeyValue(spacerLateral.minWidthProperty(), 370)
                )
        );

        slide.play();
    }

    private void closeForm() {
        Timeline slide = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(panelFormularioNuevo.translateXProperty(), 0),
                        new KeyValue(spacerLateral.prefWidthProperty(), 370),
                        new KeyValue(spacerLateral.minWidthProperty(), 370)
                ),
                new KeyFrame(Duration.millis(350),
                        new KeyValue(panelFormularioNuevo.translateXProperty(), 400),
                        new KeyValue(spacerLateral.prefWidthProperty(), 0),
                        new KeyValue(spacerLateral.minWidthProperty(), 0)
                )
        );

        slide.setOnFinished(e -> {
            panelFormularioNuevo.setVisible(false);
        });

        slide.play();
    }

    private void guardarNuevoCliente() {
        if (txtNuevoNombre.getText().trim().isEmpty() || txtNuevoApellidos.getText().trim().isEmpty()
                || txtNuevoDni.getText().trim().isEmpty() || txtNuevoTelefono.getText().trim().isEmpty()) {
            System.out.println("Faltan campos obligatorios");
            return;
        }

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(txtNuevoNombre.getText().trim());
        nuevoCliente.setApellidos(txtNuevoApellidos.getText().trim());
        nuevoCliente.setDni(txtNuevoDni.getText().trim());
        nuevoCliente.setTelefono(txtNuevoTelefono.getText().trim());
        nuevoCliente.setCorreoElectronico(txtNuevoCorreo.getText().trim());
        nuevoCliente.setFechaRegistro(new Date());

        Direccion dir = new Direccion();
        dir.setCalle(txtNuevoCalle.getText().trim());
        dir.setCiudad(txtNuevoCiudad.getText().trim());

        try {
            dir.setCodigoPostal(txtNuevoCP.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtNuevoCP.getText().trim()));
        } catch (NumberFormatException ex) {
            dir.setCodigoPostal(0);
        }
        nuevoCliente.setDireccion(dir);

        ClienteDAO dao = new ClienteDAO();
        if (dao.insertarCliente(nuevoCliente)) {
            masterData.add(nuevoCliente);
            tablaClientes.sort();
            lblEstadoTabla.setText(masterData.size() + " clientes");

            limpiarFormularioNuevo();
            closeForm();
        }
    }

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

    private void eliminarClienteSeleccionado() {
        if (clienteSeleccionado != null) {
            ClienteDAO dao = new ClienteDAO();

            if (dao.eliminarCliente(clienteSeleccionado.getId())) {
                masterData.remove(clienteSeleccionado);
                lblEstadoTabla.setText(masterData.size() + " clientes");

                vistaDetalles.setVisible(false);
                vistaDetalles.setManaged(false);
                vistaTabla.setVisible(true);
                vistaTabla.setManaged(true);

                clienteSeleccionado = null;
            }
        }
    }

    private void configurarSeleccionDeTabla() {
        tablaClientes.setOnMouseClicked(event -> {
            Cliente newSelection = tablaClientes.getSelectionModel().getSelectedItem();

            if (newSelection != null) {
                clienteSeleccionado = newSelection;

                try {
                    txtDetalleNombre.setText(newSelection.getNombre());
                    txtDetalleApellidos.setText(newSelection.getApellidos());
                    txtDetalleDni.setText(newSelection.getDni());
                    txtDetalleTelefono.setText(newSelection.getTelefono());

                    txtDetalleCorreo.setText(newSelection.getCorreoElectronico());

                    if (newSelection.getDireccion() != null) {
                        txtDetalleDireccion.setText(newSelection.getDireccion().toString());
                    } else {
                        txtDetalleDireccion.setText("Sin dirección");
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
                    ex.printStackTrace();
                }
            }
        });

        btnVolver.setOnAction(e -> {
            vistaDetalles.setVisible(false);
            vistaDetalles.setManaged(false);

            vistaTabla.setVisible(true);
            vistaTabla.setManaged(true);

            bloquearCamposEdicion();
        });
    }

    private void configurarModoEdicion() {
        btnEditar.setOnAction(e -> {
            txtDetalleNombre.setEditable(true);
            txtDetalleApellidos.setEditable(true);
            txtDetalleDni.setEditable(true);
            txtDetalleTelefono.setEditable(true);
            txtDetalleCorreo.setEditable(true);
            txtDetalleDireccion.setEditable(true);

            String estiloEditable = "-fx-background-color: #FFFFFF; -fx-border-color: #C68E17; -fx-border-radius: 4;";
            txtDetalleNombre.setStyle(estiloEditable);
            txtDetalleApellidos.setStyle(estiloEditable);
            txtDetalleDni.setStyle(estiloEditable);
            txtDetalleTelefono.setStyle(estiloEditable);
            txtDetalleCorreo.setStyle(estiloEditable);
            txtDetalleDireccion.setStyle(estiloEditable);

            btnEditar.setVisible(false);
            btnEditar.setManaged(false);
            btnEliminar.setVisible(false);
            btnEliminar.setManaged(false);
            btnGuardarEdicion.setVisible(true);
            btnGuardarEdicion.setManaged(true);
        });

        btnGuardarEdicion.setOnAction(e -> {
            if (clienteSeleccionado != null) {
                clienteSeleccionado.setNombre(txtDetalleNombre.getText());
                clienteSeleccionado.setApellidos(txtDetalleApellidos.getText());
                clienteSeleccionado.setDni(txtDetalleDni.getText());
                clienteSeleccionado.setTelefono(txtDetalleTelefono.getText());
                clienteSeleccionado.setCorreoElectronico(txtDetalleCorreo.getText());

                ClienteDAO dao = new ClienteDAO();
                dao.actualizarCliente(clienteSeleccionado);

                tablaClientes.refresh();
            }

            bloquearCamposEdicion();
        });
    }

    private void bloquearCamposEdicion() {
        txtDetalleNombre.setEditable(false);
        txtDetalleApellidos.setEditable(false);
        txtDetalleDni.setEditable(false);
        txtDetalleTelefono.setEditable(false);
        txtDetalleCorreo.setEditable(false);
        txtDetalleDireccion.setEditable(false);

        String estiloLectura = "-fx-background-color: #EDE7D9; -fx-border-color: #7A5230; -fx-border-radius: 4;";
        txtDetalleNombre.setStyle(estiloLectura);
        txtDetalleApellidos.setStyle(estiloLectura);
        txtDetalleDni.setStyle(estiloLectura);
        txtDetalleTelefono.setStyle(estiloLectura);
        txtDetalleCorreo.setStyle(estiloLectura);
        txtDetalleDireccion.setStyle(estiloLectura);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);
    }
}
