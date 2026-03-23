package com.example.orchester;

public class KlavesovyNastroj extends Nastroj{
    int pocetKlavesov;
    public KlavesovyNastroj(String nazov,double cena,int pocet, String zvuk, int klavesy){
        super(nazov,cena,pocet,zvuk);
        this.pocetKlavesov=klavesy;
    }
}