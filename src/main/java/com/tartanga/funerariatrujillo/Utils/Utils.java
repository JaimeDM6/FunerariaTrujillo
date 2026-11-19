package com.tartanga.funerariatrujillo.Utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilidades compartidas por las vistas: apertura y cierre del panel lateral de formularios,
 * formato de columnas de tabla y validaciones de datos comunes.
 *
 * @author Aritz
 */
public class Utils {

    /**
     * Muestra el panel lateral del formulario y oculta el espaciador.
     *
     * @param panelFormulario panel del formulario
     * @param spacerLateral espaciador lateral que ocupa su sitio mientras el panel está cerrado
     */
    public static void openForm(VBox panelFormulario, Region spacerLateral) {
        if (panelFormulario.getTranslateX() != 0) {
            panelFormulario.setVisible(true);

            Timeline slide = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(panelFormulario.translateXProperty(), 400),
                            new KeyValue(spacerLateral.prefWidthProperty(), 0),
                            new KeyValue(spacerLateral.minWidthProperty(), 0)),
                    new KeyFrame(Duration.millis(350),
                            new KeyValue(panelFormulario.translateXProperty(), 0),
                            new KeyValue(spacerLateral.prefWidthProperty(), 370),
                            new KeyValue(spacerLateral.minWidthProperty(), 370)));
            slide.play();
        }
    }

    /**
     * Oculta el panel lateral del formulario y vuelve a mostrar el espaciador.
     *
     * @param panelFormulario panel del formulario
     * @param spacerLateral espaciador lateral que ocupa su sitio mientras el panel está cerrado
     */
    public static void closeForm(VBox panelFormulario, Region spacerLateral) {
        Timeline slide = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(panelFormulario.translateXProperty(), 0),
                        new KeyValue(spacerLateral.prefWidthProperty(), 370),
                        new KeyValue(spacerLateral.minWidthProperty(), 370)),
                new KeyFrame(Duration.millis(350),
                        new KeyValue(panelFormulario.translateXProperty(), 400),
                        new KeyValue(spacerLateral.prefWidthProperty(), 0),
                        new KeyValue(spacerLateral.minWidthProperty(), 0)));

        slide.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                panelFormulario.setVisible(false);
            }
        });

        slide.play();
    }

    /**
     * Configura una columna para mostrar solo los seis últimos caracteres del identificador, en
     * mayúsculas.
     *
     * @param columna columna de identificadores
     */
    public static <T> void columnID(TableColumn<T, String> columna) {
        columna.setCellFactory(new Callback<TableColumn<T, String>, TableCell<T, String>>() {
            @Override
            public TableCell<T, String> call(TableColumn<T, String> param) {
                return new TableCell<T, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.length() > 6 ? item.substring(item.length() - 6).toUpperCase() : item);
                        }
                    }
                };
            }
        });
    }

    /**
     * Configura una columna para mostrar las fechas con el formato dd/MM/yyyy.
     *
     * @param columna columna de fechas
     */
    public static <T> void columnDate(TableColumn<T, Date> columna) {
        columna.setCellFactory(new Callback<TableColumn<T, Date>, TableCell<T, Date>>() {
            @Override
            public TableCell<T, Date> call(TableColumn<T, Date> param) {
                return new TableCell<T, Date>() {
                    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        setText((empty || item == null) ? null : format.format(item));
                    }
                };
            }
        });
    }

    /**
     * Comprueba que un DNI tenga ocho dígitos seguidos de una letra.
     *
     * @param dni DNI que se comprueba
     * @return {@code true} si el DNI es válido
     */
    public static boolean dniValido(String dni) {
        return dni != null && dni.matches("^[0-9]{8}[A-Za-z]$");
    }

    /**
     * Comprueba que un correo electrónico tenga la forma usuario@dominio y termine en .com o .es.
     *
     * @param correo correo que se comprueba
     * @return {@code true} si el correo es válido
     */
    public static boolean correoValido(String correo) {
        return correo != null && correo.matches("^.+@.+\\.(com|es)$");
    }

    /**
     * Comprueba que un teléfono tenga exactamente nueve dígitos.
     *
     * @param telefono teléfono que se comprueba
     * @return {@code true} si el teléfono es válido
     */
    public static boolean telefonoValido(String telefono) {
        return telefono != null && telefono.matches("^[0-9]{9}$");
    }

    /**
     * Comprueba que un código postal tenga exactamente cinco dígitos.
     *
     * @param cp código postal que se comprueba
     * @return {@code true} si el código postal es válido
     */
    public static boolean cpValido(String cp) {
        return cp != null && cp.matches("^[0-9]{5}$");
    }
}
