module com.tartanga.funerariatrujillo2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.tartanga.funerariatrujillo2 to javafx.fxml;
    exports com.tartanga.funerariatrujillo2;
}
