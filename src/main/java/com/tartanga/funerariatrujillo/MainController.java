package com.tartanga.funerariatrujillo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane rootLayout;
    @FXML
    private StackPane contentArea;

    @FXML
    private SidebarController sidebarController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        }

        cargarVista("Clientes.fxml");
    }

    /**
     * Método reutilizable para cambiar el contenido central.
     *
     * @param archivoFxml
     */
    public void cargarVista(String archivoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(archivoFxml));
            Node vista = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);

        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + archivoFxml);
        }
    }
}
