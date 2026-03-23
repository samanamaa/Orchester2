package com.example.orchester;

public class RytmickyNastroj extends Nastroj{
    int pocetZvukov;
    public RytmickyNastroj(String nazov,double cena, String zvuk,int pocet, int zvuky){
        super(nazov,cena,zvuk, pocet);
        this.pocetZvukov=zvuky;
    }
    public int getPocetZvukov() {return pocetZvukov;}
    @Override
    public String toString() {
        return super.toString() + " | Počet zvukov: " + getPocetZvukov();
    }
}
