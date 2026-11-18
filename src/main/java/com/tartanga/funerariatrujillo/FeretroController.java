package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.FeretroDAO;
import com.tartanga.funerariatrujillo.Objects.Feretro;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class FeretroController implements Initializable {

    private final FeretroDAO feretroDAO;

    public FeretroController() {
        this.feretroDAO = new FeretroDAO();
    }

    public FeretroController(FeretroDAO feretroDAO) {
        this.feretroDAO = feretroDAO;
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
    private TableView<Feretro> tablaFeretros;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblEstadoTabla;

    @FXML
    private TableColumn<Feretro, String> colID;
    @FXML
    private TableColumn<Feretro, String> colMedidas;
    @FXML
    private TableColumn<Feretro, String> colMaterial;
    @FXML
    private TableColumn<Feretro, Double> colPeso;
    @FXML
    private TableColumn<Feretro, Double> colPrecio;

    @FXML
    private Button btnNuevoFeretro;
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
    private TextField txtNuevasMedidas;
    @FXML
    private TextField txtNuevoMaterial;
    @FXML
    private TextField txtNuevoPeso;
    @FXML
    private TextField txtNuevoPrecio;

    @FXML
    private TextField txtDetalleMedidas;
    @FXML
    private TextField txtDetalleMaterial;
    @FXML
    private TextField txtDetallePeso;
    @FXML
    private TextField txtDetallePrecio;

    private final ObservableList<Feretro> masterData = FXCollections.observableArrayList();
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
        btnNuevoFeretro.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openForm();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevoFeretro();
            }
        });

        btnVolver.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                volverAListado();
            }
        });

        btnEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                configurarCamposEdicion(true);
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
                eliminarFeretroSeleccionado();
            }
        });

        tablaFeretros.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Feretro seleccion = tablaFeretros.getSelectionModel().getSelectedItem();

                if (seleccion != null) {
                    mostrarDetalles(seleccion);
                }
            }
        });
    }

    private void configurarTabla() {
        Utils.columnID(colID);
        colMedidas.setCellValueFactory(new PropertyValueFactory<Feretro, String>("medidas"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<Feretro, String>("material"));
        colPeso.setCellValueFactory(new PropertyValueFactory<Feretro, Double>("peso"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<Feretro, Double>("precio"));

        colID.setReorderable(false);
        colMedidas.setReorderable(false);
        colMaterial.setReorderable(false);
        colPeso.setReorderable(false);
        colPrecio.setReorderable(false);
    }

    private void configurarBusqueda() {
        FilteredList<Feretro> filtradas = new FilteredList<>(masterData, new Predicate<Feretro>() {
            @Override
            public boolean test(Feretro feretro) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String anterior, String nuevo) {
                String filtro = textoSinNulos(nuevo).trim().toLowerCase();

                filtradas.setPredicate(new Predicate<Feretro>() {
                    @Override
                    public boolean test(Feretro feretro) {
                        return textoSinNulos(feretro.getId()).toLowerCase().contains(filtro)
                                || textoSinNulos(feretro.getMedidas()).toLowerCase().contains(filtro)
                                || textoSinNulos(feretro.getMaterial()).toLowerCase().contains(filtro);
                    }
                });

                actualizarEstadoTabla();
            }
        });

        SortedList<Feretro> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tablaFeretros.comparatorProperty());
        tablaFeretros.setItems(ordenadas);
    }

    private String textoSinNulos(String texto) {
        String resultado = "";
        if (texto != null) {
            resultado = texto;
        }
        return resultado;
    }

    private void cargarDatos() {
        lblEstadoTabla.setText("Cargando féretros...");
        vistaTabla.setDisable(true);

        Task<List<Feretro>> tarea = new Task<List<Feretro>>() {
            @Override
            protected List<Feretro> call() throws Exception {
                return feretroDAO.obtenerFeretros();
            }
        };

        tarea.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                vistaTabla.setDisable(false);
                masterData.setAll(tarea.getValue());
                tablaFeretros.sort();
                actualizarEstadoTabla();
            }
        });

        tarea.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                vistaTabla.setDisable(false);
                lblEstadoTabla.setText("Error de conexión");
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                        "No se han podido cargar los féretros desde la base de datos.");
            }
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void guardarNuevoFeretro() {
        txtNuevasMedidas.getStyleClass().remove("error");
        txtNuevoMaterial.getStyleClass().remove("error");
        txtNuevoPeso.getStyleClass().remove("error");
        txtNuevoPrecio.getStyleClass().remove("error");

        String mensaje = validarDatos(txtNuevasMedidas, txtNuevoMaterial, txtNuevoPeso, txtNuevoPrecio);

        if (mensaje == null) {
            Feretro nuevo = new Feretro(
                    txtNuevasMedidas.getText().trim(),
                    txtNuevoMaterial.getText().trim(),
                    convertirNumero(txtNuevoPeso.getText()),
                    convertirNumero(txtNuevoPrecio.getText()));

            if (feretroDAO.insertarFeretro(nuevo)) {
                masterData.add(nuevo);
                tablaFeretros.sort();
                actualizarEstadoTabla();
                limpiarFormularioNuevo();
                closeForm();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Hubo un problema al guardar el féretro en la base de datos.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
        }
    }

    private void guardarEdicion() {
        txtDetalleMedidas.getStyleClass().remove("error");
        txtDetalleMaterial.getStyleClass().remove("error");
        txtDetallePeso.getStyleClass().remove("error");
        txtDetallePrecio.getStyleClass().remove("error");

        String mensaje = validarDatos(txtDetalleMedidas, txtDetalleMaterial, txtDetallePeso, txtDetallePrecio);

        if (mensaje == null) {
            if (feretroSeleccionado != null) {
                Feretro modificado = new Feretro(
                        txtDetalleMedidas.getText().trim(),
                        txtDetalleMaterial.getText().trim(),
                        convertirNumero(txtDetallePeso.getText()),
                        convertirNumero(txtDetallePrecio.getText()));
                modificado.setId(feretroSeleccionado.getId());

                if (feretroDAO.actualizarFeretro(modificado)) {
                    int posicion = buscarPosicionPorId(modificado.getId());
                    if (posicion >= 0) {
                        masterData.set(posicion, modificado);
                    }
                    feretroSeleccionado = modificado;
                    tablaFeretros.sort();
                    actualizarEstadoTabla();
                    mostrarDetalles(modificado);
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "Hubo un error al actualizar los datos en la base de datos.");
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
        }
    }

    private void eliminarFeretroSeleccionado() {
        if (feretroSeleccionado != null) {
            String id = feretroSeleccionado.getId();

            if (feretroDAO.eliminarFeretro(id)) {
                int posicion = buscarPosicionPorId(id);
                if (posicion >= 0) {
                    masterData.remove(posicion);
                }
                actualizarEstadoTabla();
                volverAListado();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Hubo un problema al eliminar el féretro de la base de datos.");
            }
        }
    }

    private String validarDatos(TextField txtMedidas, TextField txtMaterial, TextField txtPeso, TextField txtPrecio) {
        String mensaje = null;
        boolean hayError = false;

        if (txtMedidas.getText().isBlank()) {
            txtMedidas.getStyleClass().add("error");
            hayError = true;
        }

        if (txtMaterial.getText().isBlank()) {
            txtMaterial.getStyleClass().add("error");
            hayError = true;
        }

        if (txtPeso.getText().isBlank()) {
            txtPeso.getStyleClass().add("error");
            hayError = true;
        }

        if (txtPrecio.getText().isBlank()) {
            txtPrecio.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje = "Completa las medidas, el material, el peso y el precio.";
        } else {
            try {
                double peso = convertirNumero(txtPeso.getText());
                double precio = convertirNumero(txtPrecio.getText());

                if (!Double.isFinite(peso) || peso <= 0) {
                    txtPeso.getStyleClass().add("error");
                    mensaje = "El peso debe ser mayor que cero.";
                } else if (!Double.isFinite(precio) || precio < 0) {
                    txtPrecio.getStyleClass().add("error");
                    mensaje = "El precio no puede ser negativo.";
                }
            } catch (NumberFormatException ex) {
                txtPeso.getStyleClass().add("error");
                txtPrecio.getStyleClass().add("error");
                mensaje = "El peso y el precio deben ser números, por ejemplo: 80,50.";
            }
        }

        return mensaje;
    }

    private double convertirNumero(String texto) {
        return Double.parseDouble(texto.trim().replace(",", "."));
    }

    private void mostrarDetalles(Feretro feretro) {
        feretroSeleccionado = feretro;

        txtDetalleMedidas.setText(textoSinNulos(feretro.getMedidas()));
        txtDetalleMaterial.setText(textoSinNulos(feretro.getMaterial()));
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
            estilo = "-fx-background-color: #FFFFFF; -fx-border-color: #C68E17; -fx-border-radius: 4;";
        } else {
            estilo = "-fx-background-color: #EDE7D9; -fx-border-color: #7A5230; -fx-border-radius: 4;";
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

    private int buscarPosicionPorId(String id) {
        int posicion = -1;
        int i = 0;

        while (posicion == -1 && i < masterData.size()) {
            if (id != null && id.equals(masterData.get(i).getId())) {
                posicion = i;
            }
            i++;
        }

        return posicion;
    }

    private void actualizarEstadoTabla() {
        lblEstadoTabla.setText(tablaFeretros.getItems().size() + " féretros");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}