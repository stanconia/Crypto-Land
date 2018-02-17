package org.personal.crypto_watcher.db.mongodb;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.db.DBInterface;
import org.personal.crypto_watcher.db.mongodb.trade.TradeDB;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.db.mongodb.currency_stats.CurrencyMetricsDB;
import org.personal.crypto_watcher.model.MarketCap;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.db.model.mongodb.*;
import org.personal.crypto_watcher.model.BestPerformers;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MongoDBInterface implements DBInterface {

    private static MongoClient client;
    private static Jongo jongo;
    private static com.google.common.cache.LoadingCache<String, Tradable> cache;

    //private static Map<String, List<CurrencyStat>> currStatCache;

    /*static{
        client = getMongoClient();
        jongo = new Jongo(client.getDB("test"));

        cache = CacheBuilder.newBuilder()
        .expireAfterWrite(25, TimeUnit.HOURS)
                .build(new CacheLoader<String, Tradable>() {
                    public Tradable load(String key) {
                        return getTradable(key);
                    }
                });

        CurrencyMetricsDB.init(client);
        TradeDB.init(client);
    }*/

    public MongoDBInterface(){

        client = getMongoClient();
        jongo = new Jongo(client.getDB("test"));

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(25, TimeUnit.HOURS)
                .build(new CacheLoader<String, Tradable>() {
                    public Tradable load(String key) {
                        return getTradable(key);
                    }
                });

        CurrencyMetricsDB.init(client);
        TradeDB.init(client);
    }


    private static  MongoClient getMongoClient() {

        String env = System.getProperty("env");
        if(env == null){
            MongoClientURI uri = new MongoClientURI("mongodb://stanwizzy:ab34567%40@crypto-shard-00-00-xcx7b.mongodb.net:27017," +
                    "crypto-shard-00-01-xcx7b.mongodb.net:27017," +
                    "crypto-shard-00-02-xcx7b.mongodb.net:27017/test?ssl=true&replicaSet=Crypto-shard-0&authSource=admin");
            return new MongoClient(uri);
        }else{
            return new MongoClient();
        }
    }


    public  void persistTradables(List<Tradable> bittrexTradables, String time) {

        MongoCollection tradables = jongo.getCollection("Tradables");
        List<String> symbols = new ArrayList<>();
        MongoTime mongoTime = new MongoTime();
        mongoTime.setTime(time);

        bittrexTradables.stream().forEach(dynamoTradable ->{
                String symbol = dynamoTradable.getMarketName()+ "_" + time;
                symbols.add(symbol);
                tradables.insert(dynamoTradable);
                cache.asMap().put(symbol,dynamoTradable);});
        mongoTime.setChildren(symbols);
        tradables.insert(mongoTime);
    }

    public  List<Tradable> getTradables(String time){

        MongoCollection tradables = jongo.getCollection("Tradables");
        MongoTime mongoTime = tradables.findOne("{_id:'" + time + "'}").as(MongoTime.class);
        if(mongoTime == null){
            return new ArrayList<>();
        }else{
            List<String> symbols = mongoTime.getChildren();
            return symbols.stream().map(symbol -> {
                try {
                    return cache.get(symbol);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }).collect(Collectors.toList());
        }
    }



    public  Tradable getTradable(String symbol, String time) {

        MongoCollection tradables = jongo.getCollection("Tradables");
        String timeSymbol = symbol + "_" + time;
        return tradables.findOne("{_id : '" + timeSymbol + "'}").as(Tradable.class);
    }

    public  Tradable getTradable(String key) {

        MongoCollection tradables = jongo.getCollection("Tradables");
        return tradables.findOne("{_id : '" + key + "'}").as(Tradable.class);
    }

    public  void persistCurrStat(List<CurrencyStat> currStats, String time, String window) {

        CurrencyMetricsDB.persistCurrStat(currStats,time,window);

    }

    public  CurrencyStat getCurrStat(String symbol, String id) {

        return CurrencyMetricsDB.getCurrStat(symbol,id);

    }

    public  String getLatestStableCurrIDs(String window){

        return CurrencyMetricsDB.getStableCurrTimePointerIDs(window);

    }

    public  List<CurrencyStat> getStableCurr(String id, String window){

        return CurrencyMetricsDB.getStableCurrs(id,window);

    }

    public  void updateLatestID(String id,String window){

        CurrencyMetricsDB.updateStableCurrTimePointerIDs(id,window);

    }

    public  void persistStableCurr(List<CurrencyStat> stableCurrs, String time,String window) {

        CurrencyMetricsDB.persistStableCurrs(stableCurrs,time,window);
    }


    public  Map<String,Double> getNumSellMeans(String window){

        return CurrencyMetricsDB.getNumSellMeans(window);
    }


    public  void persistNumSellMean(Map<String,Double> numSellMean,String window){

        CurrencyMetricsDB.persistNumSellMean(numSellMean,window);
    }

    public  void persistCoinAlert(CurrencyAlert currAlert) {

        TradeDB.persistCoinAlert(currAlert);
    }

    public  CurrencyAlert getCoinAlert(String symbol) {

        return TradeDB.getCoinAlert(symbol);
    }

    public  void persistCoinProp(CoinProp coinProp) {

       TradeDB.persistCoinProp(coinProp);
    }

    public  CoinProp getCoinProp(String symbol) {

       return TradeDB.getCoinProp(symbol);
    }

    @Override
    public MarketCap getMarketCap(String time) {
        return null;
    }

    @Override
    public void putMarketCap(MarketCap marketCap) {

    }
}
