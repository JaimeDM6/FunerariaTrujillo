package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.FichaServicioDAO;
import com.tartanga.funerariatrujillo.Objects.Servicio;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FichaServicioControllerTest extends ApplicationTest {

    public static class MockServicioDAO extends FichaServicioDAO {

        @Override
        public Servicio obtenerServicioPorId(String idAsignacion) {
            Servicio s = new Servicio();
            s.setIdAsignacion(idAsignacion);
            s.setTituloServicio("SV-TEST");
            s.setEstado("Pendiente");
            s.setTrabajadorNombre("Ana Martín");
            s.setTrabajadorCargo("TANATOPRACTOR");
            s.setDifuntoNombre("Manuel García Pérez");
            s.setDifuntoFallecimiento(new Date());
            s.setProcedencia("Hospital Universitario");
            s.setCausaMuerte("Paro cardíaco");
            s.setClienteNombre("Elena García");
            s.setTipoServicio("ENTIERRO");
            s.setFeretroInfo("Madera de roble (190x60)");
            s.setUrnaInfo("No aplica");
            s.setIndicacionesFamilia("Presentación sencilla");
            s.setPasoIdentificacion(false);
            s.setPasoConservacion(false);
            s.setPasoEstetica(false);
            s.setObservaciones("");
            s.setUltimaActualizacion(null);

            return s;
        }

        @Override
        public boolean actualizarSeguimientoServicio(Servicio s) {
            return true;
        }
    }

    private FichaServicioController controlador;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/ficha_servicio.fxml"));

        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == FichaServicioController.class) {
                    controlador = new FichaServicioController(new MockServicioDAO());
                    return controlador;
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

        Platform.runLater(() -> controlador.cargarServicioDesdeBD("mock-id-1234"));
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @Order(1)
    public void testInitialStateDataLoaded() {

        Label lblTitulo = lookup("#lblTituloFicha").queryAs(Label.class);
        Label lblTrabajador = lookup("#lblPanelTrabajador").queryAs(Label.class);
        Label lblDifunto = lookup("#lblDifuntoNombre").queryAs(Label.class);
        Label lblProcedencia = lookup("#lblDifuntoProcedencia").queryAs(Label.class);

        assertEquals("Ficha del servicio · SV-TEST", lblTitulo.getText());
        assertEquals("Ana Martín · TANATOPRACTOR", lblTrabajador.getText());
        assertEquals("Manuel García Pérez", lblDifunto.getText());
        assertEquals("Hospital Universitario", lblProcedencia.getText());
    }

    @Test
    @Order(2)
    public void testSeguimientoInitialValues() {
        ComboBox<String> cmb = lookup("#cmbEstadoServicio").queryComboBox();
        assertEquals("Pendiente", cmb.getValue(), "El estado inicial debería ser Pendiente.");

        CheckBox chk1 = lookup("#chkPaso1").queryAs(CheckBox.class);
        CheckBox chk2 = lookup("#chkPaso2").queryAs(CheckBox.class);
        CheckBox chk3 = lookup("#chkPaso3").queryAs(CheckBox.class);

        assertFalse(chk1.isSelected());
        assertFalse(chk2.isSelected());
        assertFalse(chk3.isSelected());

        TextField txt = lookup("#txtObservaciones").queryAs(TextField.class);
        assertEquals("", txt.getText());
    }

    @Test
    @Order(3)
    public void testUpdateSeguimiento() {
        clickOn("#cmbEstadoServicio");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);

        clickOn("#chkPaso1");
        clickOn("#chkPaso2");

        clickOn("#txtObservaciones").write("Identidad verificada. Preparando.");

        ComboBox<String> cmb = lookup("#cmbEstadoServicio").queryComboBox();
        assertEquals("En curso", cmb.getValue());

        CheckBox chk1 = lookup("#chkPaso1").queryAs(CheckBox.class);
        CheckBox chk2 = lookup("#chkPaso2").queryAs(CheckBox.class);
        assertTrue(chk1.isSelected());
        assertTrue(chk2.isSelected());

        TextField txt = lookup("#txtObservaciones").queryAs(TextField.class);
        assertEquals("Identidad verificada. Preparando.", txt.getText());
    }

    @Test
    @Order(4)
    public void testGuardarCambios() {
        clickOn("#chkPaso3");
        clickOn("#txtObservaciones").write(" Y finalizado.");

        clickOn("#btnGuardarCambios");
        sleep(500);

        type(javafx.scene.input.KeyCode.ENTER);
        TextField txt = lookup("#txtObservaciones").queryAs(TextField.class);
        assertEquals(" Y finalizado.", txt.getText());
    }
}
