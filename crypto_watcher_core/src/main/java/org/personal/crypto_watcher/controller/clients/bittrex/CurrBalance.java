package org.personal.crypto_watcher.controller.clients.bittrex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrBalance {

    @JsonProperty("Currency")
    private String symbol;

    @JsonProperty("Available")
    private double availBalance;

    @JsonProperty("Balance")
    private double balance;

    private List<BittrexOrder> sellOrders;

    private List<BittrexOrder> buyOrders;
    private String market;

    public CurrBalance(String symbol, double availBalance,double balance) {
        this.symbol = symbol;
        this.availBalance = availBalance;
        this.balance = balance;
        sellOrders = new ArrayList<>();
        buyOrders = new ArrayList<>();
    }

    public  CurrBalance(){

    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setAvailBalance(double availBalance) {
        this.availBalance = availBalance;
    }

    public String getSymbol() {

        return symbol;
    }

    public double getAvailBalance() {
        return availBalance;
    }

    public void setBuyOrders(List<BittrexOrder> buyOrders) {
        this.buyOrders = buyOrders;
    }

    public void setSellOrders(List<BittrexOrder> sellOrders) {
        this.sellOrders = sellOrders;
    }

    public List<BittrexOrder> getSellOrders() {
        return sellOrders;
    }

    public List<BittrexOrder> getBuyOrders() {
        return buyOrders;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getMarket() {
        return market;
    }
}
