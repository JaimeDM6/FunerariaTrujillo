package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.SalasDAO;
import com.tartanga.funerariatrujillo.Objects.Sala;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class SalasController implements Initializable {

    private final SalasDAO salasDAO;

    public SalasController() {
        this.salasDAO = new SalasDAO();
    }

    public SalasController(SalasDAO salasDAO) {
        this.salasDAO = salasDAO;
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
    private TableView<Sala> tablaSalas;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblEstadoTabla;

    @FXML
    private TableColumn<Sala, String> colID;
    @FXML
    private TableColumn<Sala, String> colNombre;
    @FXML
    private TableColumn<Sala, String> colDescripcion;
    @FXML
    private TableColumn<Sala, Integer> colCapacidad;

    @FXML
    private Button btnNuevaSala;
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
    private TextArea txtNuevaDescripcion;
    @FXML
    private TextField txtNuevaCapacidad;

    @FXML
    private TextField txtDetalleNombre;
    @FXML
    private TextArea txtDetalleDescripcion;
    @FXML
    private TextField txtDetalleCapacidad;

    private final ObservableList<Sala> masterData = FXCollections.observableArrayList();
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
        btnNuevaSala.setOnAction(new EventHandler<ActionEvent>() {
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
                guardarNuevaSala();
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
                eliminarSalaSeleccionada();
            }
        });

        tablaSalas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Sala seleccion = tablaSalas.getSelectionModel().getSelectedItem();

                if (seleccion != null) {
                    mostrarDetalles(seleccion);
                }
            }
        });
    }

    private void configurarTabla() {
        Utils.columnID(colID);
        colNombre.setCellValueFactory(new PropertyValueFactory<Sala, String>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Sala, String>("descripcion"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<Sala, Integer>("capacidad"));

        colID.setReorderable(false);
        colNombre.setReorderable(false);
        colDescripcion.setReorderable(false);
        colCapacidad.setReorderable(false);
    }

    private void configurarBusqueda() {
        FilteredList<Sala> filtradas = new FilteredList<>(masterData, new Predicate<Sala>() {
            @Override
            public boolean test(Sala sala) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String anterior, String nuevo) {
                String filtro = textoSinNulos(nuevo).trim().toLowerCase();

                filtradas.setPredicate(new Predicate<Sala>() {
                    @Override
                    public boolean test(Sala sala) {
                        return textoSinNulos(sala.getId()).toLowerCase().contains(filtro)
                                || textoSinNulos(sala.getNombre()).toLowerCase().contains(filtro)
                                || textoSinNulos(sala.getDescripcion()).toLowerCase().contains(filtro);
                    }
                });

                actualizarEstadoTabla();
            }
        });

        SortedList<Sala> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tablaSalas.comparatorProperty());
        tablaSalas.setItems(ordenadas);
    }

    private String textoSinNulos(String texto) {
        String resultado = "";
        if (texto != null) {
            resultado = texto;
        }
        return resultado;
    }

    private void cargarDatos() {
        lblEstadoTabla.setText("Cargando salas...");
        vistaTabla.setDisable(true);

        Task<List<Sala>> tarea = new Task<List<Sala>>() {
            @Override
            protected List<Sala> call() throws Exception {
                return salasDAO.obtenerSalas();
            }
        };

        tarea.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                vistaTabla.setDisable(false);
                masterData.setAll(tarea.getValue());
                tablaSalas.sort();
                actualizarEstadoTabla();
            }
        });

        tarea.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                vistaTabla.setDisable(false);
                lblEstadoTabla.setText("Error de conexión");
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                        "No se han podido cargar las salas desde la base de datos.");
            }
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void guardarNuevaSala() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevaDescripcion.getStyleClass().remove("error");
        txtNuevaCapacidad.getStyleClass().remove("error");

        String mensaje = validarDatos(txtNuevoNombre, txtNuevaDescripcion, txtNuevaCapacidad);

        if (mensaje == null) {
            Sala nueva = new Sala(
                    txtNuevoNombre.getText().trim(),
                    txtNuevaDescripcion.getText().trim(),
                    Integer.parseInt(txtNuevaCapacidad.getText().trim()));

            if (salasDAO.insertarSala(nueva)) {
                masterData.add(nueva);
                tablaSalas.sort();
                actualizarEstadoTabla();
                limpiarFormularioNuevo();
                closeForm();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Hubo un problema al guardar la sala en la base de datos.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
        }
    }

    private void guardarEdicion() {
        txtDetalleNombre.getStyleClass().remove("error");
        txtDetalleDescripcion.getStyleClass().remove("error");
        txtDetalleCapacidad.getStyleClass().remove("error");

        String mensaje = validarDatos(txtDetalleNombre, txtDetalleDescripcion, txtDetalleCapacidad);

        if (mensaje == null) {
            if (salaSeleccionada != null) {
                Sala modificada = new Sala(
                        txtDetalleNombre.getText().trim(),
                        txtDetalleDescripcion.getText().trim(),
                        Integer.parseInt(txtDetalleCapacidad.getText().trim()));
                modificada.setId(salaSeleccionada.getId());

                if (salasDAO.actualizarSala(modificada)) {
                    int posicion = buscarPosicionPorId(modificada.getId());
                    if (posicion >= 0) {
                        masterData.set(posicion, modificada);
                    }
                    salaSeleccionada = modificada;
                    tablaSalas.sort();
                    actualizarEstadoTabla();
                    mostrarDetalles(modificada);
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "Hubo un error al actualizar los datos en la base de datos.");
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
        }
    }

    private void eliminarSalaSeleccionada() {
        if (salaSeleccionada != null) {
            String id = salaSeleccionada.getId();

            if (salasDAO.eliminarSala(id)) {
                int posicion = buscarPosicionPorId(id);
                if (posicion >= 0) {
                    masterData.remove(posicion);
                }
                actualizarEstadoTabla();
                volverAListado();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Hubo un problema al eliminar la sala de la base de datos.");
            }
        }
    }

    private String validarDatos(TextField txtNombre, TextArea txtDescripcion, TextField txtCapacidad) {
        String mensaje = null;
        boolean hayError = false;

        if (txtNombre.getText().isBlank()) {
            txtNombre.getStyleClass().add("error");
            hayError = true;
        }

        if (txtDescripcion.getText().isBlank()) {
            txtDescripcion.getStyleClass().add("error");
            hayError = true;
        }

        if (txtCapacidad.getText().isBlank()) {
            txtCapacidad.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            mensaje = "Debes completar el nombre, la descripción y la capacidad.";
        } else {
            try {
                int capacidad = Integer.parseInt(txtCapacidad.getText().trim());

                if (capacidad <= 0) {
                    txtCapacidad.getStyleClass().add("error");
                    mensaje = "La capacidad debe ser mayor que cero.";
                }
            } catch (NumberFormatException ex) {
                txtCapacidad.getStyleClass().add("error");
                mensaje = "La capacidad debe ser un número entero, por ejemplo: 30.";
            }
        }

        return mensaje;
    }

    private void mostrarDetalles(Sala sala) {
        salaSeleccionada = sala;

        txtDetalleNombre.setText(textoSinNulos(sala.getNombre()));
        txtDetalleDescripcion.setText(textoSinNulos(sala.getDescripcion()));
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

    private void bloquearCamposEdicion() {
        configurarCamposEdicion(false);
    }

    private void configurarCamposEdicion(boolean editable) {
        txtDetalleNombre.setEditable(editable);
        txtDetalleDescripcion.setEditable(editable);
        txtDetalleCapacidad.setEditable(editable);

        String estilo;
        if (editable) {
            estilo = "-fx-background-color: #FFFFFF; -fx-border-color: #C68E17; -fx-border-radius: 4;";
        } else {
            estilo = "-fx-background-color: #EDE7D9; -fx-border-color: #7A5230; -fx-border-radius: 4;";
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
        lblEstadoTabla.setText(tablaSalas.getItems().size() + " salas");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
