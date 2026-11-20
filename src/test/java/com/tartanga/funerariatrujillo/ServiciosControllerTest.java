package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.ServiciosDAO;
import com.tartanga.funerariatrujillo.Objects.Servicios;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.TextInputControlMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiciosControllerTest extends ApplicationTest {

    private static boolean simularDuplicado = false;

    public static class MockServiciosDAO extends ServiciosDAO {

        @Override
        public List<Servicios> obtenerServicios() {
            List<Servicios> lista = new ArrayList<>();
            Servicios s = new Servicios();
            s.setId("mock-id-1234");
            s.setNombre("Test");
            s.setDescripcion("Descripcion de prueba");
            s.setPrecio(99.99);
            lista.add(s);
            return lista;
        }

        @Override
        public boolean insertarServicio(Servicios s) {
            s.setId("mock-id-1234");
            return true;
        }

        @Override
        public boolean eliminarServicio(String id) {
            return true;
        }

        @Override
        public boolean actualizarServicio(Servicios s) {
            return true;
        }

        public boolean existeServicio(String nombre, String idExcluido) {
            return simularDuplicado;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/servicios.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == ServiciosController.class) {
                    return new ServiciosController(new MockServiciosDAO());
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Parent root = loader.load();
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }

    @Test
    @Order(1)
    public void testInitialState() {
        verifyThat("#vistaTabla", isVisible());
        verifyThat("#vistaDetalles", isInvisible());

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertFalse(panelFormulario.isVisible(), "El formulario de nuevo servicio debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testOpenForm() {
        clickOn("#btnNuevoServicio");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(3)
    public void testCloseForm() {
        clickOn("#btnNuevoServicio");
        sleep(450);

        clickOn("#btnCerrarFormulario");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertFalse(panelFormulario.isVisible());
        assertEquals(400.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(4)
    public void testBlankForm() {
        clickOn("#btnNuevoServicio");
        sleep(400);

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
    }

    @Test
    @Order(5)
    public void testNegativePrice() {
        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoServicio");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Prueba");
        clickOn("#txtNuevaDescripcion").write("Descripcion");
        clickOn("#txtNuevoPrecio").write("-5");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un servicio con precio negativo.");
    }

    @Test
    @Order(6)
    public void testInvalidPrice() {
        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoServicio");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Prueba");
        clickOn("#txtNuevaDescripcion").write("Descripcion");
        clickOn("#txtNuevoPrecio").write("abc");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un servicio con precio no numérico.");
    }

    @Test
    @Order(7)
    public void testSearchFilter() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);

        clickOn("#txtBuscar").write("Texto de prueba");
        verifyThat("#txtBuscar", hasText("Texto de prueba"));
        assertEquals(0, tabla.getItems().size());
        assertEquals("Servicio no encontrado", lblEstado.getText());

        clickOn("#txtBuscar").eraseText(15);
        assertEquals(1, tabla.getItems().size());
        assertEquals("1 servicios", lblEstado.getText());
    }

    @Test
    @Order(8)
    public void testCompleteFlow() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabla.getSelectionModel().select(0);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn(tabla);

        verifyThat("#vistaTabla", isInvisible());
        verifyThat("#vistaDetalles", isVisible());

        TextField txtNombre = lookup("#txtDetalleNombre").queryAs(TextField.class);
        assertFalse(txtNombre.isEditable(), "El nombre no debe ser editable al entrar a la ficha.");
        assertEquals("Test", txtNombre.getText());

        clickOn("#btnEditar");

        assertTrue(txtNombre.isEditable(), "El nombre debe ser editable tras pulsar Editar.");

        verifyThat("#btnEditar", isInvisible());
        verifyThat("#btnEliminar", isInvisible());
        verifyThat("#btnGuardarEdicion", isVisible());

        clickOn("#btnGuardarEdicion");
        assertFalse(txtNombre.isEditable(), "Los campos deben volver a bloquearse tras guardar.");
        verifyThat("#btnGuardarEdicion", isInvisible());
        verifyThat("#btnEditar", isVisible());

        clickOn("#btnVolver");
        verifyThat("#vistaDetalles", isInvisible());
        verifyThat("#vistaTabla", isVisible());
    }

    @Test
    @Order(9)
    public void testEditBlankName() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabla.getSelectionModel().select(0);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn(tabla);
        clickOn("#btnEditar");

        TextField txtNombre = lookup("#txtDetalleNombre").queryAs(TextField.class);
        clickOn(txtNombre).eraseText(10);

        clickOn("#btnGuardarEdicion");
        push(KeyCode.ENTER);

        assertTrue(txtNombre.isEditable(), "Con datos no válidos los campos deben seguir editables.");
        verifyThat("#btnGuardarEdicion", isVisible());
    }

    @Test
    @Order(10)
    public void testCreateService() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoServicio");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Nuevo");
        clickOn("#txtNuevaDescripcion").write("Servicio nuevo");
        clickOn("#txtNuevoPrecio").write("255.45");

        clickOn("#btnGuardarNuevo");
        sleep(450);

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener un servicio más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " servicios", lblEstado.getText());
    }

    @Test
    @Order(11)
    public void testDeleteService() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();
        int initialSize = tabla.getItems().size();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabla.getSelectionModel().select(0);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn(tabla);

        verifyThat("#vistaDetalles", isVisible());

        clickOn("#btnEliminar");

        verifyThat("#vistaTabla", isVisible());
        verifyThat("#vistaDetalles", isInvisible());
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener un servicio menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " servicios", lblEstado.getText());
    }

    @Test
    @Order(12)
    public void testCreateServiceDuplicate() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoServicio");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Nuevo");
        clickOn("#txtNuevaDescripcion").write("Servicio nuevo");
        clickOn("#txtNuevoPrecio").write("255.45");

        simularDuplicado = true;
        try {
            clickOn("#btnGuardarNuevo");
            push(KeyCode.ENTER);

            assertEquals(initialSize, tabla.getItems().size(), "Un registro duplicado no debe añadirse a la tabla.");
            verifyThat("#panelFormularioNuevo", isVisible());
        } finally {
            simularDuplicado = false;
        }

        clickOn("#btnCerrarFormulario");
        sleep(450);
    }

    @Test
    @Order(13)
    public void testEditDuplicate() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Servicios> tabla = lookup("#tablaServicios").queryTableView();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabla.getSelectionModel().select(0);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn(tabla);
        clickOn("#btnEditar");

        simularDuplicado = true;
        try {
            clickOn("#btnGuardarEdicion");
            push(KeyCode.ENTER);

            verifyThat("#btnGuardarEdicion", isVisible());
            verifyThat("#btnEditar", isInvisible());
        } finally {
            simularDuplicado = false;
        }
    }
}