package org.personal.crypto_watcher.controller.metrics.best_performers;

public class GeneralCurrencyStat {

    private String symbol;
    private double priceVel;
    private double volVel;
    private double value;
    private double numBuyVel;
    private double numSellVel;



    public GeneralCurrencyStat(String symbol, double priceVel, double volVel, double numBuyVel, double numSellVel) {

        this.symbol = symbol;
        this.priceVel = priceVel;
        this.volVel = volVel;
        this.numBuyVel = numBuyVel;
        this.numSellVel = numSellVel;

        this.value = (priceVel + volVel) /2;

    }

    public GeneralCurrencyStat(){

    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {this.symbol = symbol;}

    public double getPriceVel() {
        return priceVel;
    }
    public void setPriceVel(double priceVel) {this.priceVel = priceVel;}

    public double getVolVel() {
        return volVel;
    }
    public void setVolVel(double volVel) {this.volVel = volVel;}

    public double getValue() {
        return value;
    }
    public void setValue(double value) {this.value = value;}

    public double getNumBuyVel() {
        return numBuyVel;
    }

    public void setNumBuyVel(double numBuyVel) {
        this.numBuyVel = numBuyVel;
    }

    public double getNumSellVel() {
        return numSellVel;
    }

    public void setNumSellVel(double numSellVel) {
        this.numSellVel = numSellVel;
    }

    @Override
    public String toString(){

        return priceVel + " " + volVel + " " + value;
    }
}