package org.personal.crypto_watcher.controller.trade.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinProp {


    @MongoId
    @JsonProperty("_id")
    private String symbol;

    private double maxNumBuy;
    private double maxNumSell;
    private String currencyType;
    private double cash;
    private double moonThresh;
    private Map<String,SellRule> sellRules;
    private  double lossThresh;

    public static final String USDT_CURRENCY = "USDT";
    public static final String BTC_CURRENCY = "BTC";



    public CoinProp(String symbol){

        this.symbol = symbol;
        this.cash = 0.01;
        this.currencyType = BTC_CURRENCY;
        this.moonThresh = 0.15;
        this.lossThresh = 0.1;
        this.sellRules = new HashMap<>();
        sellRules.put(SellRule.LOSS,new SellRule(0.1,300,200) );
        sellRules.put(SellRule.PROFIT,new SellRule(0.1,150,200) );
        sellRules.put(SellRule.MOON,new SellRule(0.15,100,200) );
    }

    public String getSymbol() {
        return symbol;
    }


    public double getMaxNumBuy() {
        return maxNumBuy;
    }

    public void setMaxNumBuy(double maxNumBuy) {
        this.maxNumBuy = maxNumBuy;
    }

    public double getMaxNumSell() {
        return maxNumSell;
    }

    public void setMaxNumSell(double maxNumSell) {
        this.maxNumSell = maxNumSell;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getMoonThresh() {
        return moonThresh;
    }

    public void setMoonThresh(double moonThresh) {
        this.moonThresh = moonThresh;
    }


    public SellRule getRule(String ruleType){
        return sellRules.get(ruleType);
    }


    public Map<String, SellRule> getSellRules() {
        return sellRules;
    }

    public void setSellRules(Map<String, SellRule> sellRules) {
        this.sellRules = sellRules;
    }

    public double getLossThresh() {
        return lossThresh;
    }

    public void setLossThresh(double thresh){
        this.lossThresh = thresh;
    }
}
