package com.tartanga.funerariatrujillo;

import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Rol;
import com.tartanga.funerariatrujillo.Security.UserSession;

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
    @FXML
    private Label lblNombreUsuario;
    @FXML
    private Label lblRolUsuario;
    @FXML
    private Label lblInicialesUsuario;

    private List<Button> navButtons;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;

        navButtons = Arrays.asList(
                btnClientes, btnDifuntos, btnTrabajadores, btnServicios,
                btnFeretros, btnUrnas, btnSalas, btnAsignaciones, btnFichaServicio
        );

        navegacion();
        Rol rol = UserSession.getCurrentUser() == null
                || UserSession.getCurrentUser().getRol() == null
                ? Rol.AGENTE_FUNERARIO : UserSession.getCurrentUser().getRol();
        aplicarPermisos(rol);
        btnCerrarSesion.setOnAction(event -> mainController.cerrarSesion());
        if (UserSession.getCurrentUser() != null) {
            String nombre = UserSession.getCurrentUser().getNombre() + " "
                    + UserSession.getCurrentUser().getApellidos();
            lblNombreUsuario.setText(nombre.trim());
            lblRolUsuario.setText(rol.toString());
            lblInicialesUsuario.setText(iniciales(nombre));
        }
        if (btnClientes.isManaged() && btnClientes.isVisible()) {
            marcarActivo(btnClientes);
        }
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

    private void aplicarPermisos(Rol rol) {
                setVisible(btnClientes, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO
                        || rol == Rol.CONTABILIDAD);
                setVisible(btnDifuntos, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO
                        || rol == Rol.TANATOPRAXIA);
                setVisible(btnTrabajadores, rol == Rol.ADMINISTRACION);
                setVisible(btnServicios, rol != null);
                setVisible(btnFeretros, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO);
                setVisible(btnUrnas, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO);
                setVisible(btnSalas, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO
                        || rol == Rol.TANATOPRAXIA);
                setVisible(btnAsignaciones, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO);
                setVisible(btnFichaServicio, rol != null);
    }

    private void setVisible(Button button, boolean visible) {
                button.setVisible(visible);
                button.setManaged(visible);
    }

    private String iniciales(String nombre) {
                String[] partes = nombre.trim().split("\\s+");
                if (partes.length == 0 || partes[0].isBlank()) {
                    return "?";
                }
                return partes.length == 1
                        ? partes[0].substring(0, 1).toUpperCase()
                        : (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }
}
