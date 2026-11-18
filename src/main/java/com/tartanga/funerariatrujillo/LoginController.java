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
import javafx.scene.shape.SVGPath;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.Initializable;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private Button btnMostrarPassword;
    @FXML
    private SVGPath svgOjo;
    @FXML
    private CheckBox chkRememberUser;
    @FXML
    private Button btnLogin;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Label lblError;

    private boolean passwordVisible = false;

    private final Preferences preferences = Preferences.userNodeForPackage(LoginController.class);
    private final TrabajadorDAO trabajadorDAO;

    private Runnable onLoginSuccess = new Runnable() {
        @Override
        public void run() {
            abrirAplicacion();
        }
    };

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
        chkRememberUser.setSelected(!txtUser.getText().trim().isEmpty());
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());

        btnMostrarPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                passwordVisible = !passwordVisible;

                txtPassword.setVisible(!passwordVisible);
                txtPassword.setManaged(!passwordVisible);
                txtPasswordVisible.setVisible(passwordVisible);
                txtPasswordVisible.setManaged(passwordVisible);

                if (passwordVisible) {
                    svgOjo.setContent("M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z");
                } else {
                    svgOjo.setContent("M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z");
                }
            }
        });

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

        txtPasswordVisible.setOnAction(new EventHandler<ActionEvent>() {
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

            tarea.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    finalizarAutenticacion(tarea.getValue());
                }
            });

            tarea.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    btnLogin.setDisable(false);
                    txtPassword.clear();
                    lblError.setText("No se pudo conectar con el servicio de autenticación.");
                }
            });

            Thread hilo = new Thread(tarea, "login-authentication");
            hilo.setDaemon(true);
            hilo.start();

        } catch (Throwable e) {
            btnLogin.setDisable(false);
            lblError.setText("Error crítico en el sistema.");
            e.printStackTrace();
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
            exception.printStackTrace();
        }
    }
}
