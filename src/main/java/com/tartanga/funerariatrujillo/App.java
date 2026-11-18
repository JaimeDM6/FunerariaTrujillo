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
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

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

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
