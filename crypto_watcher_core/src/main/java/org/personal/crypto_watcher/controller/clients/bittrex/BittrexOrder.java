package org.personal.crypto_watcher.controller.clients.bittrex;

public class BittrexOrder {

    private String market;
    private double quantity;
    private double price;
    private String uuid;
    private double lmtPrice;


    public BittrexOrder(String market, String quantity, String pricePerUnit, String uuid, String limitPrice) {

        this.market = market;
        this.quantity = (quantity == null) ? 0: Double.parseDouble(quantity) ;
        this.price = (pricePerUnit == null) ? 0: Double.parseDouble(pricePerUnit);
        this.uuid = uuid;
        this.lmtPrice = (limitPrice == null) ? 0: Double.parseDouble(limitPrice) ;

    }

    public String getMarket() {
        return market;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getUuid() {
        return uuid;
    }

    public double getLmtPrice() {
        return lmtPrice;
    }
}
