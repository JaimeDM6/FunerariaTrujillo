package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO;
import com.tartanga.funerariatrujillo.Objects.Asignacion;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;

public class AsignacionesTanatopraxiaControllerTest extends ApplicationTest {

    public static class MockTanatopraxiaDAO extends AsignacionesDAO {

        @Override
        public boolean puedeGestionar() {
            return false;
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
            a.setEstado("Pendiente");
            a.setFechaAsignacion(new Date());
            a.setIndicacionesFamilia("Presentacion sencilla");
            lista.add(a);
            return lista;
        }

        @Override
        public boolean actualizarAsignacion(Asignacion a) {
            a.setUltimaActualizacion(new Date());
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
                    return new AsignacionesController(new MockTanatopraxiaDAO());
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
    public void testCannotCreate() {
        verifyThat("#btnNuevaAsignacion", isInvisible());
    }

    @Test
    public void testLimitedEditing() {
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

        verifyThat("#vistaDetalles", isVisible());
        verifyThat("#btnEliminar", isInvisible());

        clickOn("#btnEditar");

        ComboBox<String> cmbDifunto = lookup("#cmbDetalleDifunto").queryComboBox();
        ComboBox<String> cmbTrabajador = lookup("#cmbDetalleTrabajador").queryComboBox();
        ComboBox<String> cmbEstado = lookup("#cmbDetalleEstado").queryComboBox();
        CheckBox chkIdentificacion = lookup("#chkDetalleIdentificacion").queryAs(CheckBox.class);
        TextArea txtIndicaciones = lookup("#txtDetalleIndicaciones").queryAs(TextArea.class);
        TextArea txtObservaciones = lookup("#txtDetalleObservaciones").queryAs(TextArea.class);

        assertTrue(cmbDifunto.isDisabled(), "Tanatopraxia no puede cambiar el difunto.");
        assertTrue(cmbTrabajador.isDisabled(), "Tanatopraxia no puede cambiar el trabajador.");
        assertFalse(txtIndicaciones.isEditable(), "Tanatopraxia no puede cambiar las indicaciones.");
        assertFalse(cmbEstado.isDisabled(), "Tanatopraxia puede cambiar el estado.");
        assertFalse(chkIdentificacion.isDisabled(), "Tanatopraxia puede actualizar el seguimiento.");
        assertTrue(txtObservaciones.isEditable(), "Tanatopraxia puede escribir observaciones.");

        clickOn("#btnGuardarEdicion");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(cmbEstado.isDisabled());
        verifyThat("#btnEliminar", isInvisible());
    }
}