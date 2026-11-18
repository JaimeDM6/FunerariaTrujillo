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
import javafx.scene.shape.SVGPath;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Controlador de la vista de trabajadores. Gestiona el listado con búsqueda, el alta (con usuario
 * y contraseña), la edición y la eliminación de trabajadores.
 *
 * @author Iker
 */
public class TrabajadorController implements Initializable {

    private final TrabajadorDAO trabajadorDAO;

    /**
     * Crea el controlador con el TrabajadorDAO por defecto.
     */
    public TrabajadorController() {
        this.trabajadorDAO = new TrabajadorDAO();
    }

    /**
     * Crea el controlador con el DAO indicado. Permite sustituir el acceso a la base de datos, por
     * ejemplo en las pruebas.
     *
     * @param trabajadorDAO DAO que se utilizará para acceder a la base de datos
     */
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
    private TextField txtNuevaCiudad;
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

    @FXML
    private PasswordField txtNuevaPassword;
    @FXML
    private PasswordField txtNuevaConfirmarPassword;
    @FXML
    private TextField txtNuevaPasswordVisible;
    @FXML
    private TextField txtNuevaConfirmarPasswordVisible;
    @FXML
    private Button btnMostrarNuevaPassword;
    @FXML
    private Button btnMostrarNuevaConfirmarPassword;
    @FXML
    private SVGPath svgOjoNuevaPassword;
    @FXML
    private SVGPath svgOjoNuevaConfirmarPassword;

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

    private static final String OJO_ABIERTO = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
    private static final String OJO_TACHADO = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z";

    private final ObservableList<Trabajador> masterData = FXCollections.observableArrayList();
    private Trabajador trabajadorSeleccionado;

    /**
     * Inicializa la vista de trabajadores: configura la tabla, el buscador y los formularios de alta y
     * de detalle, y carga los datos de la base de datos.
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

        ObservableList<String> jornadas = FXCollections.observableArrayList("Completa", "Parcial", "Nocturna");
        cmbNuevaJornada.setItems(jornadas);
        cmbDetalleJornada.setItems(jornadas);

        cmbNuevoCargo.setItems(FXCollections.observableArrayList(Cargo.values()));
        cmbDetalleCargo.setItems(FXCollections.observableArrayList(Cargo.values()));

        cmbNuevoRol.setItems(FXCollections.observableArrayList(Rol.values()));
        cmbDetalleRol.setItems(FXCollections.observableArrayList(Rol.values()));

        txtNuevaPasswordVisible.textProperty().bindBidirectional(txtNuevaPassword.textProperty());
        txtNuevaConfirmarPasswordVisible.textProperty().bindBidirectional(txtNuevaConfirmarPassword.textProperty());

        btnMostrarNuevaPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                alternarVisibilidadPassword(txtNuevaPassword, txtNuevaPasswordVisible, svgOjoNuevaPassword);
            }
        });

        btnMostrarNuevaConfirmarPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                alternarVisibilidadPassword(txtNuevaConfirmarPassword, txtNuevaConfirmarPasswordVisible, svgOjoNuevaConfirmarPassword);
            }
        });

        cmbNuevoCargo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cargo>() {
            @Override
            public void changed(ObservableValue<? extends Cargo> observable, Cargo valorAnterior, Cargo valorNuevo) {
                actualizarRolesNuevo();
            }
        });

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
                        boolean coincide = true;

                        if (newValue != null && !newValue.trim().isEmpty()) {
                            String lowerCaseFilter = newValue.toLowerCase();
                            coincide = trabajador.getNombreCompleto().toLowerCase().contains(lowerCaseFilter)
                                    || (trabajador.getCargo() != null
                                    && trabajador.getCargo().toString().toLowerCase().contains(lowerCaseFilter));
                        }

                        return coincide;
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

    /**
     * Carga los trabajadores desde la base de datos en un hilo en segundo plano y actualiza la tabla
     * al terminar. Si la carga falla, avisa al usuario del error de conexión.
     */
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

