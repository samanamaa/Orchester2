package com.example.orchester;

public class KlavesovyNastroj extends Nastroj{
    int pocetKlavesov;
    public KlavesovyNastroj(String druh,double cena,int pocet,int klavesy){
        super(druh,cena,pocet,"ting");
        this.pocetKlavesov=klavesy;
    }
}