package org.personal.crypto_watcher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jongo.marshall.jackson.oid.MongoId;

import javax.persistence.Column;
import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrCap {

    @MongoId
    @JsonProperty("_id")
    private String time;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("market_cap_usd")
    private double marketCap;

    @JsonProperty("24h_volume_usd")
    private double volume;

    @JsonProperty("price_usd")
    private double price;

    public CurrCap(){

    }

    public CurrCap(String input){

        ObjectMapper mapper = new ObjectMapper();
        try {
            CurrCap cap = mapper.readValue(input,CurrCap.class);
            this.symbol = cap.getSymbol();
            this.price = cap.getPrice();
            this.volume = cap.getVolume();
            this.marketCap = cap.getMarketCap();
            this.time = cap.getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }


    public void setTime(String time) {

        this.time = time;
    }
}