    /**
     * Genera un nombre de usuario con el formato inicial.apellido, sin tildes y en minúsculas. Si ya
     * existe, añade más letras del nombre y, en último caso, un número.
     *
     * @param nombre nombre del trabajador
     * @param apellidos apellidos del trabajador; solo se usa el primero
     * @return nombre de usuario que todavía no existe en la base de datos
     */
    private String generarUsuario(String nombre, String apellidos) {
        String cleanNombre = java.text.Normalizer.normalize(nombre.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("\\s+", "");

        String cleanApellido = java.text.Normalizer
                .normalize(apellidos.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .split("\\s+")[0];

        String usuarioGenerado = "";
        boolean disponible = false;
        int i = 1;

        while (!disponible && i <= cleanNombre.length()) {
            usuarioGenerado = cleanNombre.substring(0, i) + "." + cleanApellido;
            disponible = !trabajadorDAO.existeUsuario(usuarioGenerado);
            i++;
        }

        String resultado = usuarioGenerado;

        if (!disponible) {
            int contador = 1;
            while (trabajadorDAO.existeUsuario(resultado)) {
                resultado = usuarioGenerado + contador;
                contador++;
            }
        }

        return resultado;
    }

    /**
     * Valida el formulario de alta y, si los datos son correctos, genera el usuario, inserta el
     * trabajador en la base de datos y recarga la tabla. Si no lo son, muestra un aviso con el motivo.
     */
    private void guardarNuevoTrabajador() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevoApellidos.getStyleClass().remove("error");
        txtNuevoDni.getStyleClass().remove("error");
        txtNuevoCorreo.getStyleClass().remove("error");
        txtNuevoCalle.getStyleClass().remove("error");
        txtNuevaCiudad.getStyleClass().remove("error");
        txtNuevoCP.getStyleClass().remove("error");
        txtNuevoSS.getStyleClass().remove("error");
        txtNuevoIban.getStyleClass().remove("error");
        txtNuevoSalario.getStyleClass().remove("error");
        txtNuevaPassword.getStyleClass().remove("error");
        txtNuevaConfirmarPassword.getStyleClass().remove("error");
        txtNuevaPasswordVisible.getStyleClass().remove("error");
        txtNuevaConfirmarPasswordVisible.getStyleClass().remove("error");
        cmbNuevoCargo.getStyleClass().remove("error");
        cmbNuevaJornada.getStyleClass().remove("error");
        cmbNuevoRol.getStyleClass().remove("error");

        String mensaje = validarNuevoTrabajador();

        if (mensaje == null) {
            Trabajador nuevoTrabajador = new Trabajador();
            nuevoTrabajador.setNombre(txtNuevoNombre.getText().trim());
            nuevoTrabajador.setApellidos(txtNuevoApellidos.getText().trim());
            nuevoTrabajador.setDni(txtNuevoDni.getText().trim().toUpperCase());
            nuevoTrabajador.setCorreo_electronico(txtNuevoCorreo.getText().trim());
            nuevoTrabajador.setNumero_ss(txtNuevoSS.getText().trim());
            nuevoTrabajador.setIban(txtNuevoIban.getText().trim());

            String usuario = generarUsuario(txtNuevoNombre.getText(), txtNuevoApellidos.getText());
            nuevoTrabajador.setUsuario(usuario);

            String hashPass = BCrypt.hashpw(txtNuevaPassword.getText().trim(), BCrypt.gensalt(12));
            nuevoTrabajador.setPassword(hashPass);

            nuevoTrabajador.setSalario(Double.valueOf(txtNuevoSalario.getText().trim()));
            nuevoTrabajador.setCargo(cmbNuevoCargo.getSelectionModel().getSelectedItem());
            nuevoTrabajador.setTipo_jornada(cmbNuevaJornada.getSelectionModel().getSelectedItem());
            nuevoTrabajador.setRol(cmbNuevoRol.getSelectionModel().getSelectedItem());

            Direccion dir = new Direccion();
            dir.setCalle(txtNuevoCalle.getText().trim());
            dir.setCiudad(txtNuevaCiudad.getText().trim());
            dir.setCodigoPostal(Integer.parseInt(txtNuevoCP.getText().trim()));
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
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos no válidos");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
    }

    /**
     * Comprueba los campos del formulario de alta de trabajador y marca como erróneos los que no son
     * válidos.
     *
     * @return mensaje con el primer error encontrado, o {@code null} si todos los datos son válidos
     */
    private String validarNuevoTrabajador() {
        String mensaje = null;
        boolean hayError = false;
        String pass = txtNuevaPassword.getText().trim();
        String confirm = txtNuevaConfirmarPassword.getText().trim();

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
        if (txtNuevaCiudad.getText().trim().isEmpty()) {
            txtNuevaCiudad.getStyleClass().add("error");
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
        if (pass.isEmpty() || pass.length() < 8) {
            txtNuevaPassword.getStyleClass().add("error");
            txtNuevaPasswordVisible.getStyleClass().add("error");
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
            mensaje = "Todos los campos son obligatorios y la contraseña debe tener al menos 8 caracteres.";
        } else if (!pass.equals(confirm)) {
            txtNuevaPassword.getStyleClass().add("error");
            txtNuevaPasswordVisible.getStyleClass().add("error");
            txtNuevaConfirmarPassword.getStyleClass().add("error");
            txtNuevaConfirmarPasswordVisible.getStyleClass().add("error");
            mensaje = "Las contraseñas introducidas no son iguales.";
        } else if (!Utils.dniValido(txtNuevoDni.getText().trim())) {
            txtNuevoDni.getStyleClass().add("error");
            mensaje = "El DNI debe tener 8 números y una letra (ej. 12345678A).";
        } else if (!Utils.correoValido(txtNuevoCorreo.getText().trim())) {
            txtNuevoCorreo.getStyleClass().add("error");
            mensaje = "El correo electrónico no tiene un formato válido.";
        } else if (!Utils.cpValido(txtNuevoCP.getText().trim())) {
            txtNuevoCP.getStyleClass().add("error");
            mensaje = "El código postal debe tener 5 números.";
        } else {
            try {
                Double.valueOf(txtNuevoSalario.getText().trim());
            } catch (NumberFormatException e) {
                txtNuevoSalario.getStyleClass().add("error");
                mensaje = "El salario debe ser un valor numérico.";
            }
        }

        return mensaje;
    }

    /**
     * Alterna entre mostrar y ocultar la contraseña y cambia el icono del ojo.
     *
     * @param campoOculto campo de contraseña oculta
     * @param campoVisible campo de texto que muestra la contraseña
     * @param icono icono del botón de visibilidad
     */
    private void alternarVisibilidadPassword(PasswordField campoOculto, TextField campoVisible, SVGPath icono) {
        boolean mostrar = !campoVisible.isVisible();

        campoOculto.setVisible(!mostrar);
        campoOculto.setManaged(!mostrar);
        campoVisible.setVisible(mostrar);
        campoVisible.setManaged(mostrar);

        if (mostrar) {
            icono.setContent(OJO_TACHADO);
        } else {
            icono.setContent(OJO_ABIERTO);
        }
    }

    /**
     * Actualiza los roles que se pueden elegir en el formulario de alta según el cargo seleccionado.
     * Si solo hay un rol posible, lo selecciona.
     */
    private void actualizarRolesNuevo() {
        Cargo cargo = cmbNuevoCargo.getSelectionModel().getSelectedItem();
        ObservableList<Rol> roles = FXCollections.observableArrayList();

        if (cargo == Cargo.TANATOPRACTOR) {
            roles.add(Rol.TANATOPRAXIA);
            roles.add(Rol.AGENTE_FUNERARIO);
        } else if (cargo == Cargo.AGENTE_FUNERARIO) {
            roles.add(Rol.AGENTE_FUNERARIO);
        } else if (cargo == Cargo.RECEPCIONISTA) {
            roles.add(Rol.CONTABILIDAD);
            roles.add(Rol.ADMINISTRACION);
        } else if (cargo == Cargo.CONDUCTOR) {
            roles.add(Rol.TRANSPORTE);
        } else {
            roles.addAll(Rol.values());
        }

        cmbNuevoRol.setItems(roles);
        cmbNuevoRol.getSelectionModel().clearSelection();

        if (roles.size() == 1) {
            cmbNuevoRol.getSelectionModel().select(0);
        }
    }

    /**
     * Vacía los campos del formulario de alta.
     */
    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevoApellidos.clear();
        txtNuevoDni.clear();
        txtNuevoCorreo.clear();
        txtNuevoCalle.clear();
        txtNuevaCiudad.clear();
        txtNuevoCP.clear();
        txtNuevoSS.clear();
        txtNuevoIban.clear();
        txtNuevoSalario.clear();
        txtNuevaPassword.clear();
        txtNuevaConfirmarPassword.clear();
        cmbNuevoCargo.getSelectionModel().clearSelection();
        cmbNuevaJornada.getSelectionModel().clearSelection();
        cmbNuevoRol.getSelectionModel().clearSelection();
    }

    /**
     * Elimina de la base de datos el trabajador seleccionado en la tabla y recarga los datos.
     */
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
     * Configura la selección de la tabla: al elegir un trabajador se muestran sus datos en el panel de
     * detalle.
     */
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

                        String calle = "";
                        String ciudad = "";
                        String codigoPostal = "";

                        if (newSelection.getDireccion() != null) {
                            calle = textoSinNulos(newSelection.getDireccion().getCalle());
                            ciudad = textoSinNulos(newSelection.getDireccion().getCiudad());
                            if (newSelection.getDireccion().getCodigoPostal() != 0) {
                                codigoPostal = String.valueOf(newSelection.getDireccion().getCodigoPostal());
                            }
                        }

                        txtDetalleCalle.setText(calle);
                        txtDetalleCiudad.setText(ciudad);
                        txtDetalleCP.setText(codigoPostal);

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

    /**
     * Configura los botones del panel de detalle para editar el trabajador seleccionado y guardar o
     * descartar los cambios.
     */
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

                String mensaje = validarDetalleTrabajador();

                if (mensaje == null) {
                    boolean bloquear = true;

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

                        int codigoPostal = 0;
                        try {
                            codigoPostal = Integer.parseInt(txtDetalleCP.getText().trim());
                        } catch (NumberFormatException ex) {
                            codigoPostal = 0;
                        }
                        dir.setCodigoPostal(codigoPostal);
                        trabajadorSeleccionado.setDireccion(dir);

                        trabajadorSeleccionado.setNumero_ss(txtDetalleSS.getText().trim());
                        trabajadorSeleccionado.setIban(txtDetalleIban.getText().trim());
                        trabajadorSeleccionado.setSalario(Double.valueOf(txtDetalleSalario.getText().trim()));
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
                            bloquear = false;
                        }
                    }

                    if (bloquear) {
                        bloquearCamposEdicion();
                    }
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
     * Comprueba los campos del panel de detalle antes de guardar la edición del trabajador.
     *
     * @return mensaje con el primer error encontrado, o {@code null} si todos los datos son válidos
     */
    private String validarDetalleTrabajador() {
        String mensaje = null;
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
            mensaje = "No puedes dejar en blanco los campos.";
        } else {
            try {
                Double.valueOf(txtDetalleSalario.getText().trim());
            } catch (NumberFormatException ex) {
                txtDetalleSalario.getStyleClass().add("error");
                mensaje = "El salario debe ser un valor numérico.";
            }
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
     * Deja los campos del panel de detalle en modo solo lectura.
     */
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