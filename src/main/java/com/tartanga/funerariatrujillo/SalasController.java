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

/**
 * Controlador de la vista de salas. Gestiona el listado con búsqueda, el alta, la edición y la
 * eliminación de salas.
 *
 * @author Aritz
 */
public class SalasController implements Initializable {

    private final SalasDAO salasDAO;

    /**
     * Crea el controlador con el SalasDAO por defecto.
     */
    public SalasController() {
        this.salasDAO = new SalasDAO();
    }

    /**
     * Crea el controlador con el DAO indicado. Permite sustituir el acceso a la base de datos, por
     * ejemplo en las pruebas.
     *
     * @param salasDAO DAO que se utilizará para acceder a la base de datos
     */
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

    /**
     * Inicializa la vista de las salas: configura los botones, la tabla y el buscador, y carga los
     * datos de la base de datos.
     *
     * @param location ubicación del documento FXML; no se utiliza
     * @param resources recursos de internacionalización; no se utilizan
     */
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

    /**
     * Asigna las acciones de los botones de la vista: abrir y cerrar el formulario de alta, guardar,
     * editar, eliminar y volver al listado.
     */
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

    /**
     * Configura las columnas de la tabla, su origen de datos y la selección de filas.
     */
    private void configurarTabla() {
        colID.setCellValueFactory(new PropertyValueFactory<Sala, String>("id"));
        Utils.columnID(colID);
        colNombre.setCellValueFactory(new PropertyValueFactory<Sala, String>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Sala, String>("descripcion"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<Sala, Integer>("capacidad"));

        colID.setReorderable(false);
        colNombre.setReorderable(false);
        colDescripcion.setReorderable(false);
        colCapacidad.setReorderable(false);
    }

    /**
     * Configura el buscador para filtrar la tabla mientras se escribe y actualiza el contador de
     * resultados.
     */
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
     * Carga las salas desde la base de datos en un hilo en segundo plano y actualiza la tabla al
     * terminar. Si la carga falla, avisa al usuario del error de conexión.
     */
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

    /**
     * Valida el formulario de alta y, si los datos son correctos, inserta la sala en la base de datos
     * y recarga la tabla. Si no lo son, muestra un aviso con el motivo.
     */
    private void guardarNuevaSala() {
        txtNuevoNombre.getStyleClass().remove("error");
        txtNuevaDescripcion.getStyleClass().remove("error");
        txtNuevaCapacidad.getStyleClass().remove("error");

        String mensaje = validarDatos(txtNuevoNombre, txtNuevaDescripcion, txtNuevaCapacidad);

        if (mensaje == null && salasDAO.existeSala(txtNuevoNombre.getText().trim(), null)) {
            txtNuevoNombre.getStyleClass().add("error");
            mensaje = "Ya existe una sala con ese nombre.";
        }

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

    /**
     * Valida los datos del panel de detalle y, si son correctos, actualiza la sala en la base de datos
     * y recarga la tabla. Si no lo son, muestra un aviso con el motivo.
     */
    private void guardarEdicion() {
        txtDetalleNombre.getStyleClass().remove("error");
        txtDetalleDescripcion.getStyleClass().remove("error");
        txtDetalleCapacidad.getStyleClass().remove("error");

        String mensaje = validarDatos(txtDetalleNombre, txtDetalleDescripcion, txtDetalleCapacidad);

        String idActual = null;
        if (salaSeleccionada != null) {
            idActual = salaSeleccionada.getId();
        }

        if (mensaje == null && salasDAO.existeSala(txtDetalleNombre.getText().trim(), idActual)) {
            txtDetalleNombre.getStyleClass().add("error");
            mensaje = "Ya existe una sala con ese nombre.";
        }

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

    /**
     * Elimina de la base de datos la sala seleccionada en la tabla y recarga los datos.
     */
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

    /**
     * Comprueba los campos de un formulario de sala y marca como erróneos los que no son válidos.
     *
     * @param txtNombre campo del nombre
     * @param txtDescripcion campo de la descripción
     * @param txtCapacidad campo de la capacidad
     * @return mensaje con el primer error encontrado, o {@code null} si todos los datos son válidos
     */
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

    /**
     * Muestra el detalle de la sala indicada: lo guarda como sala seleccionada, rellena los campos,
     * cambia del listado al panel de detalle y deja los campos en solo lectura.
     *
     * @param sala sala cuyos datos se van a mostrar
     */
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

    /**
     * Vuelve del panel de detalle al listado, bloquea los campos y descarta la selección.
     */
    private void volverAListado() {
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);

        bloquearCamposEdicion();
        salaSeleccionada = null;
    }

    /**
     * Deja los campos del panel de detalle en modo solo lectura.
     */
    private void bloquearCamposEdicion() {
        configurarCamposEdicion(false);
    }

    /**
     * Habilita o deshabilita la edición de los campos del panel de detalle.
     *
     * @param editable {@code true} para permitir la edición; {@code false} para dejar los campos en
     *         solo lectura
     */
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
     * Oculta el panel lateral del formulario de alta sin modificar sus campos.
     */
    private void ocultarFormularioNuevo() {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);
    }

    /**
     * Vacía los campos del formulario de alta.
     */
    private void limpiarFormularioNuevo() {
        txtNuevoNombre.clear();
        txtNuevaDescripcion.clear();
        txtNuevaCapacidad.clear();
    }

    /**
     * Busca la posición de un elemento en la lista de datos a partir de su identificador.
     *
     * @param id identificador que se busca
     * @return posición del elemento en la lista, o -1 si no se encuentra
     */
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

    /**
     * Actualiza el contador de resultados y el texto de la tabla según los elementos visibles.
     */
    private void actualizarEstadoTabla() {
        lblEstadoTabla.setText(tablaSalas.getItems().size() + " salas");
    }

    /**
     * Muestra una ventana de aviso y espera a que el usuario la cierre.
     *
     * @param tipo tipo de aviso
     * @param titulo título de la ventana
     * @param mensaje mensaje que se muestra
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
