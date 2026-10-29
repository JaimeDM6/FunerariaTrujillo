package com.tartanga.funerariatrujillo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientesController implements Initializable {

    @FXML
    private VBox vistaTabla;
    @FXML
    private VBox vistaDetalles;
    @FXML
    private VBox panelFormularioNuevo;
    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private Button btnNuevoCliente;
    @FXML
    private Button btnCerrarFormulario;
    @FXML
    private TextField txtNuevoNombre;
    @FXML
    private TextField txtNuevoDni;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnGuardarEdicion;
    @FXML
    private TextField txtDetalleNombre;
    @FXML
    private TextField txtDetalleApellidos;
    @FXML
    private TextField txtDetalleDni;
    @FXML
    private TextField txtDetalleTelefono;
    @FXML
    private TextField txtDetalleCorreo;
    @FXML
    private TextField txtDetalleDireccion;

    private final double ANCHO_FORMULARIO_LATERAL = 360.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarAnimacionFormularioLateral();
        configurarSeleccionDeTabla();
        configurarModoEdicion();
    }

    private void configurarAnimacionFormularioLateral() {
        btnNuevoCliente.setOnAction(e -> {
            panelFormularioNuevo.setVisible(true);

            Timeline abrir = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(panelFormularioNuevo.maxWidthProperty(), ANCHO_FORMULARIO_LATERAL)
                    )
            );
            abrir.play();
        });

        btnCerrarFormulario.setOnAction(e -> {
            Timeline cerrar = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(panelFormularioNuevo.maxWidthProperty(), 0.0)
                    )
            );
            cerrar.setOnFinished(ev -> panelFormularioNuevo.setVisible(false));
            cerrar.play();
        });
    }

    private void configurarSeleccionDeTabla() {
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                panelFormularioNuevo.setMaxWidth(0);
                panelFormularioNuevo.setVisible(false);
                vistaTabla.setVisible(false);
                vistaDetalles.setVisible(true);
                tablaClientes.getSelectionModel().clearSelection();
            }
        });

        btnVolver.setOnAction(e -> {
            vistaDetalles.setVisible(false);
            vistaTabla.setVisible(true);
            bloquearCamposEdicion();
        });
    }

    private void configurarModoEdicion() {
        btnEditar.setOnAction(e -> {
            txtDetalleNombre.setEditable(true);
            txtDetalleApellidos.setEditable(true);
            txtDetalleDni.setEditable(true);
            txtDetalleTelefono.setEditable(true);
            txtDetalleCorreo.setEditable(true);
            txtDetalleDireccion.setEditable(true);

            String estiloEditable = "-fx-background-color: #FFFFFF; -fx-border-color: #C68E17; -fx-border-radius: 4;";
            txtDetalleNombre.setStyle(estiloEditable);
            txtDetalleApellidos.setStyle(estiloEditable);
            txtDetalleDni.setStyle(estiloEditable);
            txtDetalleTelefono.setStyle(estiloEditable);
            txtDetalleCorreo.setStyle(estiloEditable);
            txtDetalleDireccion.setStyle(estiloEditable);

            btnEditar.setVisible(false);
            btnEditar.setManaged(false);
            btnGuardarEdicion.setVisible(true);
            btnGuardarEdicion.setManaged(true);
        });

        btnGuardarEdicion.setOnAction(e -> {
            bloquearCamposEdicion();
        });
    }

    private void bloquearCamposEdicion() {
        txtDetalleNombre.setEditable(false);
        txtDetalleApellidos.setEditable(false);
        txtDetalleDni.setEditable(false);
        txtDetalleTelefono.setEditable(false);
        txtDetalleCorreo.setEditable(false);
        txtDetalleDireccion.setEditable(false);

        String estiloLectura = "-fx-background-color: #F9F7F3; -fx-border-color: #E2DBD0; -fx-border-radius: 4;";
        txtDetalleNombre.setStyle(estiloLectura);
        txtDetalleApellidos.setStyle(estiloLectura);
        txtDetalleDni.setStyle(estiloLectura);
        txtDetalleTelefono.setStyle(estiloLectura);
        txtDetalleCorreo.setStyle(estiloLectura);
        txtDetalleDireccion.setStyle(estiloLectura);

        btnGuardarEdicion.setVisible(false);
        btnGuardarEdicion.setManaged(false);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
    }
}
