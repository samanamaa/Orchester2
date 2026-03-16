module com.example.tlacidlo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tlacidlo to javafx.fxml;
    exports com.example.tlacidlo;
}