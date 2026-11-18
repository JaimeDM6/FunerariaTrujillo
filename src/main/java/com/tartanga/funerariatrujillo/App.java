package com.tartanga.funerariatrujillo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

/**
 * Clase principal de la aplicación JavaFX de la Funeraria Trujillo. Crea la ventana, carga la
 * pantalla de inicio de sesión y permite cambiar la vista raíz de la escena.
 *
 * @author Aritz
 */
public class App extends Application {

    private static Scene scene;

    /**
     * Arranca la aplicación: carga la pantalla de inicio de sesión, establece el título, el icono y
     * las fuentes, y muestra la ventana.
     *
     * @param stage ventana principal proporcionada por JavaFX
     * @throws IOException si no se puede cargar el archivo FXML o un recurso
     */
    @Override
    public void start(Stage stage) throws IOException {
        loadFonts();

        scene = new Scene(loadFXML("login"), 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Funeraria Trujillo");
        stage.getIcons().add(new Image(App.class.getResourceAsStream("images/Icon.png")));
        stage.show();
    }

    private void loadFonts() throws IOException {
        loadFont("/com/tartanga/funerariatrujillo/fonts/Inter_18pt-Regular.ttf");
        loadFont("/com/tartanga/funerariatrujillo/fonts/Inter_18pt-Medium.ttf");
        loadFont("/com/tartanga/funerariatrujillo/fonts/Inter_18pt-Bold.ttf");
    }

    private void loadFont(String path) throws IOException {

        InputStream resource = getClass().getResourceAsStream(path);

        if (resource == null) {
            throw new IllegalStateException(
                    "No se encuentra la fuente: " + path
            );
        }

        try (InputStream input = resource) {

            Font font = Font.loadFont(input, 14);

            if (font == null) {
                throw new IllegalStateException(
                        "No se pudo cargar la fuente: " + path
                );
            }
        }
    }

    /**
     * Cambia el contenido de la escena cargando otro archivo FXML.
     *
     * @param fxml nombre del archivo FXML, sin la extensión
     * @throws IOException si no se puede cargar el archivo FXML
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carga un archivo FXML y devuelve su vista.
     *
     * @param fxml nombre del archivo FXML, sin la extensión
     * @return raíz de la vista cargada
     * @throws IOException si no se puede cargar el archivo FXML
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Método de arranque de la aplicación.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        launch();
    }
}
