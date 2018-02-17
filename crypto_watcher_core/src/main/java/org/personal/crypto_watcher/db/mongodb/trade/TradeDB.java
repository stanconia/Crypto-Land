package org.personal.crypto_watcher.db.mongodb.trade;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.db.model.mongodb.CoinAlertHolder;
import org.personal.crypto_watcher.db.model.mongodb.CoinPropHolder;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;

public class TradeDB {

    private static Jongo tradeDB;


    public static void init(MongoClient client) {

        tradeDB = new Jongo(client.getDB("trade"));
    }

    public static void persistCoinAlert(CurrencyAlert currAlert) {

        MongoCollection coinAlerts = tradeDB.getCollection("CoinAlerts");
        coinAlerts.update("{_id: '" +currAlert.getSymbol()  +"'}")
                .upsert()
                .with("{$set: {value: #}}", currAlert);
    }

    public static CurrencyAlert getCoinAlert(String symbol) {

        MongoCollection coinAlerts = tradeDB.getCollection("CoinAlerts");
        CoinAlertHolder coinAlertHolder = coinAlerts.findOne("{_id: '" +symbol  +"'}")
                .as(CoinAlertHolder.class);
        return (coinAlertHolder != null ) ? coinAlertHolder.getValue() : null;
    }

    public static void persistCoinProp(CoinProp coinProp) {

        MongoCollection coinPropCollection = tradeDB.getCollection("CoinProps");
        coinPropCollection.update("{_id: '" +coinProp.getSymbol()  +"'}")
                .upsert()
                .with("{$set: {value: #}}", coinProp);
    }

    public static CoinProp getCoinProp(String symbol) {

        MongoCollection coinPropCollection = tradeDB.getCollection("CoinProps");
        CoinPropHolder coinPropHolder = coinPropCollection.findOne("{_id: '" +symbol  +"'}")
                .as(CoinPropHolder.class);
        return (coinPropHolder != null ) ? coinPropHolder.getValue() : null;
    }



}
