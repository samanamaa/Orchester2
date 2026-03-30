package com.example.orchester;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final ObservableList<Nastroj> nastroje = FXCollections.observableArrayList();
    private final String CESTA_K_RESOURCES = "src/main/resources/orchester.txt";

    @FXML private TableView<Nastroj> table;
    @FXML private TableColumn<Nastroj, String> colNazov;
    @FXML private TableColumn<Nastroj, Double> colCena;
    @FXML private TableColumn<Nastroj, Integer> colPocet;
    @FXML private TableColumn<Nastroj, String> colZvuk;
    @FXML private TableColumn<Nastroj, Integer> colPocetKlavesov;
    @FXML private TableColumn<Nastroj, Integer> colPocetZvukov;
    @FXML private TableColumn<Nastroj, Void> colAkcie;
    @FXML private TableColumn<Nastroj, Void> colAkcie1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNazov.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNazov()));
        colCena.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCena()));
        colPocet.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPocet()));
        colZvuk.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getZvuk()));

        colPocetKlavesov.setCellValueFactory(c -> {
            if (c.getValue() instanceof KlavesovyNastroj k) return new SimpleObjectProperty<>(k.getPocetKlavesov());
            return null;
        });

        colPocetZvukov.setCellValueFactory(c -> {
            if (c.getValue() instanceof RytmickyNastroj r) return new SimpleObjectProperty<>(r.getPocetZvukov());
            return null;
        });

        table.setItems(nastroje);
        nastavAkcneStlpce();
        nacitaj();
    }

    private void nastavAkcneStlpce() {
        colAkcie.setCellFactory(param -> new ButtonCell("Delete", "/deleteicon.png", nastroje::remove));
        colAkcie1.setCellFactory(param -> new ButtonCell("Edit", "/updateicon.png", this::otvorFormular));
    }

    private void otvorFormular(Nastroj povodny) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle(povodny == null ? "Pridať nástroj" : "Upraviť nástroj");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField tfNazov = new TextField(povodny != null ? povodny.getNazov() : "");
        TextField tfCena = new TextField(povodny != null ? String.valueOf(povodny.getCena()) : "");
        TextField tfPocet = new TextField(povodny != null ? String.valueOf(povodny.getPocet()) : "");
        TextField tfZvuk = new TextField(povodny != null ? povodny.getZvuk() : "");

        ComboBox<String> cbTyp = new ComboBox<>(FXCollections.observableArrayList("Klávesový", "Rytmický"));
        TextField tfSpecialny = new TextField();
        Label lblSpecialny = new Label();

        if (povodny instanceof KlavesovyNastroj k) {
            cbTyp.setValue("Klávesový");
            lblSpecialny.setText("Počet klávesov:");
            tfSpecialny.setText(String.valueOf(k.getPocetKlavesov()));
        } else if (povodny instanceof RytmickyNastroj r) {
            cbTyp.setValue("Rytmický");
            lblSpecialny.setText("Počet zvukov:");
            tfSpecialny.setText(String.valueOf(r.getPocetZvukov()));
        } else {
            cbTyp.setValue("Klávesový");
            lblSpecialny.setText("Počet klávesov:");
        }

        cbTyp.setOnAction(e -> lblSpecialny.setText(cbTyp.getValue().equals("Klávesový") ? "Počet klávesov:" : "Počet zvukov:"));

        grid.addRow(0, new Label("Názov:"), tfNazov);
        grid.addRow(1, new Label("Cena (€):"), tfCena);
        grid.addRow(2, new Label("Počet kusov:"), tfPocet);
        grid.addRow(3, new Label("Zvuk:"), tfZvuk);
        grid.addRow(4, new Label("Typ nástroja:"), cbTyp);
        grid.addRow(5, lblSpecialny, tfSpecialny);

        Button btnUlozit = new Button("Uložiť");
        btnUlozit.setOnAction(e -> {
            try {
                String nazov = tfNazov.getText();
                double cena = Double.parseDouble(tfCena.getText());
                int pocet = Integer.parseInt(tfPocet.getText());
                String zvuk = tfZvuk.getText();
                int spec = Integer.parseInt(tfSpecialny.getText());

                Nastroj novy = cbTyp.getValue().equals("Klávesový")
                        ? new KlavesovyNastroj(nazov, cena, zvuk, pocet, spec)
                        : new RytmickyNastroj(nazov, cena, zvuk, pocet, spec);

                if (povodny != null) nastroje.set(nastroje.indexOf(povodny), novy);
                else nastroje.add(novy);

                table.refresh();
                modal.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Chybné údaje!").show();
            }
        });

        VBox layout = new VBox(10, grid, btnUlozit);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        modal.setScene(new Scene(layout));
        modal.showAndWait();
    }

    @FXML
    protected void pridajNastroj() {
        otvorFormular(null);
    }

    @FXML
    protected void nacitaj() {
        try {
            File file = new File(CESTA_K_RESOURCES);
            InputStream is;

            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                is = getClass().getResourceAsStream("/orchester.txt");
            }

            if (is == null) return;

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            nastroje.clear();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) continue;
                if (data[0].equals("r")) {
                    nastroje.add(new RytmickyNastroj(data[1], Double.parseDouble(data[2]), data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5])));
                } else if (data[0].equals("k")) {
                    nastroje.add(new KlavesovyNastroj(data[1], Double.parseDouble(data[2]), data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5])));
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
            Path path = Paths.get(CESTA_K_RESOURCES);
            StringBuilder obsah = new StringBuilder();

            for (Nastroj n : nastroje) {
                if (n instanceof RytmickyNastroj r) {
                    obsah.append("r,").append(r.getNazov()).append(",").append(r.getCena()).append(",")
                            .append(r.getZvuk()).append(",").append(r.getPocet()).append(",").append(r.getPocetZvukov());
                } else if (n instanceof KlavesovyNastroj k) {
                    obsah.append("k,").append(k.getNazov()).append(",").append(k.getCena()).append(",")
                            .append(k.getZvuk()).append(",").append(k.getPocet()).append(",").append(k.getPocetKlavesov());
                }
                obsah.append(System.lineSeparator());
            }

            Files.writeString(path, obsah.toString(), StandardCharsets.UTF_8);
            new Alert(Alert.AlertType.INFORMATION, "Uložené do resources!").show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Chyba pri zápise!").show();
        }
    }

    @FXML
    protected void cenaSkladu() {
        double suma = nastroje.stream().mapToDouble(n -> n.getCena() * n.getPocet()).sum();
        new Alert(Alert.AlertType.INFORMATION, "Celková cena: " + String.format("%.2f", suma) + " €").show();
    }

    @FXML
    protected void skladHraj() {
        ListView<String> lv = new ListView<>();
        nastroje.forEach(n -> lv.getItems().add(n.getZvuk()));
        Stage stage = new Stage();
        stage.setScene(new Scene(lv, 300, 400));
        stage.setTitle("Koncert");
        stage.show();
    }

    private class ButtonCell extends TableCell<Nastroj, Void> {
        private final Button btn = new Button();
        public ButtonCell(String text, String iconPath, java.util.function.Consumer<Nastroj> action) {
            btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            try {
                InputStream is = getClass().getResourceAsStream(iconPath);
                if (is != null) {
                    ImageView iv = new ImageView(new Image(is));
                    iv.setFitHeight(16); iv.setFitWidth(16);
                    btn.setGraphic(iv);
                } else btn.setText(text);
            } catch (Exception e) { btn.setText(text); }
            btn.setOnAction(e -> action.accept(getTableView().getItems().get(getIndex())));
            setAlignment(Pos.CENTER);
        }
        @Override protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : btn);
        }
    }
}