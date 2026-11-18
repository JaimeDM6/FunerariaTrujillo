package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.FeretroDAO;
import com.tartanga.funerariatrujillo.Objects.Feretro;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class FeretroController implements Initializable {

    @FXML private VBox vistaTabla;
    @FXML private VBox vistaDetalles;
    @FXML private VBox panelFormularioNuevo;
    @FXML private Region spacerLateral;

    @FXML private TableView<Feretro> tablaFeretros;
    @FXML private TextField txtBuscar;
    @FXML private Label lblEstadoTabla;

    @FXML private TableColumn<Feretro, String> colID;
    @FXML private TableColumn<Feretro, String> colMedidas;
    @FXML private TableColumn<Feretro, String> colMaterial;
    @FXML private TableColumn<Feretro, Double> colPeso;
    @FXML private TableColumn<Feretro, Double> colPrecio;

    @FXML private Button btnNuevoFeretro;
    @FXML private Button btnCerrarFormulario;
    @FXML private Button btnVolver;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardarEdicion;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardarNuevo;

    @FXML private TextField txtNuevasMedidas;
    @FXML private TextField txtNuevoMaterial;
    @FXML private TextField txtNuevoPeso;
    @FXML private TextField txtNuevoPrecio;

    @FXML private TextField txtDetalleMedidas;
    @FXML private TextField txtDetalleMaterial;
    @FXML private TextField txtDetallePeso;
    @FXML private TextField txtDetallePrecio;

    private final ObservableList<Feretro> masterData =
            FXCollections.observableArrayList();

    private Feretro feretroSeleccionado;

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
        btnNuevoFeretro.setOnAction(event -> openForm());
        btnCerrarFormulario.setOnAction(event -> closeForm());
        btnGuardarNuevo.setOnAction(event -> guardarNuevoFeretro());
        btnVolver.setOnAction(event -> volverAListado());
        btnEditar.setOnAction(event -> activarEdicion());
        btnGuardarEdicion.setOnAction(event -> guardarEdicion());
        btnEliminar.setOnAction(event -> eliminarFeretroSeleccionado());

        tablaFeretros.setOnMouseClicked(event -> {
            Feretro seleccion = tablaFeretros.getSelectionModel().getSelectedItem();

            if (seleccion != null) {
                mostrarDetalles(seleccion);
            }
        });
    }

    private void configurarTabla() {
        colID.setCellValueFactory(
                new PropertyValueFactory<Feretro, String>("id"));
        colMedidas.setCellValueFactory(
                new PropertyValueFactory<Feretro, String>("medidas"));
        colMaterial.setCellValueFactory(
                new PropertyValueFactory<Feretro, String>("material"));
        colPeso.setCellValueFactory(
                new PropertyValueFactory<Feretro, Double>("peso"));

        colPrecio.setCellValueFactory(new PropertyValueFactory<Feretro, Double>("precio"));
        colPrecio.setReorderable(false);
        colID.setReorderable(false);
        colMedidas.setReorderable(false);
        colMaterial.setReorderable(false);
        colPeso.setReorderable(false);
    }

    private void configurarBusqueda() {
        FilteredList<Feretro> filtradas = new FilteredList<>(masterData);

        txtBuscar.textProperty().addListener((observable, anterior, nuevo) -> {
            String filtro = textoSeguro(nuevo).trim().toLowerCase(Locale.ROOT);

            filtradas.setPredicate(feretro ->
                    textoSeguro(feretro.getId()).toLowerCase(Locale.ROOT).contains(filtro)
                    || textoSeguro(feretro.getMedidas()).toLowerCase(Locale.ROOT).contains(filtro)
                    || textoSeguro(feretro.getMaterial()).toLowerCase(Locale.ROOT).contains(filtro)
            );

            actualizarEstadoTabla();
        });

        SortedList<Feretro> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tablaFeretros.comparatorProperty());
        tablaFeretros.setItems(ordenadas);
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private void cargarDatos() {
        lblEstadoTabla.setText("Cargando féretros...");
        vistaTabla.setDisable(true);

        Task<List<Feretro>> tarea = new Task<List<Feretro>>() {
            @Override
            protected List<Feretro> call() {
                return new FeretroDAO().obtenerFeretros();
            }
        };

        tarea.setOnSucceeded(event -> {
            vistaTabla.setDisable(false);
            masterData.setAll(tarea.getValue());
            tablaFeretros.sort();
            actualizarEstadoTabla();
        });

        tarea.setOnFailed(event -> {
            vistaTabla.setDisable(false);
            lblEstadoTabla.setText("Error de conexión");
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                    "No se han podido cargar los féretros desde MongoDB.");
        });

        iniciarTarea(tarea);
    }

    private void guardarNuevoFeretro() {
        Feretro nueva = leerFormulario(
                txtNuevasMedidas.getText(),
                txtNuevoMaterial.getText(),
                txtNuevoPeso.getText(),
                txtNuevoPrecio.getText()
        );

        if (nueva == null) {
            return;
        }

        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return new FeretroDAO().insertarFeretro(nueva);
            }
        };

        ejecutarOperacion(tarea, () -> {
            masterData.add(nueva);
            tablaFeretros.sort();
            actualizarEstadoTabla();
            limpiarFormularioNuevo();
            closeForm();
        }, "No se ha podido guardar el féretro.");
    }

    private void guardarEdicion() {
        if (feretroSeleccionado == null) {
            return;
        }

        // El objeto original solo se sustituye si MongoDB guarda los cambios.
        Feretro modificada = leerFormulario(
                txtDetalleMedidas.getText(),
                txtDetalleMaterial.getText(),
                txtDetallePeso.getText(),
                txtDetallePrecio.getText()
        );

        if (modificada == null) {
            return;
        }

        modificada.setId(feretroSeleccionado.getId());

        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return new FeretroDAO().actualizarFeretro(modificada);
            }
        };

        ejecutarOperacion(tarea, () -> {
            int posicion = buscarPosicionPorId(modificada.getId());
            if (posicion >= 0) {
                masterData.set(posicion, modificada);
            }
            feretroSeleccionado = modificada;
            tablaFeretros.sort();
            actualizarEstadoTabla();
            mostrarDetalles(modificada);
        }, "No se ha podido actualizar el féretro.");
    }

    private void eliminarFeretroSeleccionado() {
        if (feretroSeleccionado == null) {
            return;
        }

        String id = feretroSeleccionado.getId();

        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                return new FeretroDAO().eliminarFeretro(id);
            }
        };

        ejecutarOperacion(tarea, () -> {
            int posicion = buscarPosicionPorId(id);
            if (posicion >= 0) {
                masterData.remove(posicion);
            }
            actualizarEstadoTabla();
            volverAListado();
        }, "No se ha podido eliminar el féretro.");
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

    // Se usa tanto al crear un féretro como al editarlo.
    private Feretro leerFormulario(String medidas, String material,
                                   String textoPeso, String textoPrecio) {
        if (medidas.trim().isEmpty() || material.trim().isEmpty()
                || textoPeso.trim().isEmpty() || textoPrecio.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos",
                    "Completa las medidas, el material, el peso y el precio.");
            return null;
        }
        try {
            double peso = Double.parseDouble(textoPeso.trim().replace(",", "."));
            double precio = Double.parseDouble(textoPrecio.trim().replace(",", "."));
            if (!Double.isFinite(peso) || !Double.isFinite(precio)
                    || peso <= 0 || precio < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Datos incorrectos",
                        "El peso debe ser mayor que cero y el precio no negativo.");
                return null;
            }
            return new Feretro(medidas.trim(), material.trim(), peso, precio);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incorrectos",
                    "El peso y el precio deben ser números, por ejemplo: 80,50.");
            return null;
        }
    }

    private void mostrarDetalles(Feretro feretro) {
        feretroSeleccionado = feretro;

        txtDetalleMedidas.setText(textoSeguro(feretro.getMedidas()));
        txtDetalleMaterial.setText(textoSeguro(feretro.getMaterial()));
        txtDetallePeso.setText(String.valueOf(feretro.getPeso()));
        txtDetallePrecio.setText(String.valueOf(feretro.getPrecio()));

        ocultarFormularioNuevo();
        vistaTabla.setVisible(false);
        vistaTabla.setManaged(false);
        vistaDetalles.setVisible(true);
        vistaDetalles.setManaged(true);

        tablaFeretros.getSelectionModel().clearSelection();
        bloquearCamposEdicion();
    }

    private void volverAListado() {
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);

        bloquearCamposEdicion();
        feretroSeleccionado = null;
    }

    private void activarEdicion() {
        configurarCamposEdicion(true);
    }

    private void bloquearCamposEdicion() {
        configurarCamposEdicion(false);
    }

    private void configurarCamposEdicion(boolean editable) {
        txtDetalleMedidas.setEditable(editable);
        txtDetalleMaterial.setEditable(editable);
        txtDetallePeso.setEditable(editable);
        txtDetallePrecio.setEditable(editable);

        String estilo;
        if (editable) {
            estilo = "-fx-background-color: #FFFFFF; "
                    + "-fx-border-color: #C68E17; -fx-border-radius: 4;";
        } else {
            estilo = "-fx-background-color: #EDE7D9; "
                    + "-fx-border-color: #7A5230; -fx-border-radius: 4;";
        }

        txtDetalleMedidas.setStyle(estilo);
        txtDetalleMaterial.setStyle(estilo);
        txtDetallePeso.setStyle(estilo);
        txtDetallePrecio.setStyle(estilo);

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
        txtNuevasMedidas.clear();
        txtNuevoMaterial.clear();
        txtNuevoPeso.clear();
        txtNuevoPrecio.clear();
    }

    // No usamos indexOf() porque Feretro.equals() compara por medidas, no por ID.
    private int buscarPosicionPorId(String id) {
        for (int i = 0; i < masterData.size(); i++) {
            if (id != null && id.equals(masterData.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    private void actualizarEstadoTabla() {
        lblEstadoTabla.setText(tablaFeretros.getItems().size() + " féretros");
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
