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

/**
 * Controlador de la vista de féretros. Gestiona el listado con búsqueda, el alta, la edición y la
 * eliminación de féretros.
 *
 * @author Aritz
 */
public class FeretroController implements Initializable {

    private final FeretroDAO feretroDAO;

    /**
     * Crea el controlador con el FeretroDAO por defecto.
     */
    public FeretroController() {
        this.feretroDAO = new FeretroDAO();
    }

    /**
     * Crea el controlador con el DAO indicado. Permite sustituir el acceso a la base de datos, por
     * ejemplo en las pruebas.
     *
     * @param feretroDAO DAO que se utilizará para acceder a la base de datos
     */
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

    /**
     * Inicializa la vista de los féretros: configura los botones, la tabla y el buscador, y carga los
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

    /**
     * Configura las columnas de la tabla, su origen de datos y la selección de filas.
     */
    private void configurarTabla() {
        colID.setCellValueFactory(new PropertyValueFactory<Feretro, String>("id"));
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

    /**
     * Configura el buscador para filtrar la tabla mientras se escribe y actualiza el contador de
     * resultados.
     */
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
     * Carga los féretros desde la base de datos en un hilo en segundo plano y actualiza la tabla al
     * terminar. Si la carga falla, avisa al usuario del error de conexión.
     */
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

    /**
     * Valida el formulario de alta y, si los datos son correctos, inserta el féretro en la base de
     * datos y recarga la tabla. Si no lo son, muestra un aviso con el motivo.
     */
    private void guardarNuevoFeretro() {
        txtNuevasMedidas.getStyleClass().remove("error");
        txtNuevoMaterial.getStyleClass().remove("error");
        txtNuevoPeso.getStyleClass().remove("error");
        txtNuevoPrecio.getStyleClass().remove("error");

        String mensaje = validarDatos(txtNuevasMedidas, txtNuevoMaterial, txtNuevoPeso, txtNuevoPrecio);

        if (mensaje == null && feretroDAO.existeFeretro(txtNuevasMedidas.getText().trim(),
                txtNuevoMaterial.getText().trim(), null)) {
            txtNuevasMedidas.getStyleClass().add("error");
            txtNuevoMaterial.getStyleClass().add("error");
            mensaje = "Ya existe un féretro con esas medidas y ese material.";
        }

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

    /**
     * Valida los datos del panel de detalle y, si son correctos, actualiza el féretro en la base de
     * datos y recarga la tabla. Si no lo son, muestra un aviso con el motivo.
     */
    private void guardarEdicion() {
        txtDetalleMedidas.getStyleClass().remove("error");
        txtDetalleMaterial.getStyleClass().remove("error");
        txtDetallePeso.getStyleClass().remove("error");
        txtDetallePrecio.getStyleClass().remove("error");

        String mensaje = validarDatos(txtDetalleMedidas, txtDetalleMaterial, txtDetallePeso, txtDetallePrecio);

        String idActual = null;
        if (feretroSeleccionado != null) {
            idActual = feretroSeleccionado.getId();
        }

        if (mensaje == null && feretroDAO.existeFeretro(txtDetalleMedidas.getText().trim(),
                txtDetalleMaterial.getText().trim(), idActual)) {
            txtDetalleMedidas.getStyleClass().add("error");
            txtDetalleMaterial.getStyleClass().add("error");
            mensaje = "Ya existe un féretro con esas medidas y ese material.";
        }

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

    /**
     * Elimina de la base de datos el féretro seleccionado en la tabla y recarga los datos.
     */
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

    /**
     * Comprueba los campos de un formulario de féretro y marca como erróneos los que no son válidos.
     *
     * @param txtMedidas campo de las medidas
     * @param txtMaterial campo del material
     * @param txtPeso campo del peso
     * @param txtPrecio campo del precio
     * @return mensaje con el primer error encontrado, o {@code null} si todos los datos son válidos
     */
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

    /**
     * Convierte un texto en un número, admitiendo la coma como separador decimal.
     *
     * @param texto texto con el número
     * @return valor numérico
     * @throws NumberFormatException si el texto no es un número
     */
    private double convertirNumero(String texto) {
        return Double.parseDouble(texto.trim().replace(",", "."));
    }

    /**
     * Muestra el detalle del féretro indicado: lo guarda como féretro seleccionado, rellena los
     * campos, cambia del listado al panel de detalle y deja los campos en solo lectura.
     *
     * @param feretro féretro cuyos datos se van a mostrar
     */
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

    /**
     * Vuelve del panel de detalle al listado, bloquea los campos y descarta la selección.
     */
    private void volverAListado() {
        vistaDetalles.setVisible(false);
        vistaDetalles.setManaged(false);
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(true);

        bloquearCamposEdicion();
        feretroSeleccionado = null;
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
        txtNuevasMedidas.clear();
        txtNuevoMaterial.clear();
        txtNuevoPeso.clear();
        txtNuevoPrecio.clear();
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
        lblEstadoTabla.setText(tablaFeretros.getItems().size() + " féretros");
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