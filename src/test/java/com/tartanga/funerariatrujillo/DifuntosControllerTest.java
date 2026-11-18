package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.DifuntoDAO;
import com.tartanga.funerariatrujillo.Objects.Difunto;
import com.tartanga.funerariatrujillo.Objects.Feretro;
import com.tartanga.funerariatrujillo.Objects.Urna;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.TextInputControlMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifuntosControllerTest extends ApplicationTest {

    public static class MockDifuntoDAO extends DifuntoDAO {

        @Override
        public List<Difunto> obtenerDifuntos() {
            List<Difunto> lista = new ArrayList<>();
            Difunto d = new Difunto();
            d.setId("mock-id-1234");
            d.setId_cliente("cli-1");
            d.setId_feretro("fer-1");
            d.setId_urna("urn-1");
            d.setNombre("Test");
            d.setApellidos("Difunto");
            d.setFecha_nacimiento(new Date(0L));
            d.setFecha_muerte(new Date());
            d.setCausa_muerte("Natural");
            d.setServicio(Difunto.Servicio.ENTIERRO);

            Feretro feretro = new Feretro();
            feretro.setMaterial("Roble");
            d.setFeretro(feretro);

            Urna urna = new Urna();
            urna.setMaterial("Marmol");
            d.setUrna(urna);

            lista.add(d);
            return lista;
        }

        @Override
        public List<String> obtenerMaterialesFeretros() {
            List<String> lista = new ArrayList<>();
            lista.add("Roble");
            lista.add("Pino");
            return lista;
        }

        @Override
        public List<String> obtenerMaterialesUrnas() {
            List<String> lista = new ArrayList<>();
            lista.add("Marmol");
            lista.add("Ceramica");
            return lista;
        }

        @Override
        public List<String> obtenerClientes() {
            List<String> lista = new ArrayList<>();
            lista.add("cli-1 - Ana Lopez");
            lista.add("cli-2 - Pedro Gomez");
            return lista;
        }

        @Override
        public String obtenerIdPorMaterial(String coleccion, String material) {
            return "mock-material-id";
        }

        @Override
        public boolean insertarDifunto(Difunto d) {
            d.setId("mock-id-1234");
            return true;
        }

        @Override
        public boolean eliminarDifunto(String id) {
            return true;
        }

        @Override
        public boolean actualizarDifunto(Difunto d) {
            return true;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/difuntos.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == DifuntosController.class) {
                    return new DifuntosController(new MockDifuntoDAO());
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

    private void rellenarFormularioNuevo(LocalDate nacimiento, LocalDate muerte) {
        TextField nombre = lookup("#txtNuevoNombre").queryAs(TextField.class);
        TextField apellidos = lookup("#txtNuevoApellidos").queryAs(TextField.class);
        TextField cliente = lookup("#txtNuevoCliente").queryAs(TextField.class);
        TextField causa = lookup("#txtNuevaCausa").queryAs(TextField.class);
        DatePicker dpNacimiento = lookup("#dpNuevoNacimiento").queryAs(DatePicker.class);
        DatePicker dpMuerte = lookup("#dpNuevoMuerte").queryAs(DatePicker.class);
        ComboBox<String> feretro = lookup("#cmbNuevoMaterialFeretro").queryComboBox();
        ComboBox<String> urna = lookup("#cmbNuevoMaterialUrna").queryComboBox();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nombre.setText("Nuevo");
                apellidos.setText("Difunto");
                cliente.setText("Ana Lopez");
                causa.setText("Natural");
                dpNacimiento.setValue(nacimiento);
                dpMuerte.setValue(muerte);
                feretro.setValue("Roble");
                urna.setValue("Marmol");
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
        assertFalse(panelFormulario.isVisible(), "El formulario de nuevo difunto debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testOpenForm() {
        clickOn("#btnNuevoDifunto");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(3)
    public void testCloseForm() {
        clickOn("#btnNuevoDifunto");
        sleep(450);

        clickOn("#btnCerrarFormulario");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertFalse(panelFormulario.isVisible());
        assertEquals(400.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(4)
    public void testDefaultDates() {
        clickOn("#btnNuevoDifunto");
        sleep(450);

        DatePicker dpNacimiento = lookup("#dpNuevoNacimiento").queryAs(DatePicker.class);
        DatePicker dpMuerte = lookup("#dpNuevoMuerte").queryAs(DatePicker.class);

        assertEquals(LocalDate.now(), dpNacimiento.getValue());
        assertEquals(LocalDate.now(), dpMuerte.getValue());
    }

    @Test
    @Order(5)
    public void testBlankForm() {
        clickOn("#btnNuevoDifunto");
        sleep(400);

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
    }

    @Test
    @Order(6)
    public void testSearchFilter() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Difunto> tabla = lookup("#tablaDifuntos").queryTableView();

        clickOn("#txtBuscar").write("Texto de prueba");
        verifyThat("#txtBuscar", hasText("Texto de prueba"));
        assertEquals(0, tabla.getItems().size());

        clickOn("#txtBuscar").eraseText(15);
        assertEquals(1, tabla.getItems().size());
    }

    @Test
    @Order(7)
    public void testClientSearchAndSpace() {
        clickOn("#btnNuevoDifunto");
        sleep(450);

        TextField cliente = lookup("#txtNuevoCliente").queryAs(TextField.class);
        ListView<String> lista = lookup("#listaClientes").queryListView();

        clickOn(cliente).write("Ana Lo");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Ana Lo", cliente.getText(), "El espacio debe escribirse sin seleccionar ningún cliente.");
        assertTrue(lista.isVisible());
        assertTrue(lista.getItems().contains("Ana Lopez"));
        assertFalse(lista.getItems().contains("Pedro Gomez"));
    }

    @Test
    @Order(8)
    public void testClientSelectionFromList() {
        clickOn("#btnNuevoDifunto");
        sleep(450);

        TextField cliente = lookup("#txtNuevoCliente").queryAs(TextField.class);
        ListView<String> lista = lookup("#listaClientes").queryListView();

        clickOn(cliente).write("Ana");
        WaitForAsyncUtils.waitForFxEvents();

        Node celda = from(lista).lookup(".list-cell").nth(0).query();
        clickOn(celda);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Ana Lopez", cliente.getText());
        assertFalse(lista.isVisible(), "La lista debe ocultarse al elegir un cliente.");
    }

    @Test
    @Order(9)
    public void testFutureDeathDate() {
        TableView<Difunto> tabla = lookup("#tablaDifuntos").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoDifunto");
        sleep(450);

        rellenarFormularioNuevo(LocalDate.of(1950, 1, 1), LocalDate.now().plusDays(5));

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un difunto con fecha de muerte futura.");
    }

    @Test
    @Order(10)
    public void testDeathBeforeBirth() {
        TableView<Difunto> tabla = lookup("#tablaDifuntos").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoDifunto");
        sleep(450);

        rellenarFormularioNuevo(LocalDate.of(2000, 1, 1), LocalDate.of(1990, 1, 1));

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse un difunto que muere antes de nacer.");
    }

    @Test
    @Order(11)
    public void testCompleteFlow() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Difunto> tabla = lookup("#tablaDifuntos").queryTableView();

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
    @Order(12)
    public void testEditBlankName() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Difunto> tabla = lookup("#tablaDifuntos").queryTableView();

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
    @Order(13)
    public void testCreateDeceased() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Difunto> tabla = lookup("#tablaDifuntos").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevoDifunto");
        sleep(450);

        rellenarFormularioNuevo(LocalDate.of(1950, 1, 1), LocalDate.now());

        clickOn("#btnGuardarNuevo");
        sleep(450);

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener un difunto más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " difuntos", lblEstado.getText());
    }

    @Test
    @Order(14)
    public void testDeleteDeceased() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Difunto> tabla = lookup("#tablaDifuntos").queryTableView();
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
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener un difunto menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " difuntos", lblEstado.getText());
    }
}