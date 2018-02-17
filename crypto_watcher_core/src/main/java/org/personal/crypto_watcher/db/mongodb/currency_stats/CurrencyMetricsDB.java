package org.personal.crypto_watcher.db.mongodb.currency_stats;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.db.model.mongodb.NumSellCollection;
import org.personal.crypto_watcher.model.BestPerformers;

import java.util.*;
import java.util.stream.Collectors;

public class CurrencyMetricsDB {

    private static Jongo currStatDB;
    private static Jongo metricsDB;
    private static Jongo tradeDB;


    public static void init(MongoClient client) {

        currStatDB = new Jongo(client.getDB("curr_stat"));
        metricsDB = new Jongo(client.getDB("metrics"));
        tradeDB = new Jongo(client.getDB("trade"));
    }

    public static void persistCurrStat(List<CurrencyStat> currStats, String time, String window) {

        for (CurrencyStat currencyStat : currStats) {
            MongoCollection currencyStats = currStatDB.getCollection(currencyStat.getSymbol());
            currencyStat.setTime(time + "_" + window);
            currencyStats.insert(currencyStat);
        }
        ;
    }

    public static CurrencyStat getCurrStat(String symbol, String id) {

        MongoCollection currencyStats = currStatDB.getCollection(symbol);
        return currencyStats.findOne("{_id : '" + id + "'}").as(CurrencyStat.class);

    }


    public static Map<String, Double> getNumSellMeans(String window) {

        MongoCollection generalStatCollection = metricsDB.getCollection("periodic_aggregates_" + window);
        NumSellCollection numSellCollection = generalStatCollection.findOne("{_id: 'num_sell_mean_" + window + "'}")
                .as(NumSellCollection.class);
        if (numSellCollection == null) {
            return new HashMap<>();
        }
        return numSellCollection.getCurr();
    }


    public static void persistNumSellMean(Map<String, Double> numSellMeanMap, String window) {

        MongoCollection generalStatCollection = metricsDB.getCollection("periodic_aggregates_" + window);
        generalStatCollection.update("{_id: 'num_sell_mean_" + window + "'}")
                .upsert()
                .with("{$set: {values: #}}", numSellMeanMap);
    }

    public static String getStableCurrTimePointerIDs(String window) {

        MongoCollection stableCurrCollection = metricsDB.getCollection("periodic_aggregates_" + window);
        Map<String, String> latestStableIDs = stableCurrCollection.findOne("{_id: 'recent_stable_currs" + "_" + window + "'}")
                .as(Map.class);
        return (latestStableIDs == null) ? "" : latestStableIDs.get("children");

    }


    public static void updateStableCurrTimePointerIDs(String recentTimeIDs, String window) {

        MongoCollection stableCurrCollection = metricsDB.getCollection("periodic_aggregates_" + window);
        stableCurrCollection.update("{_id: 'recent_stable_currs" + "_" + window + "'}")
                .upsert()
                .with("{$set: {children: #}}", recentTimeIDs);
    }

    public static void persistStableCurrs(List<CurrencyStat> stableCurrs, String time, String window) {

        MongoCollection stableCurrCollection = metricsDB.getCollection("periodic_stable_curr");
        Map<String, String> currencyIDMap = new HashMap<>();
        String id = time + "_" + window;
        currencyIDMap.put("_id", id);
        stableCurrs.forEach(stableCurr -> currencyIDMap.put(stableCurr.getSymbol(), id));
        stableCurrCollection.insert(currencyIDMap);
    }

    public static List<CurrencyStat> getStableCurrs(String id, String window) {

        MongoCollection stableCurrCollection = metricsDB.getCollection("periodic_stable_curr");
        HashMap<String, String> currencyIDMap = stableCurrCollection.findOne("{_id : '" + id + "'}").as(HashMap.class);
        return (currencyIDMap == null) ? new ArrayList<>() : getCurrStatsFromMap(currencyIDMap);
    }

    private static List<CurrencyStat> getCurrStatsFromMap(HashMap<String, String> currencyIDMap) {

        return currencyIDMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("_id"))
                .map(entry -> getCurrStat(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static void persistTopPerformers(BestPerformers bestPerformers) {

        MongoCollection performers = metricsDB.getCollection("BestPerformers");
        performers.insert(bestPerformers);
    }

    public static BestPerformers getTopPerformer(String time) {

        MongoCollection performers = metricsDB.getCollection("BestPerformers");
        return performers.findOne("{_id : '" + time + "'}").as(BestPerformers.class);
    }


}
