module com.tartanga.funerariatrujillo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    opens com.tartanga.funerariatrujillo to javafx.fxml;
    exports com.tartanga.funerariatrujillo;
    
    opens com.tartanga.funerariatrujillo.Objects to javafx.base;
    exports com.tartanga.funerariatrujillo.Objects;
    
    exports com.tartanga.funerariatrujillo.DAO;
    exports com.tartanga.funerariatrujillo.Utils;
}
