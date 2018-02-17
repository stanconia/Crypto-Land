package org.personal.crypto_watcher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrCap {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("market_cap_usd")
    private double marketCap;

    @JsonProperty("24h_volume_usd")
    private double volume;

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
}
