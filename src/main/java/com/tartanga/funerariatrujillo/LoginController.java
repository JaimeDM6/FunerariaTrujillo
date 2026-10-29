package com.tartanga.funerariatrujillo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

    public void initialize(URL location, ResourceBundle resources) {

    }
}
