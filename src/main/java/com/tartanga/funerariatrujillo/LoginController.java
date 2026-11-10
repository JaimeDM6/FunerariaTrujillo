package com.tartanga.funerariatrujillo;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.prefs.Preferences;
import com.tartanga.funerariatrujillo.DAO.TrabajadorDAO;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import com.tartanga.funerariatrujillo.Security.UserSession;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private CheckBox chkRememberUser;
    @FXML
    private Button btnLogin;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Label lblError;

    private final Preferences preferences = Preferences.userNodeForPackage(LoginController.class);
    private final TrabajadorDAO trabajadorDAO;
    private Runnable onLoginSuccess = this::abrirAplicacion;

    public LoginController() {
        this(new TrabajadorDAO());
    }

    LoginController(TrabajadorDAO trabajadorDAO) {
        this.trabajadorDAO = trabajadorDAO;
    }

    void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtUser.setText(preferences.get("usuario", ""));
        chkRememberUser.setSelected(!txtUser.getText().isBlank());

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                iniciarSesion();
            }
        });

        txtPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                iniciarSesion();
            }
        });

        txtUser.requestFocus();
    }

    private void iniciarSesion() {
        try {
            lblError.setText("");

            if (txtUser.getText().trim().isEmpty() || txtPassword.getText().isEmpty()) {
                lblError.setText("Por favor, introduce usuario y contraseña.");
                return;
            }

            String usuario = txtUser.getText().trim();
            char[] password = txtPassword.getText().toCharArray();
            btnLogin.setDisable(true);
            lblError.setText("Comprobando credenciales...");

            Task<Optional<Trabajador>> tarea = new Task<Optional<Trabajador>>() {
                @Override
                protected Optional<Trabajador> call() {
                    return trabajadorDAO.autenticar(usuario, password);
                }
            };
            tarea.setOnSucceeded(event -> finalizarAutenticacion(tarea.getValue()));
            tarea.setOnFailed(event -> {
                btnLogin.setDisable(false);
                txtPassword.clear();
                lblError.setText("No se pudo conectar con el servicio de autenticación.");
            });
            Thread hilo = new Thread(tarea, "login-authentication");
            hilo.setDaemon(true);
            hilo.start();

        } catch (Throwable e) {
            btnLogin.setDisable(false);
            lblError.setText("Error crítico en el sistema.");
        }
    }

    private void finalizarAutenticacion(Optional<Trabajador> resultado) {
        btnLogin.setDisable(false);
        if (resultado.isEmpty()) {
            lblError.setText("Usuario o contraseña incorrectos.");
            txtPassword.clear();
            return;
        }

        if (chkRememberUser.isSelected()) {
            preferences.put("usuario", txtUser.getText().trim());
        } else {
            preferences.remove("usuario");
        }

        UserSession.login(resultado.get());
        onLoginSuccess.run();
    }

    private void abrirAplicacion() {
        try {
            App.setRoot("layout");
        } catch (java.io.IOException exception) {
            lblError.setText("No se pudo abrir la aplicación principal.");
        }
    }
}
