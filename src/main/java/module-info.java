module com.toochi.resultmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires kotlin.stdlib;


    opens com.toochi.resultmanagement to javafx.fxml;
    exports com.toochi.resultmanagement;
}