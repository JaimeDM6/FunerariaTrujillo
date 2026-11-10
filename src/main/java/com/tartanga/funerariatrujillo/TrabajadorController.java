package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.TrabajadorDAO;
import com.tartanga.funerariatrujillo.Objects.Direccion;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import com.tartanga.funerariatrujillo.Utils.Utils;
import org.mindrot.jbcrypt.BCrypt;
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
    private TableColumn<Trabajador, String> colNombre;
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
    private ComboBox<String> cmbNuevoCargo;
    @FXML
    private ComboBox<String> cmbNuevaJornada;
    @FXML
    private TextField txtNuevoUsuario;
    @FXML
    private PasswordField txtNuevaPassword;
    @FXML
    private ComboBox<String> cmbNuevoRol;

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
    private ComboBox<String> cmbDetalleCargo;
    @FXML
    private ComboBox<String> cmbDetalleJornada;
    @FXML
    private ComboBox<String> cmbDetalleRol;

    private final ObservableList<Trabajador> masterData = FXCollections.observableArrayList();
    private Trabajador trabajadorSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);
        configurarCombos();
        configurarTabla();
        configurarBusqueda();
        configurarBotones();
        cargarDatos();
    }

    private void configurarCombos() {
        ObservableList<String> cargos = FXCollections.observableArrayList(
                "Tanatopractor", "Agente Funerario", "Recepcionista", "Director", "Conductor");
        ObservableList<String> jornadas = FXCollections.observableArrayList(
                "Completa", "Parcial", "Nocturna");
        cmbNuevoCargo.setItems(cargos);
        cmbDetalleCargo.setItems(FXCollections.observableArrayList(cargos));
        cmbNuevaJornada.setItems(jornadas);
        cmbDetalleJornada.setItems(FXCollections.observableArrayList(jornadas));
        ObservableList<String> roles = FXCollections.observableArrayList(
                "Agente Funerario", "Contabilidad", "Tanatopraxia", "Administración");
        cmbNuevoRol.setItems(roles);
        cmbDetalleRol.setItems(FXCollections.observableArrayList(roles));
        cmbNuevoCargo.setValue("Agente Funerario");
        cmbNuevaJornada.setValue("Completa");
        cmbNuevoRol.setValue("Agente Funerario");
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<Trabajador, String>("nombre"));
        colCargo.setCellValueFactory(new PropertyValueFactory<Trabajador, Trabajador.Cargo>("cargo"));
    }

    private void configurarBusqueda() {
        FilteredList<Trabajador> filtrados = new FilteredList<Trabajador>(masterData, new Predicate<Trabajador>() {
            @Override
            public boolean test(Trabajador trabajador) {
                return true;
            }
        });
        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String nuevoValor) {
                filtrados.setPredicate(new Predicate<Trabajador>() {
                    @Override
                    public boolean test(Trabajador trabajador) {
                        if (nuevoValor == null || nuevoValor.trim().isEmpty()) {
                            return true;
                        }
                        String filtro = nuevoValor.toLowerCase();
                        return trabajador.getNombre().toLowerCase().contains(filtro)
                                || trabajador.getApellidos().toLowerCase().contains(filtro);
                    }
                });
                lblEstadoTabla.setText(filtrados.size() + " trabajadores");
            }
        });
        SortedList<Trabajador> ordenados = new SortedList<Trabajador>(filtrados);
        ordenados.comparatorProperty().bind(tablaTrabajadores.comparatorProperty());
        tablaTrabajadores.setItems(ordenados);
    }

    private void configurarBotones() {
        btnNuevoTrabajador.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Utils.openForm(panelFormularioNuevo, spacerLateral);
            }
        });
        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Utils.closeForm(panelFormularioNuevo, spacerLateral);
            }
        });
        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevo();
            }
        });
        btnEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cambiarEdicion(true);
            }
        });
        btnGuardarEdicion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarEdicion();
            }
        });
        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarSeleccionado();
            }
        });
        btnVolver.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                volverListado();
            }
        });
        tablaTrabajadores.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Trabajador seleccionado = tablaTrabajadores.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    mostrarDetalles(seleccionado);
                }
            }
        });
    }

    private void cargarDatos() {
        Task<List<Trabajador>> tarea = new Task<List<Trabajador>>() {
            @Override
            protected List<Trabajador> call() {
                return new TrabajadorDAO().obtenerTrabajadores();
            }
        };
        tarea.setOnSucceeded(new EventHandler<javafx.concurrent.WorkerStateEvent>() {
            @Override
            public void handle(javafx.concurrent.WorkerStateEvent event) {
                masterData.setAll(tarea.getValue());
                tablaTrabajadores.sort();
                lblEstadoTabla.setText(masterData.size() + " trabajadores");
            }
        });
        tarea.setOnFailed(new EventHandler<javafx.concurrent.WorkerStateEvent>() {
            @Override
            public void handle(javafx.concurrent.WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se han podido cargar los trabajadores.");
            }
        });
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void guardarNuevo() {
        if (!validarCampos(txtNuevoNombre, txtNuevoApellidos, txtNuevoDni, txtNuevoCorreo,
                txtNuevoCalle, txtNuevoCiudad, txtNuevoCP, txtNuevoSS, txtNuevoIban, txtNuevoSalario,
                cmbNuevoCargo, cmbNuevaJornada)) {
            return;
        }
        if (txtNuevoUsuario.getText().trim().isEmpty() || txtNuevaPassword.getText().length() < 8
                || cmbNuevoRol.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Credenciales incompletas",
                    "El usuario, una contraseña de al menos 8 caracteres y el rol son obligatorios.");
            return;
        }
        try {
            Trabajador trabajador = crearTrabajador(txtNuevoNombre, txtNuevoApellidos, txtNuevoDni,
                    txtNuevoCorreo, txtNuevoCalle, txtNuevoCiudad, txtNuevoCP, txtNuevoSS,
                    txtNuevoIban, txtNuevoSalario, cmbNuevoCargo, cmbNuevaJornada);
            trabajador.setUsuario(txtNuevoUsuario.getText().trim());
            trabajador.setPassword(BCrypt.hashpw(txtNuevaPassword.getText(), BCrypt.gensalt(12)));
            trabajador.setRol(convertirRol(cmbNuevoRol.getValue()));
            if (new TrabajadorDAO().insertarTrabajador(trabajador)) {
                masterData.add(trabajador);
                lblEstadoTabla.setText(masterData.size() + " trabajadores");
                limpiarNuevo();
                Utils.closeForm(panelFormularioNuevo, spacerLateral);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido guardar el trabajador.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Salario incorrecto", "El salario debe ser un número no negativo.");
        }
    }

    private void guardarEdicion() {
        if (trabajadorSeleccionado == null || !validarCampos(txtDetalleNombre, txtDetalleApellidos, txtDetalleDni,
                txtDetalleCorreo, txtDetalleCalle, txtDetalleCiudad, txtDetalleCP, txtDetalleSS,
                txtDetalleIban, txtDetalleSalario, cmbDetalleCargo, cmbDetalleJornada)) {
            return;
        }
        try {
            actualizarTrabajadorDesdeCampos();
            if (new TrabajadorDAO().actualizarTrabajador(trabajadorSeleccionado)) {
                tablaTrabajadores.refresh();
                cambiarEdicion(false);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido actualizar el trabajador.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Salario incorrecto", "El salario debe ser un número no negativo.");
        }
    }

    private boolean validarCampos(TextField nombre, TextField apellidos, TextField dni, TextField correo,
            TextField calle, TextField ciudad, TextField cp, TextField ss, TextField iban, TextField salario,
            ComboBox<String> cargo, ComboBox<String> jornada) {
        if (nombre.getText().trim().isEmpty() || apellidos.getText().trim().isEmpty()
                || dni.getText().trim().isEmpty() || correo.getText().trim().isEmpty()
                || calle.getText().trim().isEmpty() || ciudad.getText().trim().isEmpty()
                || cp.getText().trim().isEmpty() || ss.getText().trim().isEmpty()
                || iban.getText().trim().isEmpty() || salario.getText().trim().isEmpty()
                || cargo.getValue() == null || jornada.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Todos los campos marcados con * son obligatorios.");
            return false;
        }
        if (!dni.getText().trim().matches("[0-9]{8}[A-Z]")) {
            mostrarAlerta(Alert.AlertType.WARNING, "DNI incorrecto", "El DNI debe tener 8 números y una letra mayúscula.");
            return false;
        }
        if (!correo.getText().trim().matches("^.+@.+\\..+$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo incorrecto", "Introduce un correo electrónico válido.");
            return false;
        }
        if (!cp.getText().trim().matches("[0-9]{5}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código postal incorrecto", "El código postal debe tener 5 cifras.");
            return false;
        }
        if (!iban.getText().trim().matches("[A-Z]{2}[0-9]{22}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "IBAN incorrecto", "El IBAN debe tener dos letras y 22 números.");
            return false;
        }
        try {
            if (Double.parseDouble(salario.getText().trim().replace(",", ".")) < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Salario incorrecto", "El salario no puede ser negativo.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Salario incorrecto", "El salario debe ser numérico.");
            return false;
        }
        return true;
    }

    private Trabajador crearTrabajador(TextField nombre, TextField apellidos, TextField dni, TextField correo,
            TextField calle, TextField ciudad, TextField cp, TextField ss, TextField iban, TextField salario,
            ComboBox<String> cargo, ComboBox<String> jornada) {
        Trabajador trabajador = new Trabajador();
        trabajador.setNombre(nombre.getText().trim());
        trabajador.setApellidos(apellidos.getText().trim());
        trabajador.setDni(dni.getText().trim());
        trabajador.setCorreo_electronico(correo.getText().trim());
        trabajador.setNumero_ss(ss.getText().trim());
        trabajador.setIban(iban.getText().trim());
        trabajador.setSalario(Double.valueOf(salario.getText().trim().replace(",", ".")));
        trabajador.setCargo(convertirCargo(cargo.getValue()));
        trabajador.setTipo_jornada(jornada.getValue());
        trabajador.setDireccion(crearDireccion(calle, ciudad, cp));
        return trabajador;
    }

    private Direccion crearDireccion(TextField calle, TextField ciudad, TextField cp) {
        return new Direccion(calle.getText().trim(), ciudad.getText().trim(), Integer.parseInt(cp.getText().trim()));
    }

    private void actualizarTrabajadorDesdeCampos() {
        Trabajador actualizado = crearTrabajador(txtDetalleNombre, txtDetalleApellidos, txtDetalleDni,
                txtDetalleCorreo, txtDetalleCalle, txtDetalleCiudad, txtDetalleCP, txtDetalleSS,
                txtDetalleIban, txtDetalleSalario, cmbDetalleCargo, cmbDetalleJornada);
        trabajadorSeleccionado.setNombre(actualizado.getNombre());
        trabajadorSeleccionado.setApellidos(actualizado.getApellidos());
        trabajadorSeleccionado.setDni(actualizado.getDni());
        trabajadorSeleccionado.setCorreo_electronico(actualizado.getCorreo_electronico());
        trabajadorSeleccionado.setDireccion(actualizado.getDireccion());
        trabajadorSeleccionado.setNumero_ss(actualizado.getNumero_ss());
        trabajadorSeleccionado.setIban(actualizado.getIban());
        trabajadorSeleccionado.setCargo(actualizado.getCargo());
        trabajadorSeleccionado.setRol(convertirRol(cmbDetalleRol.getValue()));
        trabajadorSeleccionado.setSalario(actualizado.getSalario());
        trabajadorSeleccionado.setTipo_jornada(actualizado.getTipo_jornada());
    }

    private Trabajador.Cargo convertirCargo(String valor) {
        if ("Tanatopractor".equals(valor)) {
            return Trabajador.Cargo.TANATOPRACTOR;
        }
        if ("Agente Funerario".equals(valor)) {
            return Trabajador.Cargo.AGENTE_FUNERARIO;
        }
        if ("Recepcionista".equals(valor)) {
            return Trabajador.Cargo.RECEPCIONISTA;
        }
        if ("Director".equals(valor)) {
            return Trabajador.Cargo.DIRECTOR;
        }
        return Trabajador.Cargo.CONDUCTOR;
    }

    private Trabajador.Rol convertirRol(String valor) {
        for (Trabajador.Rol rol : Trabajador.Rol.values()) {
            if (rol.toString().equals(valor)) {
                return rol;
            }
        }
        return Trabajador.Rol.AGENTE_FUNERARIO;
    }

    private void mostrarDetalles(Trabajador trabajador) {
        trabajadorSeleccionado = trabajador;
        cargarCamposDetalle(trabajador);
        vistaTabla.setVisible(false);
        vistaTabla.setManaged(false);
        vistaDetalles.setVisible(true);
        vistaDetalles.setManaged(true);
        tablaTrabajadores.getSelectionModel().clearSelection();
        cambiarEdicion(false);
    }

    private void cargarCamposDetalle(Trabajador trabajador) {
        txtDetalleNombre.setText(trabajador.getNombre());
        txtDetalleApellidos.setText(trabajador.getApellidos());
        txtDetalleDni.setText(trabajador.getDni());
        txtDetalleCorreo.setText(trabajador.getCorreo_electronico());
        txtDetalleSS.setText(trabajador.getNumero_ss());
        txtDetalleIban.setText(trabajador.getIban());
        txtDetalleSalario.setText(String.valueOf(trabajador.getSalario()));
        cmbDetalleCargo.setValue(obtenerNombreCargo(trabajador.getCargo()));
        cmbDetalleJornada.setValue(trabajador.getTipo_jornada());
        cmbDetalleRol.setValue(trabajador.getRol().toString());
        if (trabajador.getDireccion() != null) {
            txtDetalleCalle.setText(trabajador.getDireccion().getCalle());
            txtDetalleCiudad.setText(trabajador.getDireccion().getCiudad());
            txtDetalleCP.setText(String.format("%05d", trabajador.getDireccion().getCodigoPostal()));
        }
    }

    private String obtenerNombreCargo(Trabajador.Cargo cargo) {
        if (cargo == Trabajador.Cargo.TANATOPRACTOR) {
            return "Tanatopractor";
        }
        if (cargo == Trabajador.Cargo.AGENTE_FUNERARIO) {
            return "Agente Funerario";
        }
        if (cargo == Trabajador.Cargo.RECEPCIONISTA) {
            return "Recepcionista";
        }
        if (cargo == Trabajador.Cargo.DIRECTOR) {
            return "Director";
        }
        return "Conductor";
    }

    private void cambiarEdicion(boolean editable) {
        TextField[] campos = {txtDetalleNombre, txtDetalleApellidos, txtDetalleDni, txtDetalleCorreo,
            txtDetalleCalle, txtDetalleCiudad, txtDetalleCP, txtDetalleSS, txtDetalleIban, txtDetalleSalario};
        for (TextField campo : campos) {
            campo.setEditable(editable);
        }
        cmbDetalleCargo.setDisable(!editable);
        cmbDetalleJornada.setDisable(!editable);
        cmbDetalleRol.setDisable(!editable);
        btnEditar.setVisible(!editable);
        btnEditar.setManaged(!editable);
        btnEliminar.setVisible(!editable);
        btnEliminar.setManaged(!editable);
        btnGuardarEdicion.setVisible(editable);
        btnGuardarEdicion.setManaged(editable);
    }

    private void eliminarSeleccionado() {
        if (trabajadorSeleccionado != null && new TrabajadorDAO().eliminarTrabajador(trabajadorSeleccionado.getId())) {
            masterData.remove(trabajadorSeleccionado);
            volverListado();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido eliminar el trabajador.");
        }
    }

    private void volverListado() {
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);
        trabajadorSeleccionado = null;
    }

    private void limpiarNuevo() {
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
        cmbNuevoCargo.setValue("Agente Funerario");
        cmbNuevaJornada.setValue("Completa");
        cmbNuevoRol.setValue("Agente Funerario");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
