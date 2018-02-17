package org.personal.crypto_watcher.controller.trade.watch;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.personal.crypto_watcher.controller.alerts.TelegramAlert;
import org.personal.crypto_watcher.controller.trade.TradeMgr;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.CoinPropMgr;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WatchMgr {

    private static com.google.common.cache.LoadingCache<String, CurrencyAlert> watchCache;
    private static CoinPropMgr coinPropMgr = CoinPropMgr.getCoinPropMgr();

    static {
        watchCache = CacheBuilder.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .build(new CacheLoader<String, CurrencyAlert>() {
                    public CurrencyAlert load(String key) {
                        return coinPropMgr.getCoinAlert(key);
                    }
                });

    }

    public static void add(String symbol, String alertKey, double alertValue, String alertType) {

        add(symbol,alertKey,alertValue,alertType,true);
    }

    public static void add(String symbol, String alertKey, double alertValue,
                           String alertType,boolean canOverride) {


        CurrencyAlert currencyAlert = watchCache.getIfPresent(symbol);
        if(currencyAlert == null){
            currencyAlert = new CurrencyAlert(symbol);
            watchCache.put(symbol,currencyAlert);
        }
        currencyAlert.add(alertKey,alertValue,alertType,canOverride);
        coinPropMgr.persistCoinAlert(currencyAlert);
    }

    public static  Map<String,CurrencyAlert> getAlerts(){
         return watchCache.asMap();
    }

    public static List<String> getCurrUnderWatch(){

        return watchCache.asMap().keySet().stream().collect(Collectors.toList());}

    public static boolean watch(Tradable currentTradable){

        String symbol = currentTradable.getMarketName();
        CurrencyAlert currencyAlert = watchCache.getIfPresent(symbol);
        if(currencyAlert != null){
            boolean watchStatus = currencyAlert.hasMetCondition(currentTradable);
            if(watchStatus){
                watchCache.invalidate(symbol);
                TelegramAlert.notify( "Alert!%0A" + symbol,TelegramAlert.TRADE_EVT_ID );
                return true;
            }
        }
        return false;
    }

    public static void clear() {

        watchCache.invalidateAll();
    }

    public static void remove(String symbol, String alertKey) {


        CurrencyAlert currencyAlert = watchCache.getIfPresent(symbol);
        if(currencyAlert != null){
            currencyAlert.removeMetric(alertKey);
        }
    }
}