package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.TrabajadorDAO;
import com.tartanga.funerariatrujillo.Objects.Direccion;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Cargo;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Rol;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
public class TrabajadorControllerTest extends ApplicationTest {

    public static class MockTrabajadorDAO extends TrabajadorDAO {

        @Override
        public List<Trabajador> obtenerTrabajadores() {
            List<Trabajador> lista = new ArrayList<>();
            Trabajador t = new Trabajador();
            t.setId("mock-id-1234");
            t.setNombre("Test");
            t.setApellidos("Usuario");
            t.setDni("12345678A");
            t.setCorreo_electronico("test@test.com");
            t.setUsuario("t.usuario");
            t.setPassword("hash");
            t.setDireccion(new Direccion("Test", "Test", 10001));
            t.setNumero_ss("123456789012");
            t.setIban("ES0000000000000000000000");
            t.setCargo(Cargo.DIRECTOR);
            t.setRol(Rol.ADMINISTRACION);
            t.setSalario(1500.0);
            t.setTipo_jornada("Completa");
            lista.add(t);
            return lista;
        }

        @Override
        public boolean insertarTrabajador(Trabajador t) {
            t.setId("mock-id-1234");
            return true;
        }

        @Override
        public boolean eliminarTrabajador(String id) {
            return true;
        }

        @Override
        public boolean actualizarTrabajador(Trabajador t) {
            return true;
        }

