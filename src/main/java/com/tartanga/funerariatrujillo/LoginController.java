package com.tartanga.funerariatrujillo;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.prefs.Preferences;
import com.tartanga.funerariatrujillo.DAO.TrabajadorDAO;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import com.tartanga.funerariatrujillo.Security.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class LoginController {

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

    public void initialize(URL location, ResourceBundle resources) {
        txtUser.setText(preferences.get("usuario", ""));
        chkRememberUser.setSelected(!txtUser.getText().isBlank());
        btnLogin.setOnAction(event -> iniciarSesion());
        txtPassword.setOnAction(event -> iniciarSesion());
        txtUser.requestFocus();
    }

    private void iniciarSesion() {
        lblError.setText("");
        Optional<Trabajador> trabajador = new TrabajadorDAO()
                .autenticar(txtUser.getText(), txtPassword.getText().toCharArray());
        if (trabajador.isEmpty()) {
            lblError.setText("Usuario o contraseña incorrectos.");
            txtPassword.clear();
            return;
        }

        if (chkRememberUser.isSelected()) {
            preferences.put("usuario", txtUser.getText().trim());
        } else {
            preferences.remove("usuario");
        }
        UserSession.login(trabajador.get());
        try {
            App.setRoot("layout");
        } catch (java.io.IOException exception) {
            lblError.setText("No se pudo abrir la aplicación.");
        }
    }
}
