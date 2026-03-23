package com.example.orchester;

public class KlavesovyNastroj extends Nastroj{
    int pocetKlavesov;
    public KlavesovyNastroj(String nazov,double cena, String zvuk,int pocet, int klavesy){
        super(nazov,cena,zvuk,pocet);
        this.pocetKlavesov=klavesy;
    }
    public int getPocetKlavesov() {return pocetKlavesov;}
    @Override
    public String toString() {
        return super.toString() + " | Počet klávesov: " + getPocetKlavesov();
    }
}