        @Override
        public boolean existeUsuario(String usuario) {
            return false;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/trabajadores.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == TrabajadorController.class) {
                    return new TrabajadorController(new MockTrabajadorDAO());
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

    private void rellenarFormularioNuevo(String password, String confirmacion) {
        TextField nombre = lookup("#txtNuevoNombre").queryAs(TextField.class);
        TextField apellidos = lookup("#txtNuevoApellidos").queryAs(TextField.class);
        TextField dni = lookup("#txtNuevoDni").queryAs(TextField.class);
        TextField correo = lookup("#txtNuevoCorreo").queryAs(TextField.class);
        TextField calle = lookup("#txtNuevoCalle").queryAs(TextField.class);
        TextField ciudad = lookup("#txtNuevaCiudad").queryAs(TextField.class);
        TextField cp = lookup("#txtNuevoCP").queryAs(TextField.class);
        TextField ss = lookup("#txtNuevoSS").queryAs(TextField.class);
        TextField iban = lookup("#txtNuevoIban").queryAs(TextField.class);
        TextField salario = lookup("#txtNuevoSalario").queryAs(TextField.class);
        PasswordField pass = lookup("#txtNuevaPassword").queryAs(PasswordField.class);
        PasswordField confirm = lookup("#txtNuevaConfirmarPassword").queryAs(PasswordField.class);
        ComboBox<Cargo> cargo = lookup("#cmbNuevoCargo").queryComboBox();
        ComboBox<String> jornada = lookup("#cmbNuevaJornada").queryComboBox();
        ComboBox<Rol> rol = lookup("#cmbNuevoRol").queryComboBox();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nombre.setText("Nuevo");
                apellidos.setText("Trabajador");
                dni.setText("87654321B");
                correo.setText("nuevo@test.com");
                calle.setText("Calle");
                ciudad.setText("Ciudad");
                cp.setText("10001");
                ss.setText("123456789012");
                iban.setText("ES0000000000000000000001");
                salario.setText("1800");
                pass.setText(password);
                confirm.setText(confirmacion);
                cargo.getSelectionModel().select(Cargo.DIRECTOR);
                jornada.getSelectionModel().select("Completa");
                rol.getSelectionModel().select(Rol.ADMINISTRACION);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @Order(1)
    public void testInitialState() {
        verifyThat("#vistaTabla", isVisible());
        verifyThat("#vistaDetalles", isInvisible());

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertFalse(panelFormulario.isVisible(), "El formulario de nuevo trabajador debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testOpenForm() {
        clickOn("#btnNuevoTrabajador");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(3)
    public void testCloseForm() {
        clickOn("#btnNuevoTrabajador");
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
        clickOn("#btnNuevoTrabajador");
        sleep(400);

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
    }

    @Test
    @Order(5)
    public void testSearchFilter() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Trabajador> tabla = lookup("#tablaTrabajadores").queryTableView();

        clickOn("#txtBuscar").write("Texto de prueba");
        verifyThat("#txtBuscar", hasText("Texto de prueba"));
        assertEquals(0, tabla.getItems().size());

        clickOn("#txtBuscar").eraseText(15);
        assertEquals(1, tabla.getItems().size());
    }

    @Test
    @Order(6)
    public void testRolesByCargo() {
        clickOn("#btnNuevoTrabajador");
        sleep(450);

        ComboBox<Cargo> cargo = lookup("#cmbNuevoCargo").queryComboBox();
        ComboBox<Rol> rol = lookup("#cmbNuevoRol").queryComboBox();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cargo.getSelectionModel().select(Cargo.CONDUCTOR);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1, rol.getItems().size());
        assertEquals(Rol.TRANSPORTE, rol.getItems().get(0));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cargo.getSelectionModel().select(Cargo.RECEPCIONISTA);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, rol.getItems().size());
        assertTrue(rol.getItems().contains(Rol.CONTABILIDAD));
        assertTrue(rol.getItems().contains(Rol.ADMINISTRACION));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cargo.getSelectionModel().select(Cargo.DIRECTOR);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(Rol.values().length, rol.getItems().size());
    }

    @Test
    @Order(7)
    public void testPasswordVisibilityToggle() {
        clickOn("#btnNuevoTrabajador");
        sleep(450);

        PasswordField pass = lookup("#txtNuevaPassword").queryAs(PasswordField.class);
        TextField visible = lookup("#txtNuevaPasswordVisible").queryAs(TextField.class);
        Button boton = lookup("#btnMostrarNuevaPassword").queryButton();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pass.setText("secreto123");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(pass.isVisible());
        assertFalse(visible.isVisible());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                boton.fire();
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(pass.isVisible());
        assertTrue(visible.isVisible());
        assertEquals("secreto123", visible.getText());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                boton.fire();
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(pass.isVisible());
        assertFalse(visible.isVisible());
    }

    @Test
    @Order(8)
    public void testPasswordMismatch() {
        TableView<Trabajador> tabla = lookup("#tablaTrabajadores").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoTrabajador");
        sleep(450);

        rellenarFormularioNuevo("password123", "otraPassword");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un trabajador con contraseñas distintas.");
    }

    @Test
    @Order(9)
    public void testShortPassword() {
        TableView<Trabajador> tabla = lookup("#tablaTrabajadores").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoTrabajador");
        sleep(450);

        rellenarFormularioNuevo("corta", "corta");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un trabajador con contraseña corta.");
    }

    @Test
    @Order(10)
    public void testCompleteFlow() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Trabajador> tabla = lookup("#tablaTrabajadores").queryTableView();

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
    @Order(11)
    public void testEditInvalidSalary() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Trabajador> tabla = lookup("#tablaTrabajadores").queryTableView();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabla.getSelectionModel().select(0);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn(tabla);
        clickOn("#btnEditar");

        TextField salario = lookup("#txtDetalleSalario").queryAs(TextField.class);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                salario.setText("abc");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#btnGuardarEdicion");
        push(KeyCode.ENTER);

        TextField txtNombre = lookup("#txtDetalleNombre").queryAs(TextField.class);
        assertTrue(txtNombre.isEditable(), "Con datos no válidos los campos deben seguir editables.");
        verifyThat("#btnGuardarEdicion", isVisible());
    }

    @Test
    @Order(12)
    public void testCreateWorker() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Trabajador> tabla = lookup("#tablaTrabajadores").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoTrabajador");
        sleep(450);

        rellenarFormularioNuevo("password123", "password123");

        clickOn("#btnGuardarNuevo");
        sleep(1500);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener un trabajador más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " trabajadores", lblEstado.getText());
    }

    @Test
    @Order(13)
    public void testDeleteWorker() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Trabajador> tabla = lookup("#tablaTrabajadores").queryTableView();
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
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener un trabajador menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " trabajadores", lblEstado.getText());
    }
}