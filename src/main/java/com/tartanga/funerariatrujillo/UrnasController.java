package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.UrnaDAO;
import com.tartanga.funerariatrujillo.Objects.Urna;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class UrnasController implements Initializable {

    private final UrnaDAO urnaDAO;

    public UrnasController() {
        this.urnaDAO = new UrnaDAO();
    }

    public UrnasController(UrnaDAO urnaDAO) {
        this.urnaDAO = urnaDAO;
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
    private TableView<Urna> tablaUrnas;

    @FXML
    private Button btnNuevaUrna;
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
    private Button btnEliminar;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Label lblEstadoTabla;
    @FXML
    private TableColumn<Urna, String> colID;
    @FXML
    private TableColumn<Urna, Double> colCapacidad;
    @FXML
    private TableColumn<Urna, String> colMaterial;
    @FXML
    private TableColumn<Urna, Double> colPrecio;
    @FXML
    private TextField txtDetalleCapacidad;
    @FXML
    private TextField txtDetalleMaterial;
    @FXML
    private TextField txtDetallePrecio;
    @FXML
    private TextField txtNuevaCapacidad;
    @FXML
    private TextField txtNuevoMaterial;
    @FXML
    private TextField txtNuevoPrecio;

    private final ObservableList<Urna> masterData = FXCollections.observableArrayList();
    private Urna urnaSeleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelFormularioNuevo.setTranslateX(400);
        panelFormularioNuevo.setVisible(false);
        spacerLateral.setPrefWidth(0);
        spacerLateral.setMinWidth(0);

        btnNuevaUrna.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openForm();
            }
        });

        btnGuardarNuevo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                guardarNuevaUrna();
            }
        });

        btnEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eliminarUrnaSeleccionada();
            }
        });

        btnCerrarFormulario.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeForm();
            }
        });

        FilteredList<Urna> filteredData = new FilteredList<>(masterData, new Predicate<Urna>() {
            @Override
            public boolean test(Urna urna) {
                return true;
            }
        });

        txtBuscar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<Urna>() {
                    @Override
                    public boolean test(Urna urna) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        if (urna.getId().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else if (urna.getMaterial().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                    }
                });
                lblEstadoTabla.setText(filteredData.size() + " urnas");
            }
        });

        SortedList<Urna> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaUrnas.comparatorProperty());
        tablaUrnas.setItems(sortedData);

        colMaterial.setSortType(TableColumn.SortType.ASCENDING);
        tablaUrnas.getSortOrder().add(colMaterial);

        colID.setReorderable(false);
        colCapacidad.setReorderable(false);
        colMaterial.setReorderable(false);
        colPrecio.setReorderable(false);

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        Utils.columnID(colID);

        colCapacidad.setCellFactory(new Callback<TableColumn<Urna, Double>, TableCell<Urna, Double>>() {
            @Override
            public TableCell<Urna, Double> call(TableColumn<Urna, Double> column) {
                return new TableCell<Urna, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format(java.util.Locale.US, "%.1f L", item).replace(".0 L", " L"));
                        }
                    }
                };
            }
        });

        colPrecio.setCellFactory(new Callback<TableColumn<Urna, Double>, TableCell<Urna, Double>>() {
            @Override
            public TableCell<Urna, Double> call(TableColumn<Urna, Double> column) {
                return new TableCell<Urna, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%.2f €", item));
                        }
                    }
                };
            }
        });

        cargarDatos();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
    }

    private void cargarDatos() {
        Task<List<Urna>> taskObtenerUrnas = new Task<List<Urna>>() {
            @Override
            protected List<Urna> call() throws Exception {
                return urnaDAO.obtenerUrnas();
            }
        };

        taskObtenerUrnas.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                List<Urna> resultado = taskObtenerUrnas.getValue();
                masterData.setAll(resultado);
                tablaUrnas.sort();
                lblEstadoTabla.setText(masterData.size() + " urnas");
            }
        });

        taskObtenerUrnas.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lblEstadoTabla.setText("Error de conexión");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de conexión");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo cargar la lista de urnas desde la base de datos.");
                alert.showAndWait();
            }
        });

        Thread hiloSecundario = new Thread(taskObtenerUrnas);
        hiloSecundario.setDaemon(true);
        hiloSecundario.start();
    }

    private void guardarNuevaUrna() {
        txtNuevaCapacidad.getStyleClass().remove("error");
        txtNuevoMaterial.getStyleClass().remove("error");
        txtNuevoPrecio.getStyleClass().remove("error");

        boolean hayError = false;

        if (txtNuevaCapacidad.getText().isBlank()) {
            txtNuevaCapacidad.getStyleClass().add("error");
            hayError = true;
        }

        if (txtNuevoMaterial.getText().isBlank()) {
            txtNuevoMaterial.getStyleClass().add("error");
            hayError = true;
        }

        if (txtNuevoPrecio.getText().isBlank()) {
            txtNuevoPrecio.getStyleClass().add("error");
            hayError = true;
        }

        if (hayError) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Faltan datos");
            alert.setHeaderText(null);
            alert.setContentText("Todos los campos son obligatorios.");
            alert.showAndWait();
            return;
        }

        Urna nuevaUrna = new Urna();
        nuevaUrna.setCapacidad(Double.valueOf(txtNuevaCapacidad.getText().trim()));
        nuevaUrna.setMaterial(txtNuevoMaterial.getText().trim());
        nuevaUrna.setPrecio(Double.valueOf(txtNuevoPrecio.getText().trim()));

        if (urnaDAO.insertarUrna(nuevaUrna)) {
            masterData.add(nuevaUrna);
            tablaUrnas.sort();
            lblEstadoTabla.setText(masterData.size() + " urnas");

            limpiarFormularioNuevo();
            closeForm();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Hubo un problema al guardar la urna en la base de datos.");
            alert.showAndWait();
        }
    }

    private void limpiarFormularioNuevo() {
        txtNuevaCapacidad.clear();
        txtNuevoMaterial.clear();
        txtNuevoPrecio.clear();
    }

    private void eliminarUrnaSeleccionada() {
        if (urnaSeleccionada != null) {

            if (urnaDAO.eliminarUrna(urnaSeleccionada.getId())) {
                masterData.remove(urnaSeleccionada);
                lblEstadoTabla.setText(masterData.size() + " urnas");

                vistaDetalles.setVisible(false);
                vistaDetalles.setManaged(false);
                vistaTabla.setVisible(true);
                vistaTabla.setManaged(true);

                urnaSeleccionada = null;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un problema al eliminar la urna de la base de datos.");
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
        tablaUrnas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Urna newSelection = tablaUrnas.getSelectionModel().getSelectedItem();

                if (newSelection != null) {
                    urnaSeleccionada = newSelection;

                    try {
                        txtDetalleCapacidad.setText("" + newSelection.getCapacidad());
                        txtDetalleMaterial.setText(newSelection.getMaterial());
                        txtDetallePrecio.setText("" + newSelection.getPrecio());

                        panelFormularioNuevo.setTranslateX(400);
                        panelFormularioNuevo.setVisible(false);
                        spacerLateral.setPrefWidth(0);
                        spacerLateral.setMinWidth(0);
                        vistaTabla.setVisible(false);
                        vistaTabla.setManaged(false);
                        vistaDetalles.setVisible(true);
                        vistaDetalles.setManaged(true);
                        tablaUrnas.getSelectionModel().clearSelection();

                    } catch (Exception ex) {
                        System.err.println("Error al cargar la urna seleccionada: " + ex.getMessage());
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
                txtDetalleCapacidad.setEditable(true);
                txtDetalleMaterial.setEditable(true);
                txtDetallePrecio.setEditable(true);

                txtDetalleCapacidad.getStyleClass().remove("error");
                txtDetalleMaterial.getStyleClass().remove("error");
                txtDetallePrecio.getStyleClass().remove("error");

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
                txtDetalleCapacidad.getStyleClass().remove("error");
                txtDetalleMaterial.getStyleClass().remove("error");
                txtDetallePrecio.getStyleClass().remove("error");

                boolean hayError = false;

                if (txtDetalleCapacidad.getText().isBlank()) {
                    txtDetalleCapacidad.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetalleMaterial.getText().isBlank()) {
                    txtDetalleMaterial.getStyleClass().add("error");
                    hayError = true;
                }

                if (txtDetallePrecio.getText().isBlank()) {
                    txtDetallePrecio.getStyleClass().add("error");
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

                if (urnaSeleccionada != null) {
                    urnaSeleccionada.setCapacidad(Double.valueOf(txtDetalleCapacidad.getText()));
                    urnaSeleccionada.setMaterial(txtDetalleMaterial.getText());
                    urnaSeleccionada.setPrecio(Double.valueOf(txtDetallePrecio.getText()));

                    if (urnaDAO.actualizarUrna(urnaSeleccionada)) {
                        tablaUrnas.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Hubo un error al actualizar los datos en la base de datos.");
                        alert.showAndWait();
                    }
                }

                bloquearCamposEdicion();
            }
        });
    }

    private void bloquearCamposEdicion() {
        txtDetalleCapacidad.setEditable(false);
        txtDetalleMaterial.setEditable(false);
        txtDetallePrecio.setEditable(false);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);
    }
}
