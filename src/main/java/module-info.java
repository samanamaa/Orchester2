module com.example.orchester {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.orchester to javafx.fxml;
    exports com.example.orchester;
}