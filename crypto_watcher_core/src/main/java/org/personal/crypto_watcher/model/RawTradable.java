package org.personal.crypto_watcher.model;

//{"MarketName":"BTC-1ST",
// "High":0.00008335,"Low":0.00007732,"Last":0.00008066,"Bid":0.00008066,"Ask":0.00008187,
// "Volume":861610.86281309,,"BaseVolume":69.15704460,
// "TimeStamp":"2017-09-23T19:07:10.283",
// "OpenBuyOrders":306,"OpenSellOrders":6058,
// "PrevDay":0.00008294,"Created":"2017-06-06T01:22:35.727"}

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawTradable {

    @JsonProperty("MarketName")
    private String marketName;

    @JsonProperty("Last")
    private double price;

    @JsonProperty("BaseVolume")
    private double volume;

    @JsonProperty("OpenBuyOrders")
    private int numBuyOrders;

    @JsonProperty("OpenSellOrders")
    private int numSellOrders;

    public RawTradable(){}

    public RawTradable(String symbol,double price,
                       double volume, int numBuyOrders,int numSellOrders){

        this.marketName = symbol;
        this.price = price;
        this.volume = volume;
        this.numBuyOrders = numBuyOrders;
        this.numSellOrders = numSellOrders;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public int getNumBuyOrders() {
        return numBuyOrders;
    }

    public void setNumBuyOrders(int numBuyOrders) {
        this.numBuyOrders = numBuyOrders;
    }

    public int getNumSellOrders() {
        return numSellOrders;
    }

    public void setNumSellOrders(int numSellOrders) {
        this.numSellOrders = numSellOrders;
    }

}
