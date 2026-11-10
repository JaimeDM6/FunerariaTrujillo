package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.TrabajadorDAO;
import com.tartanga.funerariatrujillo.Objects.Direccion;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Cargo;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Rol;
import com.tartanga.funerariatrujillo.Utils.Utils;
import org.mindrot.jbcrypt.BCrypt;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class TrabajadorController implements Initializable {

    private final TrabajadorDAO trabajadorDAO;

    public TrabajadorController() {
        this.trabajadorDAO = new TrabajadorDAO();
    }

    public TrabajadorController(TrabajadorDAO trabajadorDAO) {
        this.trabajadorDAO = trabajadorDAO;
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
    private TableView<Trabajador> tablaTrabajadores;

    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblEstadoTabla;
    @FXML
    private TableColumn<Trabajador, String> colID;
    @FXML
    private TableColumn<Trabajador, String> colNombre;
    @FXML
    private TableColumn<Trabajador, String> colDNI;
    @FXML
    private TableColumn<Trabajador, String> colCorreo;
    @FXML
    private TableColumn<Trabajador, Direccion> colDireccion;
    @FXML
    private TableColumn<Trabajador, Integer> colNumSS;
    @FXML
    private TableColumn<Trabajador, Trabajador.Cargo> colCargo;
    @FXML
    private Button btnNuevoTrabajador;
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
    private TextField txtNuevoDni;
    @FXML
    private TextField txtNuevoCorreo;
    @FXML
    private TextField txtNuevoCalle;
    @FXML
    private TextField txtNuevoCiudad;
    @FXML
    private TextField txtNuevoCP;
    @FXML
    private TextField txtNuevoSS;
    @FXML
    private TextField txtNuevoIban;
    @FXML
    private TextField txtNuevoSalario;
    @FXML
    private ComboBox<Cargo> cmbNuevoCargo;
    @FXML
    private ComboBox<String> cmbNuevaJornada;
    private TextField txtNuevoUsuario;
    @FXML
    private PasswordField txtNuevaPassword;
    @FXML
    private PasswordField txtNuevaConfirmarPassword;
    @FXML
    private ComboBox<Rol> cmbNuevoRol;

    @FXML
    private TextField txtDetalleNombre;
    @FXML
    private TextField txtDetalleApellidos;
    @FXML
    private TextField txtDetalleDni;
    @FXML
    private TextField txtDetalleCorreo;
    @FXML
    private TextField txtDetalleCalle;
    @FXML
    private TextField txtDetalleCiudad;
    @FXML
    private TextField txtDetalleCP;
    @FXML
    private TextField txtDetalleSS;
    @FXML
    private TextField txtDetalleIban;
    @FXML
    private TextField txtDetalleSalario;
    @FXML
    private ComboBox<Cargo> cmbDetalleCargo;
    @FXML
    private ComboBox<String> cmbDetalleJornada;
    @FXML
    private ComboBox<Rol> cmbDetalleRol;

    private final ObservableList<Trabajador> masterData = FXCollections.observableArrayList();
    private Trabajador trabajadorSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);

        ObservableList<String> jornadas = FXCollections.observableArrayList("Completa", "Parcial", "Nocturna");
        cmbNuevaJornada.setItems(jornadas);
        cmbDetalleJornada.setItems(jornadas);

        cmbNuevoCargo.setItems(FXCollections.observableArrayList(Cargo.values()));
        cmbDetalleCargo.setItems(FXCollections.observableArrayList(Cargo.values()));

        cmbNuevoRol.setItems(FXCollections.observableArrayList(Rol.values()));
        cmbDetalleRol.setItems(FXCollections.observableArrayList(Rol.values()));

        btnNuevoTrabajador.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openForm();
            }
        });

        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevoTrabajador();
            }
        });

        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarTrabajadorSeleccionado();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        FilteredList<Trabajador> filteredData = new FilteredList<>(masterData, new Predicate<Trabajador>() {
            @Override
            public boolean test(Trabajador trabajador) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Trabajador>() {
                    @Override
                    public boolean test(Trabajador trabajador) {
                        if (newValue == null || newValue.trim().isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        if (trabajador.getNombreCompleto().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else if (trabajador.getCargo() != null && trabajador.getCargo().toString().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                    }
                });
                lblEstadoTabla.setText(filteredData.size() + " trabajadores");
            }
        });

        SortedList<Trabajador> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaTrabajadores.comparatorProperty());
        tablaTrabajadores.setItems(sortedData);

        colID.setReorderable(false);
        colNombre.setReorderable(false);
        colDNI.setReorderable(false);
        colCorreo.setReorderable(false);
        colDireccion.setReorderable(false);
        colNumSS.setReorderable(false);
        colCargo.setReorderable(false);

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo_electronico"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colNumSS.setCellValueFactory(new PropertyValueFactory<>("numero_ss"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));

        Utils.columnID(colID);

        cargarDatos();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
    }

    private void cargarDatos() {
        Task<List<Trabajador>> taskObtenerTrabajadores = new Task<List<Trabajador>>() {
            @Override
            protected List<Trabajador> call() {
                return trabajadorDAO.obtenerTrabajadores();
            }
        };

        taskObtenerTrabajadores.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                masterData.setAll(taskObtenerTrabajadores.getValue());
                tablaTrabajadores.sort();
                lblEstadoTabla.setText(masterData.size() + " trabajadores");
            }
        });

        taskObtenerTrabajadores.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de conexión");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo cargar la lista de trabajadores desde la base de datos.");
                alert.showAndWait();
            }
        });

        Thread hilo = new Thread(taskObtenerTrabajadores);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void guardarNuevoTrabajador() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevoApellidos.getStyleClass().remove("error");
        txtNuevoDni.getStyleClass().remove("error");
        txtNuevoCorreo.getStyleClass().remove("error");
        txtNuevoCalle.getStyleClass().remove("error");
        txtNuevoCiudad.getStyleClass().remove("error");
        txtNuevoCP.getStyleClass().remove("error");
        txtNuevoSS.getStyleClass().remove("error");
        txtNuevoIban.getStyleClass().remove("error");
        txtNuevoSalario.getStyleClass().remove("error");
        txtNuevoUsuario.getStyleClass().remove("error");
        txtNuevaPassword.getStyleClass().remove("error");
        cmbNuevoCargo.getStyleClass().remove("error");
        cmbNuevaJornada.getStyleClass().remove("error");
        cmbNuevoRol.getStyleClass().remove("error");

        boolean hayError = false;

        if (txtNuevoNombre.getText().trim().isEmpty()) {
            txtNuevoNombre.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoApellidos.getText().trim().isEmpty()) {
            txtNuevoApellidos.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoDni.getText().trim().isEmpty()) {
            txtNuevoDni.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCorreo.getText().trim().isEmpty()) {
            txtNuevoCorreo.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCalle.getText().trim().isEmpty()) {
            txtNuevoCalle.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCiudad.getText().trim().isEmpty()) {
            txtNuevoCiudad.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoCP.getText().trim().isEmpty()) {
            txtNuevoCP.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoSS.getText().trim().isEmpty()) {
            txtNuevoSS.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoIban.getText().trim().isEmpty()) {
            txtNuevoIban.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoSalario.getText().trim().isEmpty()) {
            txtNuevoSalario.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevoUsuario.getText().trim().isEmpty()) {
            txtNuevoUsuario.getStyleClass().add("error");
            hayError = true;
        }
        if (txtNuevaPassword.getText().trim().isEmpty() || txtNuevaPassword.getText().trim().length() < 8) {
            txtNuevaPassword.getStyleClass().add("error");
            hayError = true;
        }
        if (cmbNuevoCargo.getSelectionModel().getSelectedIndex() == -1) {
            cmbNuevoCargo.getStyleClass().add("error");
            hayError = true;
        }
        if (cmbNuevaJornada.getSelectionModel().getSelectedIndex() == -1) {
            cmbNuevaJornada.getStyleClass().add("error");
            hayError = true;
        }
        if (cmbNuevoRol.getSelectionModel().getSelectedIndex() == -1) {
            cmbNuevoRol.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Faltan datos");
            alert.setHeaderText(null);
            alert.setContentText("Todos los campos son obligatorios y la contraseña debe tener al menos 8 caracteres.");
            alert.showAndWait();
            return;
        }

        Trabajador nuevoTrabajador = new Trabajador();
        nuevoTrabajador.setNombre(txtNuevoNombre.getText().trim());
        nuevoTrabajador.setApellidos(txtNuevoApellidos.getText().trim());
        nuevoTrabajador.setDni(txtNuevoDni.getText().trim());
        nuevoTrabajador.setCorreo_electronico(txtNuevoCorreo.getText().trim());
        nuevoTrabajador.setNumero_ss(txtNuevoSS.getText().trim());
        nuevoTrabajador.setIban(txtNuevoIban.getText().trim());
        nuevoTrabajador.setUsuario(txtNuevoUsuario.getText().trim());

        String hashPass = BCrypt.hashpw(txtNuevaPassword.getText(), BCrypt.gensalt(12));
        nuevoTrabajador.setPassword(hashPass);

        try {
            nuevoTrabajador.setSalario(Double.parseDouble(txtNuevoSalario.getText().trim()));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Formato incorrecto");
            alert.setHeaderText(null);
            alert.setContentText("El salario debe ser un valor numérico.");
            alert.showAndWait();
            return;
        }

        nuevoTrabajador.setCargo(cmbNuevoCargo.getSelectionModel().getSelectedItem());
        nuevoTrabajador.setTipo_jornada(cmbNuevaJornada.getSelectionModel().getSelectedItem());
        nuevoTrabajador.setRol(cmbNuevoRol.getSelectionModel().getSelectedItem());

        Direccion dir = new Direccion();
        dir.setCalle(txtNuevoCalle.getText().trim());
        dir.setCiudad(txtNuevoCiudad.getText().trim());
        try {
            dir.setCodigoPostal(txtNuevoCP.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtNuevoCP.getText().trim()));
        } catch (NumberFormatException ex) {
            dir.setCodigoPostal(0);
        }
        nuevoTrabajador.setDireccion(dir);

        if (trabajadorDAO.insertarTrabajador(nuevoTrabajador)) {
            masterData.add(nuevoTrabajador);
            tablaTrabajadores.sort();
            lblEstadoTabla.setText(masterData.size() + " trabajadores");

            limpiarFormularioNuevo();
            closeForm();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Hubo un problema al guardar el trabajador en la base de datos.");
            alert.showAndWait();
        }
    }

    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevoApellidos.clear();
        txtNuevoDni.clear();
        txtNuevoCorreo.clear();
        txtNuevoCalle.clear();
        txtNuevoCiudad.clear();
        txtNuevoCP.clear();
        txtNuevoSS.clear();
        txtNuevoIban.clear();
        txtNuevoSalario.clear();
        txtNuevoUsuario.clear();
        txtNuevaPassword.clear();
        cmbNuevoCargo.getSelectionModel().clearSelection();
        cmbNuevaJornada.getSelectionModel().clearSelection();
        cmbNuevoRol.getSelectionModel().clearSelection();
    }

    private void eliminarTrabajadorSeleccionado() {
        if (trabajadorSeleccionado != null) {
            if (trabajadorDAO.eliminarTrabajador(trabajadorSeleccionado.getId())) {
                masterData.remove(trabajadorSeleccionado);
                lblEstadoTabla.setText(masterData.size() + " trabajadores");

                vistaDetalles.setVisible(false);
                vistaDetalles.setManaged(false);
                vistaTabla.setVisible(true);
                vistaTabla.setManaged(true);

                trabajadorSeleccionado = null;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al eliminar el trabajador de la base de datos.");
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
        tablaTrabajadores.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Trabajador newSelection = tablaTrabajadores.getSelectionModel().getSelectedItem();

                if (newSelection != null) {
                    trabajadorSeleccionado = newSelection;

                    try {
                        txtDetalleNombre.setText(newSelection.getNombre());
                        txtDetalleApellidos.setText(newSelection.getApellidos());
                        txtDetalleDni.setText(newSelection.getDni());
                        txtDetalleCorreo.setText(newSelection.getCorreo_electronico());

                        if (newSelection.getDireccion() != null) {
                            txtDetalleCalle.setText(newSelection.getDireccion().getCalle() != null ? newSelection.getDireccion().getCalle() : "");
                            txtDetalleCiudad.setText(newSelection.getDireccion().getCiudad() != null ? newSelection.getDireccion().getCiudad() : "");
                            txtDetalleCP.setText(newSelection.getDireccion().getCodigoPostal() == 0 ? "" : String.valueOf(newSelection.getDireccion().getCodigoPostal()));
                        } else {
                            txtDetalleCalle.setText("");
                            txtDetalleCiudad.setText("");
                            txtDetalleCP.setText("");
                        }

                        txtDetalleSS.setText(newSelection.getNumero_ss());
                        txtDetalleIban.setText(newSelection.getIban());
                        txtDetalleSalario.setText("" + newSelection.getSalario());
                        cmbDetalleCargo.getSelectionModel().select(newSelection.getCargo());
                        cmbDetalleJornada.getSelectionModel().select(newSelection.getTipo_jornada());
                        cmbDetalleRol.getSelectionModel().select(newSelection.getRol());

                        panelFormularioNuevo.setTranslateX(400);
                        panelFormularioNuevo.setVisible(false);
                        spacerLateral.setPrefWidth(0);
                        spacerLateral.setMinWidth(0);
                        vistaTabla.setVisible(false);
                        vistaTabla.setManaged(false);
                        vistaDetalles.setVisible(true);
                        vistaDetalles.setManaged(true);
                        tablaTrabajadores.getSelectionModel().clearSelection();

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
                txtDetalleNombre.setEditable(true);
                txtDetalleApellidos.setEditable(true);
                txtDetalleDni.setEditable(true);
                txtDetalleCorreo.setEditable(true);
                txtDetalleCalle.setEditable(true);
                txtDetalleCiudad.setEditable(true);
                txtDetalleCP.setEditable(true);
                txtDetalleSS.setEditable(true);
                txtDetalleIban.setEditable(true);
                txtDetalleSalario.setEditable(true);
                cmbDetalleCargo.setDisable(false);
                cmbDetalleJornada.setDisable(false);
                cmbDetalleRol.setDisable(false);

                txtDetalleNombre.getStyleClass().remove("error");
                txtDetalleApellidos.getStyleClass().remove("error");
                txtDetalleDni.getStyleClass().remove("error");
                txtDetalleCalle.getStyleClass().remove("error");
                txtDetalleCiudad.getStyleClass().remove("error");
                txtDetalleCP.getStyleClass().remove("error");
                txtDetalleSS.getStyleClass().remove("error");
                txtDetalleIban.getStyleClass().remove("error");
                txtDetalleSalario.getStyleClass().remove("error");
                cmbDetalleCargo.getStyleClass().remove("error");
                cmbDetalleJornada.getStyleClass().remove("error");
                cmbDetalleRol.getStyleClass().remove("error");

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
                txtDetalleCorreo.getStyleClass().remove("error");
                txtDetalleCalle.getStyleClass().remove("error");
                txtDetalleCiudad.getStyleClass().remove("error");
                txtDetalleCP.getStyleClass().remove("error");
                txtDetalleSS.getStyleClass().remove("error");
                txtDetalleIban.getStyleClass().remove("error");
                txtDetalleSalario.getStyleClass().remove("error");
                cmbDetalleCargo.getStyleClass().remove("error");
                cmbDetalleJornada.getStyleClass().remove("error");
                cmbDetalleRol.getStyleClass().remove("error");

                boolean hayError = false;

                if (txtDetalleNombre.getText().trim().isEmpty()) {
                    txtDetalleNombre.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleApellidos.getText().trim().isEmpty()) {
                    txtDetalleApellidos.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleDni.getText().trim().isEmpty()) {
                    txtDetalleDni.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleCorreo.getText().trim().isEmpty()) {
                    txtDetalleCorreo.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleCalle.getText().trim().isEmpty()) {
                    txtDetalleCalle.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleCiudad.getText().trim().isEmpty()) {
                    txtDetalleCiudad.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleCP.getText().trim().isEmpty()) {
                    txtDetalleCP.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleSS.getText().trim().isEmpty()) {
                    txtDetalleSS.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleIban.getText().trim().isEmpty()) {
                    txtDetalleIban.getStyleClass().add("error");
                    hayError = true;
                }
                if (txtDetalleSalario.getText().trim().isEmpty()) {
                    txtDetalleSalario.getStyleClass().add("error");
                    hayError = true;
                }
                if (cmbDetalleCargo.getSelectionModel().getSelectedIndex() == -1) {
                    cmbDetalleCargo.getStyleClass().add("error");
                    hayError = true;
                }
                if (cmbDetalleJornada.getSelectionModel().getSelectedIndex() == -1) {
                    cmbDetalleJornada.getStyleClass().add("error");
                    hayError = true;
                }
                if (cmbDetalleRol.getSelectionModel().getSelectedIndex() == -1) {
                    cmbDetalleRol.getStyleClass().add("error");
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

                if (trabajadorSeleccionado != null) {
                    trabajadorSeleccionado.setNombre(txtDetalleNombre.getText().trim());
                    trabajadorSeleccionado.setApellidos(txtDetalleApellidos.getText().trim());
                    trabajadorSeleccionado.setDni(txtDetalleDni.getText().trim());
                    trabajadorSeleccionado.setCorreo_electronico(txtDetalleCorreo.getText().trim());

                    Direccion dir = trabajadorSeleccionado.getDireccion();
                    if (dir == null) {
                        dir = new Direccion();
                    }
                    dir.setCalle(txtDetalleCalle.getText().trim());
                    dir.setCiudad(txtDetalleCiudad.getText().trim());
                    try {
                        dir.setCodigoPostal(txtDetalleCP.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtDetalleCP.getText().trim()));
                    } catch (NumberFormatException ex) {
                        dir.setCodigoPostal(0);
                    }
                    trabajadorSeleccionado.setDireccion(dir);

                    trabajadorSeleccionado.setNumero_ss(txtDetalleSS.getText().trim());
                    trabajadorSeleccionado.setIban(txtDetalleIban.getText().trim());

                    try {
                        trabajadorSeleccionado.setSalario(Double.valueOf(txtDetalleSalario.getText().trim()));
                    } catch (NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Formato incorrecto");
                        alert.setHeaderText(null);
                        alert.setContentText("El salario debe ser un valor numérico.");
                        alert.showAndWait();
                        return;
                    }

                    trabajadorSeleccionado.setCargo(cmbDetalleCargo.getSelectionModel().getSelectedItem());
                    trabajadorSeleccionado.setTipo_jornada(cmbDetalleJornada.getSelectionModel().getSelectedItem());
                    trabajadorSeleccionado.setRol(cmbDetalleRol.getSelectionModel().getSelectedItem());

                    if (trabajadorDAO.actualizarTrabajador(trabajadorSeleccionado)) {
                        tablaTrabajadores.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Hubo un error al actualizar los datos en la base de datos.");
                        alert.showAndWait();
                        return;
                    }
                }

                bloquearCamposEdicion();
            }
        });
    }

    private void bloquearCamposEdicion() {
        txtDetalleNombre.setEditable(false);
        txtDetalleApellidos.setEditable(false);
        txtDetalleDni.setEditable(false);
        txtDetalleCorreo.setEditable(false);
        txtDetalleCalle.setEditable(false);
        txtDetalleCiudad.setEditable(false);
        txtDetalleCP.setEditable(false);
        txtDetalleSS.setEditable(false);
        txtDetalleIban.setEditable(false);
        txtDetalleSalario.setEditable(false);
        cmbDetalleCargo.setDisable(true);
        cmbDetalleJornada.setDisable(true);
        cmbDetalleRol.setDisable(true);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);
    }
}
