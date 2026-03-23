package com.example.orchester;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private ObservableList<Nastroj> nastroje = FXCollections.observableArrayList();

    @FXML private TableView<Nastroj> table;
    @FXML private TableColumn<Nastroj, String> colNazov;
    @FXML private TableColumn<Nastroj, Double> colCena;
    @FXML private TableColumn<Nastroj, Integer> colPocet;
    @FXML private TableColumn<Nastroj, String> colZvuk;
    @FXML private TableColumn<Nastroj, Integer> colPocetKlavesov;
    @FXML private TableColumn<Nastroj, Integer> colPocetZvukov;
    @FXML
    private ListView<String> skladListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colNazov.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNazov()));
        colCena.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCena()));
        colPocet.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPocet()));
        colZvuk.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getZvuk()));

        colPocetKlavesov.setCellValueFactory(c -> {
            if (c.getValue() instanceof KlavesovyNastroj) {
                return new SimpleObjectProperty<>(((KlavesovyNastroj)c.getValue()).getPocetKlavesov());
            }
            return new SimpleObjectProperty<>(0);
        });

        colPocetZvukov.setCellValueFactory(c -> {
            if (c.getValue() instanceof RytmickyNastroj) {
                return new SimpleObjectProperty<>(((RytmickyNastroj)c.getValue()).getPocetZvukov());
            }
            return new SimpleObjectProperty<>(0);
        });

        table.setItems(nastroje);

        nacitaj();
    }

    @FXML
    protected void nacitaj() {
        try {
            InputStream is = getClass().getResourceAsStream("/orchester.txt");
            if (is == null) return;

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            nastroje.clear();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals("r")) {
                    nastroje.add(new RytmickyNastroj(
                            data[1], Double.parseDouble(data[2]),
                            data[3], Integer.parseInt(data[4]),
                            Integer.parseInt(data[5])
                    ));
                } else if (data[0].equals("k")) {
                    nastroje.add(new KlavesovyNastroj(
                            data[1], Double.parseDouble(data[2]),
                            data[3], Integer.parseInt(data[4]),
                            Integer.parseInt(data[5])
                    ));
                }
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void pridajNastroj() {
        Stage modal = new Stage();
        modal.initModality(Modality.NONE);
        modal.setTitle("Pridať nový nástroj");

        TextField tfNazov = new TextField();
        TextField tfCena = new TextField();
        TextField tfPocet = new TextField();
        TextField tfZvuk = new TextField();
        TextField tfPocetKlavesov = new TextField();
        TextField tfPocetZvukov = new TextField();

        Button btnPridat = new Button("Pridať");
        btnPridat.setOnAction(e -> {
            String nazov = tfNazov.getText();
            double cena = Double.parseDouble(tfCena.getText());
            int pocet = Integer.parseInt(tfPocet.getText());
            String zvuk = tfZvuk.getText();
            int pocetKlavesov = tfPocetKlavesov.getText().isEmpty() ? 0 : Integer.parseInt(tfPocetKlavesov.getText());
            int pocetZvukov = tfPocetZvukov.getText().isEmpty() ? 0 : Integer.parseInt(tfPocetZvukov.getText());

            Nastroj novy;
            if (pocetKlavesov > 0) {
                novy = new KlavesovyNastroj(nazov, cena, zvuk, pocet, pocetKlavesov);
            } else {
                novy = new RytmickyNastroj(nazov, cena, zvuk, pocet, pocetZvukov);
            }

            nastroje.add(novy);
            table.refresh();
            modal.close();
        });

        VBox layout = new VBox(10,
                new Label("Názov:"), tfNazov,
                new Label("Cena:"), tfCena,
                new Label("Počet:"), tfPocet,
                new Label("Zvuk:"), tfZvuk,
                new Label("Počet klávesov (len pre klávesy):"), tfPocetKlavesov,
                new Label("Počet zvukov (len pre rytmické):"), tfPocetZvukov,
                btnPridat
        );
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout);
        modal.setScene(scene);
        modal.show();
    }
    @FXML
    protected void skladHraj() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Cigáň hraj!");

        ListView<String> listView = new ListView<>();
        for (Nastroj n : nastroje) {
            listView.getItems().add(n.getZvuk());
        }

        listView.setPrefSize(400, 300);

        Scene scene = new Scene(listView);
        modal.setScene(scene);
        modal.showAndWait();
    }
    @FXML
    protected void cenaSkladu(){

    }
}