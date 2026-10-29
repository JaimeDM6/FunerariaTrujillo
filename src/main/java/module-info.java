module com.tartanga.funerariatrujillo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.tartanga.funerariatrujillo to javafx.fxml;
    exports com.tartanga.funerariatrujillo;
}
