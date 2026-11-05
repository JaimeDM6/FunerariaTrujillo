package com.tartanga.funerariatrujillo;

import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 */
public class SidebarController {

    private MainController mainController;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnDifuntos;
    @FXML
    private Button btnTrabajadores;
    @FXML
    private Button btnServicios;
    @FXML
    private Button btnFeretros;
    @FXML
    private Button btnUrnas;
    @FXML
    private Button btnSalas;
    @FXML
    private Button btnAsignaciones;
    @FXML
    private Button btnFichaServicio;
    @FXML
    private Button btnCerrarSesion;

    private List<Button> navButtons;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;

        navButtons = Arrays.asList(
                btnClientes, btnDifuntos, btnTrabajadores, btnServicios,
                btnFeretros, btnUrnas, btnSalas, btnAsignaciones, btnFichaServicio
        );

        navegacion();
        marcarActivo(btnClientes);
    }

    private void navegacion() {
        btnClientes.setOnAction(e -> {
            mainController.cargarVista("clientes.fxml");
            marcarActivo(btnClientes);
        });
        btnDifuntos.setOnAction(e -> {
            mainController.cargarVista("difuntos.fxml");
            marcarActivo(btnDifuntos);
        });
        btnTrabajadores.setOnAction(e -> {
            mainController.cargarVista("trabajadores.fxml");
            marcarActivo(btnTrabajadores);
        });
        btnServicios.setOnAction(e -> {
            mainController.cargarVista("servicios.fxml");
            marcarActivo(btnServicios);
        });
        btnFeretros.setOnAction(e -> {
            mainController.cargarVista("feretros.fxml");
            marcarActivo(btnFeretros);
        });
        btnUrnas.setOnAction(e -> {
            mainController.cargarVista("urnas.fxml");
            marcarActivo(btnUrnas);
        });
        btnSalas.setOnAction(e -> {
            mainController.cargarVista("salas.fxml");
            marcarActivo(btnSalas);
        });
        btnAsignaciones.setOnAction(e -> {
            mainController.cargarVista("asignaciones.fxml");
            marcarActivo(btnAsignaciones);
        });
        btnFichaServicio.setOnAction(e -> {
            mainController.cargarVista("fichaServicio.fxml");
            marcarActivo(btnFichaServicio);
        });
    }

    private void marcarActivo(Button botonActivo) {
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("button-active");
            if (!btn.getStyleClass().contains("button-inactive")) {
                btn.getStyleClass().add("button-inactive");
            }
        }

        botonActivo.getStyleClass().remove("button-inactive");
        if (!botonActivo.getStyleClass().contains("button-active")) {
            botonActivo.getStyleClass().add("button-active");
        }
    }
}
