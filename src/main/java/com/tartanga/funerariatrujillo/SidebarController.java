package com.tartanga.funerariatrujillo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 */
public class SidebarController {

    @FXML
    private Button btnEmpresa;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnDifuntos;
    @FXML
    private Button btnServicios;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        navegacion();
    }

    private void navegacion() {
        btnEmpresa.setOnAction(e -> mainController.cargarVista("empresa.fxml"));
        btnClientes.setOnAction(e -> mainController.cargarVista("clientes.fxml"));
        btnDifuntos.setOnAction(e -> mainController.cargarVista("difuntos.fxml"));
        btnServicios.setOnAction(e -> mainController.cargarVista("servicios.fxml"));
    }
}
