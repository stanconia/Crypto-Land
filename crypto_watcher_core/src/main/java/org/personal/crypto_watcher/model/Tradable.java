package org.personal.crypto_watcher.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;
import org.personal.crypto_watcher.controller.data_pipeline.bittrex.TradableConverter;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Tradable {

    @JsonProperty("symbol")
    private String marketName;

    private double price;
    private double volume;

    @JsonProperty("num_buys")
    private int numBuyOrders;

    @JsonProperty("num_sells")
    private int numSellOrders;

    @JsonProperty("metrics")
    private Map<String,Metrics> metricMap;


    @MongoId
    @JsonProperty("_id")
    private String id;

    public Tradable(RawTradable rawTradable, String time){

        this.marketName = rawTradable.getMarketName();
        this.price = rawTradable.getPrice();
        this.volume = rawTradable.getVolume();
        this.numBuyOrders = rawTradable.getNumBuyOrders();
        this.numSellOrders = rawTradable.getNumSellOrders();
        this.metricMap = new HashMap<>();
        this.id = this.marketName + "_" + time;

    }

    public Tradable(){
        this.metricMap = new HashMap<>();
        metricMap.put(TradableConverter.ONE_MIN_KEY,new Metrics());
        metricMap.put(TradableConverter.FIVE_MIN_KEY,new Metrics());
        metricMap.put(TradableConverter.TEN_MIN_KEY,new Metrics());
        metricMap.put(TradableConverter.THIRTY_MIN_KEY,new Metrics());
        metricMap.put(TradableConverter.ONE_HR_KEY,new Metrics());
        metricMap.put(TradableConverter.ONE_DAY_KEY,new Metrics());

    }



    public String getMarketName() {
        return marketName;
    }
    public void setMarketName(String marketName) {this.marketName = marketName;}


    public String getId() {
        return id;
    }
    public void setId(String id) {this.id = id;}

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {this.price = price;}

    public double getVolume() {
        return volume;
    }
    public void setVolume(double volume) {this.volume = volume;}

    public int getNumBuyOrders() {
        return numBuyOrders;
    }
    public void setNumBuyOrders(int numBuyOrders) {this.numBuyOrders = numBuyOrders;}

    public int getNumSellOrders() {
        return numSellOrders;
    }
    public void setNumSellOrders(int numSellOrders) {this.numSellOrders = numSellOrders;}

    public Map<String,Metrics> getMetricMap() {
        return metricMap;
    }
    public void setMetricMap(Map<String,Metrics> metricMap) {
        this.metricMap = metricMap;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metrics{

        @JsonProperty("price_vel")
        private double priceVelocity;

        @JsonProperty("volume_vel")
        private double volumeVelocity;

        @JsonProperty("num_buy_vel")
        private double numBuyOrdersVelocity;

        @JsonProperty("num_sell_vel")
        private double numSellOrdersVelocity;

        @JsonProperty("price_aclr")
        private double priceAcceleration;

        @JsonProperty("volume_aclr")
        private double volumeAcceleration;

        @JsonProperty("num_buy_aclr")
        private double numBuyOrdersAcceleration;

        @JsonProperty("num_sell_aclr")
        private double numSellOrdersAcceleration;



        public double getPriceVelocity() {
            return priceVelocity;
        }
        public void setPriceVelocity(double priceVelocity) {
            this.priceVelocity = priceVelocity;
        }

        public double getVolumeVelocity() {
            return volumeVelocity;
        }
        public void setVolumeVelocity(double volumeVelocity) {
            this.volumeVelocity = volumeVelocity;
        }

        public double getNumBuyOrdersVelocity() {
            return numBuyOrdersVelocity;
        }
        public void setNumBuyOrdersVelocity(double numBuyOrdersVelocity) {this.numBuyOrdersVelocity = numBuyOrdersVelocity;}

        public double getNumSellOrdersVelocity() {
            return numSellOrdersVelocity;
        }
        public void setNumSellOrdersVelocity(double numSellOrdersVelocity) {this.numSellOrdersVelocity = numSellOrdersVelocity;}

        public double getPriceAcceleration() {
            return priceAcceleration;
        }
        public void setPriceAcceleration(double priceAcceleration) {
            this.priceAcceleration = priceAcceleration;
        }

        public double getVolumeAcceleration() {
            return volumeAcceleration;
        }
        public void setVolumeAcceleration(double volumeAcceleration) {
            this.volumeAcceleration = volumeAcceleration;
        }

        public double getNumBuyOrdersAcceleration() {
            return numBuyOrdersAcceleration;
        }
        public void setNumBuyOrdersAcceleration(double numBuyOrdersAcceleration) {this.numBuyOrdersAcceleration = numBuyOrdersAcceleration;}

        public double getNumSellOrdersAcceleration() {
            return numSellOrdersAcceleration;
        }
        public void setNumSellOrdersAcceleration(double numSellOrdersAcceleration) {this.numSellOrdersAcceleration = numSellOrdersAcceleration;}
    }

}
