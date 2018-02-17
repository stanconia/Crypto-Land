package org.personal.crypto_watcher.controller.metrics.trend_analyser;

public class CurrState {

    private String symbol;
    private String state;
    private String volTrend ;
    private String priceTrend ;
    private String numBuyTrend ;
    private String numSellTrend ;

    public static final String INCREASING_TREND = "incr";
    public static final String DECREASING_TREND = "decr";
    public static final String CONSTANT_TREND = "const";
    public static final String GOOD_STATE = "good";
    public static final String BAD_STATE = "good";
    public static final String NEUTRAL_STATE = "neutral";

    public CurrState(String symbol) {
        this.symbol = symbol;
        this.state = NEUTRAL_STATE;
        this.volTrend = CONSTANT_TREND;
        this.priceTrend = CONSTANT_TREND;
        this.numBuyTrend = CONSTANT_TREND;
        this.numSellTrend = CONSTANT_TREND;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVolTrend() {
        return volTrend;
    }

    public void setVolTrend(String volTrend) {
        this.volTrend = volTrend;
    }

    public String getPriceTrend() {
        return priceTrend;
    }

    public void setPriceTrend(String priceTrend) {
        this.priceTrend = priceTrend;
    }

    public String getNumBuyTrend() {
        return numBuyTrend;
    }

    public void setNumBuyTrend(String numBuyTrend) {
        this.numBuyTrend = numBuyTrend;
    }

    public String getNumSellTrend() {
        return numSellTrend;
    }

    public void setNumSellTrend(String numSellTrend) {
        this.numSellTrend = numSellTrend;
    }
}
