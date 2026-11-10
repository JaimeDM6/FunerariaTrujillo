package com.tartanga.funerariatrujillo;

import com.tartanga.funerariatrujillo.DAO.TrabajadorDAO;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import com.tartanga.funerariatrujillo.Security.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginControllerTest extends ApplicationTest {

    private LoginController controller;
    private MockTrabajadorDAO dao;

    @Override
    public void start(Stage stage) throws IOException {
        dao = new MockTrabajadorDAO();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/tartanga/funerariatrujillo/login.fxml"));
        loader.setControllerFactory(type -> {
            if (type == LoginController.class) {
                controller = new LoginController(dao);
                controller.setOnLoginSuccess(() -> {
                });
                return controller;
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("No se pudo crear " + type.getName(), exception);
            }
        });
        stage.setScene(new Scene(loader.load(), 1000, 700));
        stage.show();
    }

    @AfterEach
    void cerrarSesion() {
        UserSession.logout();
    }

    @Test
    void muestraErrorConCamposVacios() {
        limpiarCampos();
        clickOn("#btnLogin");

        assertEquals("Por favor, introduce usuario y contraseña.",
                lookup("#lblError").queryAs(Label.class).getText());
    }

    @Test
    void muestraErrorConCredencialesInvalidas() throws Exception {
        limpiarCampos();
        clickOn("#txtUser").write("desconocido");
        clickOn("#txtPassword").write("incorrecta");
        clickOn("#btnLogin");
        WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS,
                () -> lookup("#lblError").queryAs(Label.class).getText().contains("incorrectos"));

        assertEquals("Usuario o contraseña incorrectos.",
                lookup("#lblError").queryAs(Label.class).getText());
        assertTrue(dao.autenticarFueLlamado);
    }

    @Test
    void iniciaSesionConCredencialesValidas() throws Exception {
        limpiarCampos();
        clickOn("#txtUser").write("m.ruiz");
        clickOn("#txtPassword").write("password123");
        clickOn("#btnLogin");
        WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS,
                () -> UserSession.getCurrentUser() != null);

        assertSame(dao.trabajador, UserSession.getCurrentUser());
        assertEquals("m.ruiz", UserSession.getCurrentUser().getUsuario());
    }

    private void limpiarCampos() {
        interact(() -> {
            lookup("#txtUser").queryAs(TextField.class).clear();
            lookup("#txtPassword").queryAs(PasswordField.class).clear();
        });
    }

    private static class MockTrabajadorDAO extends TrabajadorDAO {

        private final Trabajador trabajador;
        private boolean autenticarFueLlamado;

        private MockTrabajadorDAO() {
            trabajador = new Trabajador();
            trabajador.setUsuario("m.ruiz");
            trabajador.setNombre("Maite");
            trabajador.setRol(Trabajador.Rol.ADMINISTRACION);
        }

        @Override
        public Optional<Trabajador> autenticar(String usuario, char[] password) {
            autenticarFueLlamado = true;
            java.util.Arrays.fill(password, '\0');
            if ("m.ruiz".equals(usuario)) {
                return Optional.of(trabajador);
            }
            return Optional.empty();
        }
    }
}
