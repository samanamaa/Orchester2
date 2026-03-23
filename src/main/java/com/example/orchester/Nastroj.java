package com.example.orchester;

public class Nastroj {
    protected String druh;
    protected double cena;
    protected int pocet;
    protected String zvuk;

    public Nastroj(String druh,double cena,int pocet,String zvuk){
        this.druh=druh;
        this.cena=cena;
        this.pocet=pocet;
        this.zvuk=zvuk;
    }

    public String getDruh(){return druh;}
    public double getCena(){return cena;}
    public int getPocet(){return pocet;}
    public String getZvuk(){return zvuk;}
}
