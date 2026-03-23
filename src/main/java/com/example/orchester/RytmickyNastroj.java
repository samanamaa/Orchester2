package com.example.orchester;

public class RytmickyNastroj extends Nastroj{
    int pocetZvukov;
    public RytmickyNastroj(String nazov,double cena,int pocet, String zvuk, int zvuky){
        super(nazov,cena,pocet,zvuk);
        this.pocetZvukov=zvuky;
    }
}
