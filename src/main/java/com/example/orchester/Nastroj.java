package com.example.orchester;

public class Nastroj {
    protected String nazov;
    protected double cena;
    protected int pocet;
    protected String zvuk;

    public Nastroj(String nazov,double cena,int pocet,String zvuk){
        this.nazov=nazov;
        this.cena=cena;
        this.pocet=pocet;
        this.zvuk=zvuk;
    }

    public String getNazov(){return nazov;}
    public double getCena(){return cena;}
    public int getPocet(){return pocet;}
    public String getZvuk(){return zvuk;}
}
