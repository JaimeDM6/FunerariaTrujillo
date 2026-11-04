package com.tartanga.funerariatrujillo;

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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        navegacion();
    }

    private void navegacion() {
        btnClientes.setOnAction(e -> mainController.cargarVista("clientes.fxml"));
        btnDifuntos.setOnAction(e -> mainController.cargarVista("difuntos.fxml"));
        btnTrabajadores.setOnAction(e -> mainController.cargarVista("trabajadores.fxml"));
        btnServicios.setOnAction(e -> mainController.cargarVista("servicios.fxml"));
        btnFeretros.setOnAction(e -> mainController.cargarVista("feretros.fxml"));
        btnUrnas.setOnAction(e -> mainController.cargarVista("urnas.fxml"));
        btnSalas.setOnAction(e -> mainController.cargarVista("salas.fxml"));
        btnAsignaciones.setOnAction(e -> mainController.cargarVista("asignaciones.fxml"));
        btnFichaServicio.setOnAction(e -> mainController.cargarVista("fichaServicio.fxml"));
    }
}
