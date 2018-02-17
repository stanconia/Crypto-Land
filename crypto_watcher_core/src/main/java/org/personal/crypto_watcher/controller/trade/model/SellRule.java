package org.personal.crypto_watcher.controller.trade.model;

public class SellRule {

    private double priceThresh;
    private double numBuyThresh;
    private double numSellThresh;
    private String threshType;

    public static final String PRICE_THRESH = "price";
    public static final String NUM_SELL_THRESH = "num-sell";
    public static final String NUM_BUY_THRESH = "num-buy";
    public static final String LOSS = "loss";
    public static final String PROFIT= "profit";
    public static final String MOON = "moon";

    public SellRule(double priceThresh, double numBuyThresh, double numSellThresh) {
        this.priceThresh = priceThresh;
        this.numBuyThresh = numBuyThresh;
        this.numSellThresh = numSellThresh;
        this.threshType = PRICE_THRESH;
    }

    public double getPriceThresh() {
        return priceThresh;
    }

    public double getNumBuyThresh() {
        return numBuyThresh;
    }

    public double getNumSellThresh() {
        return numSellThresh;
    }

    public String getThreshType() {
        return threshType;
    }

    public void setThreshType(String threshType) {
        this.threshType = threshType;
    }
}
