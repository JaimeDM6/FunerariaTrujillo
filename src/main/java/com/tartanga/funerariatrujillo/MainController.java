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
import com.tartanga.funerariatrujillo.Security.UserSession;

/**
 * Controlador del contenedor principal (layout). Aloja la barra lateral y la zona central donde se
 * cargan las vistas de cada módulo, y gestiona el cierre de sesión.
 *
 * @author Aritz
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane rootLayout;
    @FXML
    private StackPane contentArea;

    @FXML
    private SidebarController sidebarController;

    /**
     * Inicializa el contenedor principal: conecta la barra lateral con este controlador y carga la
     * vista de clientes.
     *
     * @param location ubicación del documento FXML; no se utiliza
     * @param resources recursos de internacionalización; no se utilizan
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        }

        cargarVista("clientes.fxml");
    }

    /**
     * Carga una vista en la zona central. Si la vista es la de difuntos, le facilita este controlador
     * para que pueda navegar a otras vistas.
     *
     * @param archivoFxml nombre del archivo FXML de la vista
     */
    public void cargarVista(String archivoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(archivoFxml));
            Node vista = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof DifuntosController) {
                DifuntosController difuntosController = (DifuntosController) controller;
                difuntosController.setMainController(this);
            }
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);

        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + archivoFxml);
        }
    }

    /**
     * Cierra la sesión del usuario y vuelve a la pantalla de inicio de sesión.
     */
    public void cerrarSesion() {
        UserSession.logout();
        try {
            App.setRoot("login");
        } catch (IOException e) {
            System.err.println("No se pudo cerrar la sesión: " + e.getMessage());
        }
    }
}
