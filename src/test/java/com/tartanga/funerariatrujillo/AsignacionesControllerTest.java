package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO;
import com.tartanga.funerariatrujillo.Objects.Asignacion;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.TextInputControlMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AsignacionesControllerTest extends ApplicationTest {

    public static class MockAsignacionesDAO extends AsignacionesDAO {

        @Override
        public boolean puedeGestionar() {
            return true;
        }

        @Override
        public List<Asignacion> obtenerAsignaciones() {
            List<Asignacion> lista = new ArrayList<>();
            Asignacion a = new Asignacion();
            a.setId("6afe0ad6fa01503da7765c10");
            a.setIdDifunto("6afe0ad6fa01503da7765c01");
            a.setIdTrabajador("6afe0ad6fa01503da7765c02");
            a.setDifuntoNombre("Juan Perez");
            a.setTrabajadorNombre("Maria Gomez");
            a.setEstado("En curso");
            a.setFechaAsignacion(new Date());
            a.setFechaInicioTrabajo(new Date());
            a.setIndicacionesFamilia("Presentacion sencilla");
            a.setPasoIdentificacion(true);
            a.setObservaciones("Sin novedades");
            lista.add(a);
            return lista;
        }

        @Override
        public List<String> obtenerDifuntos() {
            List<String> lista = new ArrayList<>();
            lista.add("6afe0ad6fa01503da7765c01 - Juan Perez");
            lista.add("6afe0ad6fa01503da7765c03 - Pedro Ruiz");
            return lista;
        }

        @Override
        public List<String> obtenerTrabajadores() {
            List<String> lista = new ArrayList<>();
            lista.add("6afe0ad6fa01503da7765c02 - Maria Gomez");
            lista.add("6afe0ad6fa01503da7765c04 - Laura Diaz");
            return lista;
        }

        @Override
        public boolean insertarAsignacion(Asignacion a) {
            a.setId("6afe0ad6fa01503da7765c20");
            a.setEstado("Pendiente");
            a.setFechaAsignacion(new Date());
            return true;
        }

        @Override
        public boolean actualizarAsignacion(Asignacion a) {
            a.setUltimaActualizacion(new Date());
            return true;
        }

        @Override
        public boolean eliminarAsignacion(String id) {
            return true;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/asignaciones.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == AsignacionesController.class) {
                    return new AsignacionesController(new MockAsignacionesDAO());
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

    private void abrirFichaDeLaPrimeraFila() {
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabla.getSelectionModel().select(0);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn(tabla);
    }

    @Test
    @Order(1)
    public void testInitialState() {
        verifyThat("#vistaTabla", isVisible());
        verifyThat("#vistaDetalles", isInvisible());
        verifyThat("#btnNuevaAsignacion", isVisible());

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertFalse(panelFormulario.isVisible(), "El formulario de nueva asignación debe estar oculto al inicio.");
    }

    @Test
    @Order(2)
    public void testLoadedData() {
        sleep(300);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);

        assertEquals(1, tabla.getItems().size());
        assertEquals("1 asignaciones", lblEstado.getText());
    }

    @Test
    @Order(3)
    public void testOpenForm() {
        clickOn("#btnNuevaAsignacion");
        sleep(450);

        VBox panelFormulario = lookup("#panelFormularioNuevo").queryAs(VBox.class);
        assertTrue(panelFormulario.isVisible());
        assertEquals(0.0, panelFormulario.getTranslateX(), 0.01);
    }

    @Test
    @Order(4)
    public void testCloseForm() {
        clickOn("#btnNuevaAsignacion");
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
        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnNuevaAsignacion");
        sleep(400);

        clickOn("#btnGuardarNuevo");
        push(KeyCode.ENTER);

        verifyThat("#panelFormularioNuevo", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "No debe crearse una asignación sin difunto ni trabajador.");
    }

    @Test
    @Order(6)
    public void testSearchFilter() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);

        clickOn("#txtBuscar").write("Texto de prueba");
        verifyThat("#txtBuscar", hasText("Texto de prueba"));
        assertEquals(0, tabla.getItems().size());
        assertEquals("Asignación no encontrada", lblEstado.getText());

        clickOn("#txtBuscar").eraseText(15);
        assertEquals(1, tabla.getItems().size());
        assertEquals("1 asignaciones", lblEstado.getText());

        clickOn("#txtBuscar").write("curso");
        assertEquals(1, tabla.getItems().size(), "La búsqueda debe encontrar por estado.");

        clickOn("#txtBuscar").eraseText(5);
        clickOn("#txtBuscar").write("765C10");
        assertEquals(1, tabla.getItems().size(), "La búsqueda debe encontrar por código abreviado.");
    }

    @Test
    @Order(7)
    public void testCompleteFlow() {
        abrirFichaDeLaPrimeraFila();

        verifyThat("#vistaTabla", isInvisible());
        verifyThat("#vistaDetalles", isVisible());

        ComboBox<String> cmbEstado = lookup("#cmbDetalleEstado").queryComboBox();
        assertTrue(cmbEstado.isDisabled(), "El estado no debe ser editable al entrar a la ficha.");
        assertEquals("En curso", cmbEstado.getValue());

        clickOn("#btnEditar");

        assertFalse(cmbEstado.isDisabled(), "El estado debe ser editable tras pulsar Editar.");

        verifyThat("#btnEditar", isInvisible());
        verifyThat("#btnEliminar", isInvisible());
        verifyThat("#btnGuardarEdicion", isVisible());

        clickOn("#btnGuardarEdicion");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(cmbEstado.isDisabled(), "Los campos deben volver a bloquearse tras guardar.");
        verifyThat("#btnGuardarEdicion", isInvisible());
        verifyThat("#btnEditar", isVisible());

        clickOn("#btnVolver");
        verifyThat("#vistaDetalles", isInvisible());
        verifyThat("#vistaTabla", isVisible());
    }

    @Test
    @Order(8)
    public void testEditStatus() {
        abrirFichaDeLaPrimeraFila();

        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();
        ComboBox<String> cmbEstado = lookup("#cmbDetalleEstado").queryComboBox();
        TextArea txtObservaciones = lookup("#txtDetalleObservaciones").queryAs(TextArea.class);

        clickOn("#btnEditar");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cmbEstado.setValue("Completado");
                txtObservaciones.setText("Servicio finalizado");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#btnGuardarEdicion");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Completado", tabla.getItems().get(0).getEstado());
        assertEquals("Servicio finalizado", tabla.getItems().get(0).getObservaciones());
    }

    @Test
    @Order(9)
    public void testCreateAssignment() {
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();
        int initialSize = tabla.getItems().size();

        ComboBox<String> cmbDifunto = lookup("#cmbNuevoDifunto").queryComboBox();
        ComboBox<String> cmbTrabajador = lookup("#cmbNuevoTrabajador").queryComboBox();
        TextArea txtIndicaciones = lookup("#txtNuevasIndicaciones").queryAs(TextArea.class);

        clickOn("#btnNuevaAsignacion");
        sleep(450);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cmbDifunto.setValue(cmbDifunto.getItems().get(1));
                cmbTrabajador.setValue(cmbTrabajador.getItems().get(1));
                txtIndicaciones.setText("Peinado especial");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#btnGuardarNuevo");
        sleep(450);

        assertEquals(initialSize + 1, tabla.getItems().size(), "La tabla debe tener una asignación más.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize + 1) + " asignaciones", lblEstado.getText());
    }

    @Test
    @Order(10)
    public void testCancelDelete() {
        abrirFichaDeLaPrimeraFila();

        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();
        int initialSize = tabla.getItems().size();

        clickOn("#btnEliminar");
        push(KeyCode.ESCAPE);

        verifyThat("#vistaDetalles", isVisible());
        assertEquals(initialSize, tabla.getItems().size(), "Cancelar la confirmación no debe eliminar la asignación.");
    }

    @Test
    @Order(11)
    public void testDeleteAssignment() {
        abrirFichaDeLaPrimeraFila();

        TableView<Asignacion> tabla = lookup("#tablaAsignaciones").queryTableView();
        int initialSize = tabla.getItems().size();

        verifyThat("#vistaDetalles", isVisible());

        clickOn("#btnEliminar");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("#vistaTabla", isVisible());
        verifyThat("#vistaDetalles", isInvisible());
        assertEquals(initialSize - 1, tabla.getItems().size(), "La tabla debe tener una asignación menos.");
        Label lblEstado = lookup("#lblEstadoTabla").queryAs(Label.class);
        assertEquals((initialSize - 1) + " asignaciones", lblEstado.getText());
    }
}
