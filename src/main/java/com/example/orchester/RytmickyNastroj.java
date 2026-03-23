package com.example.orchester;

public class RytmickyNastroj extends Nastroj{
    int pocetZvukov;
    public RytmickyNastroj(String druh,double cena,int pocet,int zvuky){
        super(druh,cena,pocet,"bum");
        this.pocetZvukov=zvuky;
    }
}
