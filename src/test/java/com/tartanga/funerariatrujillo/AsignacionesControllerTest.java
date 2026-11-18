package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO;
import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO.ResumenAsignacion;
import com.tartanga.funerariatrujillo.DAO.AsignacionesDAO.TanatopractorResumen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AsignacionesControllerTest extends ApplicationTest {

    public static class MockAsignacionDAO extends AsignacionesDAO {

        private boolean asignacionConfirmada = false;

        @Override
        public List<ResumenAsignacion> obtenerPendientesDeAsignar() {
            List<ResumenAsignacion> lista = new ArrayList<>();

            ResumenAsignacion p1 = new ResumenAsignacion();
            p1.setIdDifunto("650000000000000000000044");
            p1.setCodigoServicio("SV-0044");
            p1.setDifuntoNombre("Pilar Fernández Mora");
            p1.setClienteInfo("Lucía Fernández Gil · CL-0188");
            p1.setTrabajoSolicitado("Tanatoestética");
            p1.setFechaLlegada(new Date());
            p1.setEstado("Pendiente");
            lista.add(p1);

            ResumenAsignacion p2 = new ResumenAsignacion();
            p2.setIdDifunto("650000000000000000000045");
            p2.setCodigoServicio("SV-0045");
            p2.setDifuntoNombre("Antonio Ruiz León");
            p2.setClienteInfo("Carlos Ruiz · CL-0190");
            p2.setTrabajoSolicitado("Conservación temporal");
            p2.setFechaLlegada(new Date());
            p2.setEstado("Pendiente");
            lista.add(p2);

            return lista;
        }

        @Override
        public List<ResumenAsignacion> obtenerTrabajoAsignado() {
            List<ResumenAsignacion> lista = new ArrayList<>();

            ResumenAsignacion a1 = new ResumenAsignacion();
            a1.setId("asig-0042");
            a1.setIdDifunto("650000000000000000000042");
            a1.setCodigoServicio("SV-0042");
            a1.setDifuntoNombre("Manuel García Pérez");
            a1.setIdTrabajador("trab-0001");
            a1.setTrabajadorNombre("Ana Martín");
            a1.setCodigoTrabajador("TR-0001");
            a1.setTrabajadorCargo("TANATOPRACTOR");
            a1.setEstado("En curso");
            lista.add(a1);

            ResumenAsignacion a2 = new ResumenAsignacion();
            a2.setId("asig-0043");
            a2.setIdDifunto("650000000000000000000043");
            a2.setCodigoServicio("SV-0043");
            a2.setDifuntoNombre("Carmen López Díaz");
            a2.setIdTrabajador("trab-0002");
            a2.setTrabajadorNombre("Luis Ortega");
            a2.setCodigoTrabajador("TR-0002");
            a2.setTrabajadorCargo("TANATOPRACTOR");
            a2.setEstado("Pendiente");
            lista.add(a2);

            return lista;
        }

        @Override
        public List<TanatopractorResumen> obtenerTanatopractores() {
            List<TanatopractorResumen> lista = new ArrayList<>();
            lista.add(new TanatopractorResumen("trab-0001", "Ana Martín", "TR-0001", "TANATOPRACTOR"));
            lista.add(new TanatopractorResumen("trab-0002", "Luis Ortega", "TR-0002", "TANATOPRACTOR"));
            return lista;
        }

        @Override
        public boolean confirmarAsignacion(ResumenAsignacion pendiente, TanatopractorResumen tanatopractor) {
            pendiente.setId("asig-nueva-0044");
            pendiente.setIdTrabajador(tanatopractor.getId());
            pendiente.setTrabajadorNombre(tanatopractor.getNombreCompleto());
            pendiente.setCodigoTrabajador(tanatopractor.getCodigo());
            pendiente.setTrabajadorCargo(tanatopractor.getCargo());
            pendiente.setEstado("Pendiente");
            pendiente.setFechaAsignacion(new Date());
            asignacionConfirmada = true;
            return true;
        }

        public boolean isAsignacionConfirmada() {
            return asignacionConfirmada;
        }
    }

    private MockAsignacionDAO mockDAO;
    private AsignacionesController controlador;
    private AtomicReference<String> fichaAbiertaId;

    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockAsignacionDAO();
        fichaAbiertaId = new AtomicReference<>();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tartanga/funerariatrujillo/asignaciones.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> type) {
                if (type == AsignacionesController.class) {
                    controlador = new AsignacionesController(mockDAO);
                    controlador.setOnAbrirFicha(id -> fichaAbiertaId.set(id));
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
    }
    
    @BeforeEach
    public void esperarCargaInicial() throws Exception {
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                () -> !lookup("#tablaPendientes").queryTableView().getItems().isEmpty());
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @Order(1)
    public void testCargaInicialTablasYPanel() {
        TableView<?> tablaPendientes = lookup("#tablaPendientes").queryTableView();
        TableView<?> tablaAsignados = lookup("#tablaAsignados").queryTableView();
        Label lblBadge = lookup("#lblBadgePendientes").queryAs(Label.class);
        Label lblTituloAsig = lookup("#lblTituloAsignados").queryAs(Label.class);
        Label lblPanelDifunto = lookup("#lblPanelDifunto").queryAs(Label.class);
        Label lblServicioSel = lookup("#lblServicioSeleccionado").queryAs(Label.class);
        VBox vboxCarga = lookup("#vboxCargaTrabajo").queryAs(VBox.class);

        assertEquals(2, tablaPendientes.getItems().size());
        assertEquals(2, tablaAsignados.getItems().size());
        assertEquals("● Pendiente · 2", lblBadge.getText());
        assertEquals("Trabajo asignado · 2", lblTituloAsig.getText());
        assertEquals("Pilar Fernández Mora", lblPanelDifunto.getText());
        assertEquals("Servicio seleccionado · SV-0044", lblServicioSel.getText());
        assertEquals(2, vboxCarga.getChildren().size());
    }

    @Test
    @Order(2)
    public void testFiltroBusqueda() {
        TableView<?> tablaPendientes = lookup("#tablaPendientes").queryTableView();
        TableView<?> tablaAsignados = lookup("#tablaAsignados").queryTableView();

        clickOn("#txtBuscar").write("Antonio");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, tablaPendientes.getItems().size());
        assertEquals(0, tablaAsignados.getItems().size());

        clickOn("#txtBuscar");
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.BACK_SPACE);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(2, tablaPendientes.getItems().size());
        assertEquals(2, tablaAsignados.getItems().size());
    }

    @Test
    @Order(3)
    public void testConfirmarSinTanatopractorMuestraError() {
        ComboBox<?> cmb = lookup("#cmbTanatopractor").queryComboBox();
        TableView<?> tablaPendientes = lookup("#tablaPendientes").queryTableView();

        clickOn("#btnConfirmarAsignacion");
        sleep(400);
        type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(cmb.getStyleClass().contains("error"));
        assertFalse(mockDAO.isAsignacionConfirmada());
        assertEquals(2, tablaPendientes.getItems().size());
    }

    @Test
    @Order(4)
    public void testConfirmarAsignacionExitosa() {
        ComboBox<TanatopractorResumen> cmb = lookup("#cmbTanatopractor").queryComboBox();
        TableView<?> tablaPendientes = lookup("#tablaPendientes").queryTableView();
        TableView<?> tablaAsignados = lookup("#tablaAsignados").queryTableView();

        interact(() -> cmb.getSelectionModel().select(1));
        assertNotNull(cmb.getValue());

        clickOn("#btnConfirmarAsignacion");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(mockDAO.isAsignacionConfirmada());
        assertEquals(1, tablaPendientes.getItems().size());
        assertEquals(3, tablaAsignados.getItems().size());
        assertEquals("● Pendiente · 1", lookup("#lblBadgePendientes").queryAs(Label.class).getText());
        assertEquals("Trabajo asignado · 3", lookup("#lblTituloAsignados").queryAs(Label.class).getText());
    }

    @Test
    @Order(5)
    public void testAbrirFichaSeleccionada() {
        TableView<?> tablaAsignados = lookup("#tablaAsignados").queryTableView();

        interact(() -> tablaAsignados.getSelectionModel().selectFirst());
        clickOn("#btnAbrirFicha");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("asig-0042", fichaAbiertaId.get());
    }
}
