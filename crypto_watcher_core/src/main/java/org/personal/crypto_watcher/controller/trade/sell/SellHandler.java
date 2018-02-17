package org.personal.crypto_watcher.controller.trade.sell;

import org.personal.crypto_watcher.controller.clients.bittrex.BittrexClient;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.controller.trade.model.SellRule;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.CoinPropMgr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SellHandler {

    private  Map<String,StopSellPojo> stopSells;
    private static SellHandler sellHandler;


    private SellHandler(){
        this.stopSells = new HashMap<>();
    }

    public static SellHandler getSellHandler(){

        if(sellHandler == null){
            sellHandler = new SellHandler();
        }
        return sellHandler;
    }

    public void stopSell(String symbol, double quantity, double value) {

        stopSells.put(symbol, new StopSellPojo(symbol,quantity,value));
    }

    public void monitorStopSells(List<Tradable> tradables){

        Map<String,Tradable> tradableMap = tradables.stream()
                .collect(Collectors.toMap(Tradable::getMarketName,(tradable -> tradable)));
        stopSells.entrySet().forEach(entry -> {

            String symbol = entry.getKey();
            StopSellPojo stopSell = entry.getValue();
            Tradable tradable = tradableMap.get(symbol);
            CoinProp coinProp = CoinPropMgr.getCoinPropMgr().getCoinProp(symbol);
            if(isStopActivated(tradable,stopSell,coinProp)){
                BittrexClient.sell(symbol,stopSell.balance,tradable.getPrice() * 0.96);
            }
        });
    }

    private boolean isStopActivated(Tradable tradable, StopSellPojo stopSell, CoinProp coinProp) {

        String threshType = coinProp.getRule(SellRule.MOON).getThreshType();
        boolean result = false;
        switch (threshType){
            case "price" :
                if(tradable.getPrice() < stopSell.value) result = true;
                break;
            case "num-sell" :
                if(tradable.getNumSellOrders() > stopSell.value) result = true;
                break;
            case "num-buy" :
                if(tradable.getNumBuyOrders() < stopSell.value) result = true;
                break;
        }
        return result;
    }

    public void sell (String symbol, double quantity, double price) {

        BittrexClient.sell(symbol,quantity,price * 0.99);
    }


    private static class StopSellPojo{

        private String symbol;
        private double balance;
        private double value;

        public StopSellPojo(String symbol, double balance, double value) {
            this.symbol = symbol;
            this.balance = balance;
            this.value = value;
        }
    }
}
