package com.tartanga.funerariatrujillo;

import java.util.Arrays;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Rol;
import com.tartanga.funerariatrujillo.Security.UserSession;

/**
 * Controlador de la barra lateral de navegación. Muestra los módulos disponibles según el rol del
 * trabajador que ha iniciado sesión, marca el módulo activo y muestra los datos del usuario.
 *
 * @author Aritz
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

    /**
     * Establece el controlador principal y prepara la barra lateral: asigna la navegación de los
     * botones, aplica los permisos según el rol del usuario, muestra su nombre, su rol y sus
     * iniciales, y marca clientes como módulo activo si está disponible.
     *
     * @param mainController controlador principal de la aplicación
     */
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

        btnCerrarSesion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainController.cerrarSesion();
            }
        });

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

    /**
     * Asigna a cada botón de la barra la acción de cargar su vista en el controlador principal y
     * marcarse como activo.
     */
    private void navegacion() {
        btnClientes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("clientes.fxml");
                marcarActivo(btnClientes);
            }
        });

        btnDifuntos.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("difuntos.fxml");
                marcarActivo(btnDifuntos);
            }
        });

        btnTrabajadores.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("trabajadores.fxml");
                marcarActivo(btnTrabajadores);
            }
        });

        btnServicios.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("servicios.fxml");
                marcarActivo(btnServicios);
            }
        });

        btnFeretros.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("feretro.fxml");
                marcarActivo(btnFeretros);
            }
        });

        btnUrnas.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("urnas.fxml");
                marcarActivo(btnUrnas);
            }
        });

        btnSalas.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("sala.fxml");
                marcarActivo(btnSalas);
            }
        });

        btnAsignaciones.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("asignaciones.fxml");
                marcarActivo(btnAsignaciones);
            }
        });

        btnFichaServicio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.cargarVista("ficha_servicio.fxml");
                marcarActivo(btnFichaServicio);
            }
        });
    }

    /**
     * Marca como activo el botón de la ficha de servicio, mostrándolo en la barra.
     */
    public void activarBotonFichaServicio() {
        marcarActivo(btnFichaServicio);
    }

    /**
     * Marca un botón como activo y el resto como inactivos. El botón de la ficha de servicio solo es
     * visible mientras está activo.
     *
     * @param botonActivo botón que se marca como activo
     */
    private void marcarActivo(Button botonActivo) {
        if (botonActivo == btnFichaServicio) {
            setVisible(btnFichaServicio, true);
        } else {
            setVisible(btnFichaServicio, false);
        }

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

    /**
     * Muestra u oculta los botones de la barra según el rol del trabajador.
     *
     * @param rol rol del trabajador que ha iniciado sesión
     */
    private void aplicarPermisos(Rol rol) {
        setVisible(btnClientes, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO || rol == Rol.CONTABILIDAD);
        setVisible(btnDifuntos, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO || rol == Rol.TANATOPRAXIA);
        setVisible(btnTrabajadores, rol == Rol.ADMINISTRACION);
        setVisible(btnServicios, rol != null);
        setVisible(btnFeretros, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO);
        setVisible(btnUrnas, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO);
        setVisible(btnSalas, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO || rol == Rol.TANATOPRAXIA);
        setVisible(btnAsignaciones, rol == Rol.ADMINISTRACION || rol == Rol.AGENTE_FUNERARIO || rol == Rol.TANATOPRAXIA);
        setVisible(btnFichaServicio, false);
    }

    /**
     * Muestra u oculta un botón, de modo que ocupe o no espacio en la barra.
     *
     * @param button botón que se modifica
     * @param visible {@code true} para mostrarlo; {@code false} para ocultarlo
     */
    private void setVisible(Button button, boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }

    /**
     * Obtiene las iniciales de un nombre: la del primer y la del último término, o solo la primera si
     * hay un único término.
     *
     * @param nombre nombre completo
     * @return iniciales en mayúsculas, o {@code ?} si el nombre está vacío
     */
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
