package org.personal.crypto_watcher.db;

import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.model.BestPerformers;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.MarketCap;
import org.personal.crypto_watcher.model.Tradable;

import java.util.List;
import java.util.Map;

public interface DBInterface {


    public  void persistTradables(List<Tradable> bittrexTradables, String time);

    public List<Tradable> getTradables(String time);

    public Tradable getTradable(String symbol, String time);

    public  Tradable getTradable(String key) ;

    public  void persistCurrStat(List<CurrencyStat> currStats, String time, String window) ;

    public  CurrencyStat getCurrStat(String symbol, String id) ;

    public  String getLatestStableCurrIDs(String window);

    public  List<CurrencyStat> getStableCurr(String id, String window);

    public  void updateLatestID(String id,String window);

    public  void persistStableCurr(List<CurrencyStat> stableCurrs, String time,String window) ;


    public Map<String,Double> getNumSellMeans(String window);


    public  void persistNumSellMean(Map<String,Double> numSellMean,String window);

    public  void persistCoinAlert(CurrencyAlert currAlert) ;

    public CurrencyAlert getCoinAlert(String symbol) ;

    public  void persistCoinProp(CoinProp coinProp) ;

    public  CoinProp getCoinProp(String symbol) ;

    public MarketCap getMarketCap(String time);

    public void putMarketCap(MarketCap marketCap);
}
