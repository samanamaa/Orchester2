package com.example.tlacidlo;

import javafx.fxml.FXML;
    import javafx.scene.control.Button;

public class HelloController {
    @FXML
    private Button b;

    @FXML
    protected void onHelloButtonClick() {
        if (b.getText().equals("Ahoj!")) {
            b.setText("Cau!");
            b.setMinHeight(20);
            b.setMinWidth(20);
        }
        else {
            b.setText("Ahoj!");
            b.setMinHeight(40);
            b.setMinWidth(40);
        }
    }
}
