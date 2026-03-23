package com.example.orchester;

public class Nastroj {
    protected String nazov;
    protected double cena;
    protected int pocet;
    protected String zvuk;

    public Nastroj(String nazov, double cena, String zvuk, int pocet) {
        this.nazov = nazov;
        this.cena = cena;
        this.pocet = pocet;
        this.zvuk = zvuk;
    }

    public String getNazov() {
        return nazov;
    }

    public double getCena() {
        return cena;
    }

    public int getPocet() {
        return pocet;
    }

    public String getZvuk() {
        return zvuk;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public void setPocet(int pocet) {
        this.pocet = pocet;
    }

    public void setZvuk(String zvuk) {
        this.zvuk = zvuk;
    }
    @Override
    public String toString() {
        return "Názov: " + getNazov() +
                " | Cena: " + getCena() +
                " | Počet: " + getPocet() +
                " | Zvuk: " + getZvuk();
    }
}