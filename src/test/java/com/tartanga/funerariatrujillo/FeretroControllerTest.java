package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.FeretroDAO;
import com.tartanga.funerariatrujillo.Objects.Feretro;
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
public class FeretroControllerTest extends ApplicationTest {

    public static class MockFeretroDAO extends FeretroDAO {

        @Override
        public List<Feretro> obtenerFeretros() {
            List<Feretro> lista = new ArrayList<>();
            Feretro f = new Feretro();
            f.setId("mock-id-1234");
            f.setMedidas("190x60x50 cm");
            f.setMaterial("Roble");
            f.setPeso(80.5);
            f.setPrecio(950.0);
            lista.add(f);
            return lista;
        }

        @Override
        public boolean insertarFeretro(Feretro f) {
            f.setId("mock-id-5678");
            return true;
        }

        @Override
        public boolean eliminarFeretro(String id) {
            return true;
        }

        @Override
        public boolean actualizarFeretro(Feretro f) {
            return true;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/feretro.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == FeretroController.class) {
                    return new FeretroController(new MockFeretroDAO());
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
        assertFalse(panelFormulario.isVisible(), "El formulario de nuevo féretro debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testLoadedData() {
        sleep(300);
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);

        assertEquals(1, tabla.getItems().size());
        assertEquals("1 féretros", lblEstado.getText());
    }

    @Test
    @Order(3)
    public void testOpenForm() {
        clickOn("#btnNuevoFeretro");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(4)
    public void testCloseForm() {
        clickOn("#btnNuevoFeretro");
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
        clickOn("#btnNuevoFeretro");
        sleep(400);

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
    }

    @Test
    @Order(6)
    public void testInvalidNumbers() {
        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoFeretro");
        sleep(450);

        clickOn("#txtNuevasMedidas").write("190x60x50 cm");
        clickOn("#txtNuevoMaterial").write("Pino");
        clickOn("#txtNuevoPeso").write("abc");
        clickOn("#txtNuevoPrecio").write("500");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un féretro con peso no numérico.");
    }

    @Test
    @Order(7)
    public void testZeroWeight() {
        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoFeretro");
        sleep(450);

        clickOn("#txtNuevasMedidas").write("190x60x50 cm");
        clickOn("#txtNuevoMaterial").write("Pino");
        clickOn("#txtNuevoPeso").write("0");
        clickOn("#txtNuevoPrecio").write("500");

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un féretro con peso cero.");
    }

    @Test
    @Order(8)
    public void testSearchFilter() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);

        clickOn("#txtBuscar").write("Texto de prueba");
        verifyThat("#txtBuscar", hasText("Texto de prueba"));
        assertEquals(0, tabla.getItems().size());
        assertEquals("0 féretros", lblEstado.getText());

        clickOn("#txtBuscar").eraseText(15);
        assertEquals(1, tabla.getItems().size());
        assertEquals("1 féretros", lblEstado.getText());
    }

    @Test
    @Order(9)
    public void testCompleteFlow() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();

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

        TextField txtMaterial = lookup("#txtDetalleMaterial").queryAs(TextField.class);
        assertFalse(txtMaterial.isEditable(), "El material no debe ser editable al entrar a la ficha.");
        assertEquals("Roble", txtMaterial.getText());

        clickOn("#btnEditar");

        assertTrue(txtMaterial.isEditable(), "El material debe ser editable tras pulsar Editar.");

        verifyThat("#btnEditar", isInvisible());
        verifyThat("#btnEliminar", isInvisible());
        verifyThat("#btnGuardarEdicion", isVisible());

        clickOn("#btnGuardarEdicion");
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(txtMaterial.isEditable(), "Los campos deben volver a bloquearse tras guardar.");
        verifyThat("#btnGuardarEdicion", isInvisible());
        verifyThat("#btnEditar", isVisible());

        clickOn("#btnVolver");
        verifyThat("#vistaDetalles", isInvisible());
        verifyThat("#vistaTabla", isVisible());
    }

    @Test
    @Order(10)
    public void testEditBlankMaterial() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabla.getSelectionModel().select(0);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn(tabla);
        clickOn("#btnEditar");

        TextField txtMaterial = lookup("#txtDetalleMaterial").queryAs(TextField.class);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtMaterial.setText("");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#btnGuardarEdicion");
        push(KeyCode.ENTER);

        assertTrue(txtMaterial.isEditable(), "Con datos no válidos los campos deben seguir editables.");
        verifyThat("#btnGuardarEdicion", isVisible());
    }

    @Test
    @Order(11)
    public void testCreateCoffin() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoFeretro");
        sleep(450);

        clickOn("#txtNuevasMedidas").write("200x65x55 cm");
        clickOn("#txtNuevoMaterial").write("Pino");
        clickOn("#txtNuevoPeso").write("70,5");
        clickOn("#txtNuevoPrecio").write("620.50");

        clickOn("#btnGuardarNuevo");
        sleep(450);

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener un féretro más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " féretros", lblEstado.getText());
    }

    @Test
    @Order(12)
    public void testDeleteCoffin() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Feretro> tabla = lookup("#tablaFeretros").queryTableView();
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
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener un féretro menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " féretros", lblEstado.getText());
    }
}