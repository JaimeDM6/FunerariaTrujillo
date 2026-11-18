package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.UrnaDAO;
import com.tartanga.funerariatrujillo.Objects.Urna;
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
public class UrnasControllerTest extends ApplicationTest {

    public static class MockUrnaDAO extends UrnaDAO {

        @Override
        public List<Urna> obtenerUrnas() {
            List<Urna> lista = new ArrayList<>();
            Urna u = new Urna();
            u.setId("mock-id-1234");
            u.setCapacidad(1.0);
            u.setMaterial("Test");
            u.setPrecio(99.99);
            lista.add(u);
            return lista;
        }

        @Override
        public boolean insertarUrna(Urna u) {
            u.setId("mock-id-1234");
            return true;
        }

        @Override
        public boolean eliminarUrna(String id) {
            return true;
        }

        @Override
        public boolean actualizarUrna(Urna u) {
            return true;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/urnas.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == UrnasController.class) {
                    return new UrnasController(new MockUrnaDAO());
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
        assertFalse(panelFormulario.isVisible(), "El formulario de nueva urna debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testOpenForm() {
        clickOn("#btnNuevaUrna");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(3)
    public void testCloseForm() {
        clickOn("#btnNuevaUrna");
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
        clickOn("#btnNuevaUrna");
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

        TableView<Urna> tabla = lookup("#tablaUrnas").queryTableView();

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

        TextField txtCapacidad = lookup("#txtDetalleCapacidad").queryAs(TextField.class);
        assertFalse(txtCapacidad.isEditable(), "La capacidad no debe ser editable al entrar a la ficha.");

        clickOn("#btnEditar");

        assertTrue(txtCapacidad.isEditable(), "La capacidad debe ser editable tras pulsar Editar.");

        verifyThat("#btnEditar", isInvisible());
        verifyThat("#btnEliminar", isInvisible());
        verifyThat("#btnGuardarEdicion", isVisible());

        clickOn("#btnGuardarEdicion");
        assertFalse(txtCapacidad.isEditable(), "Los campos deben volver a bloquearse tras guardar.");
        verifyThat("#btnGuardarEdicion", isInvisible());
        verifyThat("#btnEditar", isVisible());

        clickOn("#btnVolver");
        verifyThat("#vistaDetalles", isInvisible());
        verifyThat("#vistaTabla", isVisible());
    }

    @Test
    @Order(7)
    public void testCreateUrn() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Urna> tabla = lookup("#tablaUrnas").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevaUrna");
        sleep(450);

        clickOn("#txtNuevaCapacidad").write("1.5");
        clickOn("#txtNuevoMaterial").write("Prueba");
        clickOn("#txtNuevoPrecio").write("255.45");

        clickOn("#btnGuardarNuevo");
        sleep(450);

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener una urna más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " urnas", lblEstado.getText());
    }

    @Test
    @Order(8)
    public void testDeleteUrn() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Urna> tabla = lookup("#tablaUrnas").queryTableView();
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
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener una urna menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " urnas", lblEstado.getText());
    }
}
