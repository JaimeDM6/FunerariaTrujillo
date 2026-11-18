package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.SalasDAO;
import com.tartanga.funerariatrujillo.Objects.Sala;
import com.tartanga.funerariatrujillo.Utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class SalasController implements Initializable {

    @FXML private VBox vistaTabla;
    @FXML private VBox vistaDetalles;
    @FXML private VBox panelFormularioNuevo;
    @FXML private Region spacerLateral;

    @FXML private TableView<Sala> tablaSalas;
    @FXML private TextField txtBuscar;
    @FXML private Label lblEstadoTabla;

    @FXML private TableColumn<Sala, String> colID;
    @FXML private TableColumn<Sala, String> colNombre;
    @FXML private TableColumn<Sala, String> colDescripcion;
    @FXML private TableColumn<Sala, Integer> colCapacidad;

    @FXML private Button btnNuevaSala;
    @FXML private Button btnCerrarFormulario;
    @FXML private Button btnVolver;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardarEdicion;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardarNuevo;

    @FXML private TextField txtNuevoNombre;
    @FXML private TextArea txtNuevaDescripcion;
    @FXML private TextField txtNuevaCapacidad;

    @FXML private TextField txtDetalleNombre;
    @FXML private TextArea txtDetalleDescripcion;
    @FXML private TextField txtDetalleCapacidad;

    private final ObservableList<Sala> masterData =
            FXCollections.observableArrayList();

    private Sala salaSeleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ocultarFormularioNuevo();
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);

        configurarTabla();
        configurarBusqueda();
        configurarBotones();
        bloquearCamposEdicion();
        cargarDatos();
    }

    private void configurarBotones() {
        // Cada acción llama al método correspondiente.
        btnNuevaSala.setOnAction(event -> openForm());
        btnCerrarFormulario.setOnAction(event -> closeForm());
        btnGuardarNuevo.setOnAction(event -> guardarNuevaSala());
        btnVolver.setOnAction(event -> volverAListado());
        btnEditar.setOnAction(event -> activarEdicion());
        btnGuardarEdicion.setOnAction(event -> guardarEdicion());
        btnEliminar.setOnAction(event -> eliminarSalaSeleccionada());

        tablaSalas.setOnMouseClicked(event -> {
            Sala seleccion = tablaSalas.getSelectionModel().getSelectedItem();

            if (seleccion != null) {
                mostrarDetalles(seleccion);
            }
        });
    }

    private void configurarTabla() {
        colID.setCellValueFactory(
                new PropertyValueFactory<Sala, String>("id"));
        colNombre.setCellValueFactory(
                new PropertyValueFactory<Sala, String>("nombre"));
        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<Sala, String>("descripcion"));
        colCapacidad.setCellValueFactory(
                new PropertyValueFactory<Sala, Integer>("capacidad"));

        colID.setReorderable(false);
        colNombre.setReorderable(false);
        colDescripcion.setReorderable(false);
        colCapacidad.setReorderable(false);
    }

    private void configurarBusqueda() {
        FilteredList<Sala> filtradas = new FilteredList<>(masterData);

        txtBuscar.textProperty().addListener((observable, anterior, nuevo) -> {
            String filtro = textoSeguro(nuevo).trim().toLowerCase(Locale.ROOT);

            filtradas.setPredicate(sala ->
                    textoSeguro(sala.getId()).toLowerCase(Locale.ROOT).contains(filtro)
                    || textoSeguro(sala.getNombre()).toLowerCase(Locale.ROOT).contains(filtro)
                    || textoSeguro(sala.getDescripcion()).toLowerCase(Locale.ROOT).contains(filtro)
            );

            actualizarEstadoTabla();
        });

        SortedList<Sala> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tablaSalas.comparatorProperty());
        tablaSalas.setItems(ordenadas);
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private void cargarDatos() {
        lblEstadoTabla.setText("Cargando salas...");
        vistaTabla.setDisable(true);

        Task<List<Sala>> tarea = new Task<List<Sala>>() {
            @Override
            protected List<Sala> call() {
                return new SalasDAO().obtenerSalas();
            }
        };

        tarea.setOnSucceeded(event -> {
            vistaTabla.setDisable(false);
            masterData.setAll(tarea.getValue());
            tablaSalas.sort();
            actualizarEstadoTabla();
        });

        tarea.setOnFailed(event -> {
            vistaTabla.setDisable(false);
            lblEstadoTabla.setText("Error de conexión");
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                    "No se han podido cargar las salas desde MongoDB.");
        });

        iniciarTarea(tarea);
    }

    private void guardarNuevaSala() {
        Sala nueva = leerFormulario(
                txtNuevoNombre.getText(),
                txtNuevaDescripcion.getText(),
                txtNuevaCapacidad.getText()
        );

        if (nueva == null) {
            return;
        }

        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return new SalasDAO().insertarSala(nueva);
            }
        };

        ejecutarOperacion(tarea, () -> {
            masterData.add(nueva);
            tablaSalas.sort();
            actualizarEstadoTabla();
            limpiarFormularioNuevo();
            closeForm();
        }, "No se ha podido guardar la sala.");
    }

    private void guardarEdicion() {
        if (salaSeleccionada == null) {
            return;
        }

        // El objeto original solo se sustituye si MongoDB guarda los cambios.
        Sala modificada = leerFormulario(
                txtDetalleNombre.getText(),
                txtDetalleDescripcion.getText(),
                txtDetalleCapacidad.getText()
        );

        if (modificada == null) {
            return;
        }

        modificada.setId(salaSeleccionada.getId());

        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return new SalasDAO().actualizarSala(modificada);
            }
        };

        ejecutarOperacion(tarea, () -> {
            int posicion = buscarPosicionPorId(modificada.getId());
            if (posicion >= 0) {
                masterData.set(posicion, modificada);
            }
            salaSeleccionada = modificada;
            tablaSalas.sort();
            actualizarEstadoTabla();
            mostrarDetalles(modificada);
        }, "No se ha podido actualizar la sala.");
    }

    private void eliminarSalaSeleccionada() {
        if (salaSeleccionada == null) {
            return;
        }

        String id = salaSeleccionada.getId();

        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return new SalasDAO().eliminarSala(id);
            }
        };

        ejecutarOperacion(tarea, () -> {
            int posicion = buscarPosicionPorId(id);
            if (posicion >= 0) {
                masterData.remove(posicion);
            }
            actualizarEstadoTabla();
            volverAListado();
        }, "No se ha podido eliminar la sala.");
    }

    // Ejecuta las operaciones de MongoDB sin bloquear el hilo de JavaFX.
    private void ejecutarOperacion(Task<Boolean> tarea,
                                   Runnable alGuardar, String mensajeError) {
        bloquearInterfaz(true);

        tarea.setOnSucceeded(event -> {
            bloquearInterfaz(false);

            if (Boolean.TRUE.equals(tarea.getValue())) {
                alGuardar.run();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", mensajeError);
            }
        });

        tarea.setOnFailed(event -> {
            bloquearInterfaz(false);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", mensajeError);
        });

        iniciarTarea(tarea);
    }

    private void iniciarTarea(Task<?> tarea) {
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void bloquearInterfaz(boolean bloquear) {
        vistaTabla.setDisable(bloquear);
        vistaDetalles.setDisable(bloquear);
        panelFormularioNuevo.setDisable(bloquear);
    }

    // Se usa tanto al crear una sala como al editarla.
    private Sala leerFormulario(String nombre, String descripcion,
                                String textoCapacidad) {
        nombre = nombre.trim();
        descripcion = descripcion.trim();
        textoCapacidad = textoCapacidad.trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || textoCapacidad.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos",
                    "Debes completar el nombre, la descripción y la capacidad.");
            return null;
        }

        try {
            int capacidad = Integer.parseInt(textoCapacidad);

            if (capacidad <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Capacidad incorrecta",
                        "La capacidad debe ser mayor que cero.");
                return null;
            }

            return new Sala(nombre, descripcion, capacidad);

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Capacidad incorrecta",
                    "La capacidad debe ser un número entero, por ejemplo: 30.");
            return null;
        }
    }

    private void mostrarDetalles(Sala sala) {
        salaSeleccionada = sala;

        txtDetalleNombre.setText(textoSeguro(sala.getNombre()));
        txtDetalleDescripcion.setText(textoSeguro(sala.getDescripcion()));
        txtDetalleCapacidad.setText(String.valueOf(sala.getCapacidad()));

        ocultarFormularioNuevo();
        vistaTabla.setVisible(false);
        vistaTabla.setManaged(false);
        vistaDetalles.setVisible(true);
        vistaDetalles.setManaged(true);

        tablaSalas.getSelectionModel().clearSelection();
        bloquearCamposEdicion();
    }

    private void volverAListado() {
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);

        bloquearCamposEdicion();
        salaSeleccionada = null;
    }

    private void activarEdicion() {
        configurarCamposEdicion(true);
    }

    private void bloquearCamposEdicion() {
        configurarCamposEdicion(false);
    }

    private void configurarCamposEdicion(boolean editable) {
        txtDetalleNombre.setEditable(editable);
        txtDetalleDescripcion.setEditable(editable);
        txtDetalleCapacidad.setEditable(editable);

        String estilo;
        if (editable) {
            estilo = "-fx-background-color: #FFFFFF; "
                    + "-fx-border-color: #C68E17; -fx-border-radius: 4;";
        } else {
            estilo = "-fx-background-color: #EDE7D9; "
                    + "-fx-border-color: #7A5230; -fx-border-radius: 4;";
        }

        txtDetalleNombre.setStyle(estilo);
        txtDetalleDescripcion.setStyle(estilo);
        txtDetalleCapacidad.setStyle(estilo);

        btnEditar.setVisible(!editable);
        btnEditar.setManaged(!editable);
        btnEliminar.setVisible(!editable);
        btnEliminar.setManaged(!editable);
        btnGuardarEdicion.setVisible(editable);
        btnGuardarEdicion.setManaged(editable);
    }

    private void openForm() {
        Utils.openForm(panelFormularioNuevo, spacerLateral);
    }

    private void closeForm() {
        Utils.closeForm(panelFormularioNuevo, spacerLateral);
    }

    private void ocultarFormularioNuevo() {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);
    }

    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevaDescripcion.clear();
        txtNuevaCapacidad.clear();
    }

    // No usamos indexOf() porque Sala.equals() compara por nombre, no por ID.
    private int buscarPosicionPorId(String id) {
        for (int i = 0; i < masterData.size(); i++) {
            if (id != null && id.equals(masterData.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    private void actualizarEstadoTabla() {
        lblEstadoTabla.setText(tablaSalas.getItems().size() + " salas");
    }

    private void mostrarAlerta(Alert.AlertType tipo,
                               String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}