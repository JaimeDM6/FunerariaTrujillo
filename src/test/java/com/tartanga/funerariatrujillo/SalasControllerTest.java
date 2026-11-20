package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.SalasDAO;
import com.tartanga.funerariatrujillo.Objects.Sala;
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
public class SalasControllerTest extends ApplicationTest {

    private static boolean simularDuplicado = false;

    public static class MockSalasDAO extends SalasDAO {

        @Override
        public List<Sala> obtenerSalas() {
            List<Sala> lista = new ArrayList<>();
            Sala s = new Sala();
            s.setId("mock-id-1234");
            s.setNombre("Sala Test");
            s.setDescripcion("Sala de prueba");
            s.setCapacidad(40);
            lista.add(s);
            return lista;
        }

        @Override
        public boolean insertarSala(Sala s) {
            s.setId("mock-id-5678");
            return true;
        }

        @Override
        public boolean eliminarSala(String id) {
            return true;
        }

        @Override
        public boolean actualizarSala(Sala s) {
            return true;
        }

        public boolean existeSala(String nombre, String idExcluido) {
            return simularDuplicado;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/sala.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == SalasController.class) {
                    return new SalasController(new MockSalasDAO());
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
        assertFalse(panelFormulario.isVisible(), "El formulario de nueva sala debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testLoadedData() {
        sleep(300);
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);

        assertEquals(1, tabla.getItems().size());
        assertEquals("1 salas", lblEstado.getText());
    }

    @Test
    @Order(3)
    public void testOpenForm() {
        clickOn("#btnNuevaSala");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(4)
    public void testCloseForm() {
        clickOn("#btnNuevaSala");
        sleep(450);

        clickOn("#btnCerrarFormulario");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertFalse(panelFormulario.isVisible());
        assertEquals(400.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(5)
    public void testBlankForm() {
        clickOn("#btnNuevaSala");
        sleep(400);

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
    }

    @Test
    @Order(6)
    public void testInvalidCapacity() {
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevaSala");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Sala Nueva");
        clickOn("#txtNuevaDescripcion").write("Descripcion");
        clickOn("#txtNuevaCapacidad").write("abc");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse una sala con capacidad no numérica.");
    }

    @Test
    @Order(7)
    public void testZeroCapacity() {
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevaSala");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Sala Nueva");
        clickOn("#txtNuevaDescripcion").write("Descripcion");
        clickOn("#txtNuevaCapacidad").write("0");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse una sala con capacidad cero.");
    }

    @Test
    @Order(8)
    public void testSearchFilter() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);

        clickOn("#txtBuscar").write("Texto de prueba");
        verifyThat("#txtBuscar", hasText("Texto de prueba"));
        assertEquals(0, tabla.getItems().size());
        assertEquals("0 salas", lblEstado.getText());

        clickOn("#txtBuscar").eraseText(15);
        assertEquals(1, tabla.getItems().size());
        assertEquals("1 salas", lblEstado.getText());
    }

    @Test
    @Order(9)
    public void testCompleteFlow() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();

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
        assertEquals("Sala Test", txtNombre.getText());

        clickOn("#btnEditar");

        assertTrue(txtNombre.isEditable(), "El nombre debe ser editable tras pulsar Editar.");

        verifyThat("#btnEditar", isInvisible());
        verifyThat("#btnEliminar", isInvisible());
        verifyThat("#btnGuardarEdicion", isVisible());

        clickOn("#btnGuardarEdicion");
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(txtNombre.isEditable(), "Los campos deben volver a bloquearse tras guardar.");
        verifyThat("#btnGuardarEdicion", isInvisible());
        verifyThat("#btnEditar", isVisible());

        clickOn("#btnVolver");
        verifyThat("#vistaDetalles", isInvisible());
        verifyThat("#vistaTabla", isVisible());
    }

    @Test
    @Order(10)
    public void testEditBlankName() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();

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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtNombre.setText("");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#btnGuardarEdicion");
        push(KeyCode.ENTER);

        assertTrue(txtNombre.isEditable(), "Con datos no válidos los campos deben seguir editables.");
        verifyThat("#btnGuardarEdicion", isVisible());
    }

    @Test
    @Order(11)
    public void testCreateRoom() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevaSala");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Sala Nueva");
        clickOn("#txtNuevaDescripcion").write("Sala recien creada");
        clickOn("#txtNuevaCapacidad").write("30");

        clickOn("#btnGuardarNuevo");
        sleep(450);

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener una sala más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " salas", lblEstado.getText());
    }

    @Test
    @Order(12)
    public void testDeleteRoom() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();
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
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener una sala menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " salas", lblEstado.getText());
    }

    @Test
    @Order(13)
    public void testCreateRoomDuplicate() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevaSala");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Sala Nueva");
        clickOn("#txtNuevaDescripcion").write("Sala recien creada");
        clickOn("#txtNuevaCapacidad").write("30");

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
    @Order(14)
    public void testEditDuplicate() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Sala> tabla = lookup("#tablaSalas").queryTableView();

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
