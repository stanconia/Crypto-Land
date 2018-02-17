package org.personal.crypto_watcher.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import java.text.DecimalFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyStat {

    @MongoId
    @JsonProperty("_id")
    private String time;

    private String symbol;

    private double priceMean;
    private double numBuyMean;
    private double priceWidth;
    private double volumeMean;
    private double numSellMean;
    private double numBuyWidth;
    private double numSellWidth;


    public CurrencyStat(){

    }


    public CurrencyStat(String symbol, double priceMean, double numBuyMean,
                        double priceWidth, double numBuyWidth,
                        double volumeMean, double numSellMean, double numSellWidth) {

        this.symbol = symbol;
        this.priceMean = priceMean;
        this.numBuyMean = numBuyMean;
        this.priceWidth = Math.abs(priceWidth);
        this.numBuyWidth = numBuyWidth;
        this.numSellWidth = numSellWidth;
        this.volumeMean = volumeMean;
        this.numSellMean = numSellMean;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPriceMean() {
        return priceMean;
    }

    public double getPriceWidth() {
        return priceWidth;
    }

    public double getVolumeMean() {
        return volumeMean;
    }

    public String getTime() {
        return time;
    }


    public void setTime(String time) {

        this.time = time;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public double getNumSellMean() {
        return numSellMean;
    }


    public double getNumBuyWidth() { return numBuyWidth; }

    public double getNumSellWidth() {
        return numSellWidth;
    }

    public double getNumBuyMean() {
        return numBuyMean;
    }
}
