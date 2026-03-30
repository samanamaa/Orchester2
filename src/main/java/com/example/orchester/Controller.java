package com.example.orchester;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    @FXML private TableColumn<Nastroj, Void> colAkcie;
    @FXML private TableColumn<Nastroj, Void> colAkcie1;
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
        nastavAkcneStlpce();

        nacitaj();
    }

    private void nastavAkcneStlpce() {
        colAkcie.setCellFactory(column -> new TableCell<>() {
            private final Button button = vytvorIkonoveTlacidlo("/deleteicon.png", "Delete");

            {
                button.setOnAction(event -> {
                    Nastroj nastroj = getTableView().getItems().get(getIndex());
                    nastroje.remove(nastroj);
                });
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });

        colAkcie1.setCellFactory(column -> new TableCell<>() {
            private final Button button = vytvorIkonoveTlacidlo("/updateicon.png", "Update");

            {
                button.setOnAction(event -> {
                    Nastroj nastroj = getTableView().getItems().get(getIndex());
                    otvorFormular(nastroj);
                });
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    private Button vytvorIkonoveTlacidlo(String resourcePath, String fallbackText) {
        Button button = new Button();
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream != null) {
            ImageView imageView = new ImageView(new Image(stream));
            imageView.setFitWidth(18);
            imageView.setFitHeight(18);
            imageView.setPreserveRatio(true);
            button.setGraphic(imageView);
        } else {
            button.setText(fallbackText);
            button.setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        return button;
    }

    private void otvorFormular(Nastroj povodny) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Upraviť nástroj");

        TextField tfNazov = new TextField(povodny.getNazov());
        TextField tfCena = new TextField(String.valueOf(povodny.getCena()));
        TextField tfPocet = new TextField(String.valueOf(povodny.getPocet()));
        TextField tfZvuk = new TextField(povodny.getZvuk());
        TextField tfPocetKlavesov = new TextField(
                povodny instanceof KlavesovyNastroj
                        ? String.valueOf(((KlavesovyNastroj) povodny).getPocetKlavesov())
                        : ""
        );
        TextField tfPocetZvukov = new TextField(
                povodny instanceof RytmickyNastroj
                        ? String.valueOf(((RytmickyNastroj) povodny).getPocetZvukov())
                        : ""
        );

        Button btnUlozit = new Button("Uložiť");
        btnUlozit.setOnAction(e -> {
            String nazov = tfNazov.getText();
            double cena = Double.parseDouble(tfCena.getText());
            int pocet = Integer.parseInt(tfPocet.getText());
            String zvuk = tfZvuk.getText();
            int pocetKlavesov = tfPocetKlavesov.getText().isBlank() ? 0 : Integer.parseInt(tfPocetKlavesov.getText());
            int pocetZvukov = tfPocetZvukov.getText().isBlank() ? 0 : Integer.parseInt(tfPocetZvukov.getText());

            Nastroj upraveny = pocetKlavesov > 0
                    ? new KlavesovyNastroj(nazov, cena, zvuk, pocet, pocetKlavesov)
                    : new RytmickyNastroj(nazov, cena, zvuk, pocet, pocetZvukov);

            int index = nastroje.indexOf(povodny);
            if (index >= 0) {
                nastroje.set(index, upraveny);
                table.refresh();
            }
            modal.close();
        });

        VBox layout = new VBox(10,
                new Label("Názov:"), tfNazov,
                new Label("Cena:"), tfCena,
                new Label("Počet:"), tfPocet,
                new Label("Zvuk:"), tfZvuk,
                new Label("Počet klávesov:"), tfPocetKlavesov,
                new Label("Počet zvukov:"), tfPocetZvukov,
                new HBox(btnUlozit)
        );
        ((HBox) layout.getChildren().get(layout.getChildren().size() - 1)).setAlignment(Pos.CENTER_RIGHT);
        layout.setStyle("-fx-padding: 10;");

        modal.setScene(new Scene(layout));
        modal.showAndWait();
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
    protected void uloz() {
        try {
            Path path = Path.of("src", "main", "resources", "orchester.txt");
            StringBuilder obsah = new StringBuilder();

            for (Nastroj nastroj : nastroje) {
                if (nastroj instanceof RytmickyNastroj rytmickyNastroj) {
                    obsah.append("r,")
                            .append(rytmickyNastroj.getNazov()).append(",")
                            .append(rytmickyNastroj.getCena()).append(",")
                            .append(rytmickyNastroj.getZvuk()).append(",")
                            .append(rytmickyNastroj.getPocet()).append(",")
                            .append(rytmickyNastroj.getPocetZvukov());
                } else if (nastroj instanceof KlavesovyNastroj klavesovyNastroj) {
                    obsah.append("k,")
                            .append(klavesovyNastroj.getNazov()).append(",")
                            .append(klavesovyNastroj.getCena()).append(",")
                            .append(klavesovyNastroj.getZvuk()).append(",")
                            .append(klavesovyNastroj.getPocet()).append(",")
                            .append(klavesovyNastroj.getPocetKlavesov());
                }
                obsah.append(System.lineSeparator());
            }

            Files.writeString(path, obsah.toString(), StandardCharsets.UTF_8);
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
