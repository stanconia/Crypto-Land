package org.personal.crypto_watcher.util;

import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;

public class CoinPropMgr {

    private static CoinPropMgr coinPropMgr;

    private  CoinPropMgr(){

    }

    public static CoinPropMgr getCoinPropMgr(){

        if(coinPropMgr == null){
            coinPropMgr = new CoinPropMgr();
        }
        return coinPropMgr;
    }


    public CoinProp getCoinProp(String symbol) {

        CoinProp coinProp = DBMgr.getDBMgr().getInterface().getCoinProp(symbol);
        return (coinProp == null) ? new CoinProp(symbol): coinProp;
    }

    public void persistCoinProp(CoinProp coinProp){

        if(coinProp != null){
            DBMgr.getDBMgr().getInterface().persistCoinProp(coinProp);
        }
    }

    public CurrencyAlert getCoinAlert(String symbol) {

        CurrencyAlert coinALert = DBMgr.getDBMgr().getInterface().getCoinAlert(symbol);
        coinALert = (coinALert == null) ? new CurrencyAlert(symbol): coinALert;
        coinALert.cleanse();
        return coinALert;
    }

    public void persistCoinAlert(CurrencyAlert coinAlert){

        if(coinAlert != null){
            DBMgr.getDBMgr().getInterface().persistCoinAlert(coinAlert);
        }
    }
}
