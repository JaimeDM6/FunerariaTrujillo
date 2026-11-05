package com.tartanga.funerariatrujillo.Utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static void openForm(VBox panelFormulario, Region spacerLateral) {
        if (panelFormulario.getTranslateX() != 0) {
            panelFormulario.setVisible(true);

            Timeline slide = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(panelFormulario.translateXProperty(), 400),
                            new KeyValue(spacerLateral.prefWidthProperty(), 0),
                            new KeyValue(spacerLateral.minWidthProperty(), 0)
                    ),
                    new KeyFrame(Duration.millis(350),
                            new KeyValue(panelFormulario.translateXProperty(), 0),
                            new KeyValue(spacerLateral.prefWidthProperty(), 370),
                            new KeyValue(spacerLateral.minWidthProperty(), 370)
                    )
            );
            slide.play();
        }
    }

    public static void closeForm(VBox panelFormulario, Region spacerLateral) {
        Timeline slide = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(panelFormulario.translateXProperty(), 0),
                        new KeyValue(spacerLateral.prefWidthProperty(), 370),
                        new KeyValue(spacerLateral.minWidthProperty(), 370)
                ),
                new KeyFrame(Duration.millis(350),
                        new KeyValue(panelFormulario.translateXProperty(), 400),
                        new KeyValue(spacerLateral.prefWidthProperty(), 0),
                        new KeyValue(spacerLateral.minWidthProperty(), 0)
                )
        );

        slide.setOnFinished(e -> panelFormulario.setVisible(false));
        slide.play();
    }

    // Método para recortar el ID a 6 caracteres
    public static <T> void columnID(TableColumn<T, String> columna) {
        columna.setCellFactory(col -> new TableCell<T, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.length() > 6 ? item.substring(item.length() - 6).toUpperCase() : item);
                }
            }
        });
    }

    // Método para formatear Fechas a DD/MM/YYYY
    public static <T> void columnDate(TableColumn<T, Date> columna) {
        columna.setCellFactory(col -> new TableCell<T, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : format.format(item));
            }
        });
    }
}
