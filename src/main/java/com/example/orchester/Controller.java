package com.example.orchester;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private ObservableList<Nastroj> nastroje = FXCollections.observableArrayList();
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    @FXML
    protected void nacitaj() {
        try {
            InputStream is = getClass().getResourceAsStream("src/main/resources/orchester.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            nastroje.clear();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals("r")) {
                    String nazov = data[1];
                    double cena = Double.parseDouble(data[2]);
                    int pocet = Integer.parseInt(data[3]);
                    String zvuk = data[4];
                    int pocetZvukov = Integer.parseInt(data[5]);

                    nastroje.add(new RytmickyNastroj(nazov, cena, pocet, zvuk, pocetZvukov));
                }
                else if (data[0].equals("k")) {
                    String nazov = data[1];
                    double cena = Double.parseDouble(data[2]);
                    int pocet = Integer.parseInt(data[3]);
                    String zvuk = data[4];
                    int pocetKlavesov = Integer.parseInt(data[5]);

                    nastroje.add(new KlavesovyNastroj(nazov, cena, pocet, zvuk, pocetKlavesov));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(nastroje);
    }
}