package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.ClienteDAO;
import com.tartanga.funerariatrujillo.Objects.Cliente;
import com.tartanga.funerariatrujillo.Objects.Direccion;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.TextInputControlMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientesControllerTest extends ApplicationTest {

    public static class MockClienteDAO extends ClienteDAO {

        @Override
        public List<Cliente> obtenerClientes() {
            List<Cliente> lista = new ArrayList<>();
            Cliente c = new Cliente();
            c.setId("mock-id-1234");
            c.setNombre("Test");
            c.setApellidos("Usuario");
            c.setDni("12345678A");
            c.setTelefono("666555444");
            c.setDireccion(new Direccion("Test", "Test", 10001));
            c.setCorreo_electronico("test@test.com");
            lista.add(c);
            return lista;
        }

        @Override
        public boolean insertarCliente(Cliente c) {
            c.setId("mock-id-1234");
            return true;
        }

        @Override
        public boolean eliminarCliente(String id) {
            return true;
        }

        @Override
        public boolean actualizarCliente(Cliente c) {
            return true;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/clientes.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == ClientesController.class) {
                    return new ClientesController(new MockClienteDAO());
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
        assertFalse(panelFormulario.isVisible(), "El formulario de nuevo cliente debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testOpenForm() {
        clickOn("#btnNuevoCliente");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(3)
    public void testCloseForm() {
        clickOn("#btnNuevoCliente");
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
        clickOn("#btnNuevoCliente");
        sleep(400);

        clickOn("#btnGuardarNuevo");

        verifyThat("#panelFormularioNuevo", isVisible());
    }

    @Test
    @Order(5)
    public void testSearchFilter() {
        clickOn("#txtBuscar").write("Texto de prueba");
        verifyThat("#txtBuscar", hasText("Texto de prueba"));
        clickOn("#txtBuscar").eraseText(15);
    }

    @Test
    @Order(6)
    public void testCompleteFlow() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Cliente> tabla = lookup("#tablaClientes").queryTableView();

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
    @Order(7)
    public void testCreateClient() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Cliente> tabla = lookup("#tablaClientes").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoCliente");
        sleep(450);

        clickOn("#txtNuevoNombre").write("Nuevo");
        clickOn("#txtNuevoApellidos").write("Cliente");
        clickOn("#txtNuevoDni").write("87654321B");
        clickOn("#txtNuevoTelefono").write("600111222");
        clickOn("#txtNuevoCorreo").write("test@test.com");
        clickOn("#txtNuevoCalle").write("Calle");
        clickOn("#txtNuevoCiudad").write("Ciudad");
        clickOn("#txtNuevoCP").write("10001");

        clickOn("#btnGuardarNuevo");
        sleep(450);

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener un cliente más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " clientes", lblEstado.getText());
    }

    @Test
    @Order(8)
    public void testDeleteClient() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Cliente> tabla = lookup("#tablaClientes").queryTableView();
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
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener un cliente menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " clientes", lblEstado.getText());
    }
}
