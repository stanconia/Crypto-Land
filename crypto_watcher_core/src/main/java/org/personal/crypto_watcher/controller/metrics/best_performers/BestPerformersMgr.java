package org.personal.crypto_watcher.controller.metrics.best_performers;


import org.apache.log4j.Logger;
import org.personal.crypto_watcher.controller.alerts.TelegramAlert;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.TimeMgr;
import org.personal.crypto_watcher.controller.data_pipeline.bittrex.TradableConverter;
import org.personal.crypto_watcher.model.BestPerformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BestPerformersMgr {

    private static Logger  logger = Logger.getLogger(BestPerformersMgr.class);


    public static void persistBestPerformers(List<Tradable> tradables){

        try {
            BestPerformers bestPerformers = new BestPerformers();
            bestPerformers.setTime(TimeMgr.getTimeStr());
            Map<String, BestPerformers.PerformanceInterval> pIntervals = getAndStoreBestMetrics(tradables);
            bestPerformers.setInterval(pIntervals);
            alert(pIntervals, tradables);
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private static void alert(Map<String, BestPerformers.PerformanceInterval> pIntervals,List<Tradable> tradables) {

        List<String> intervals = TradableConverter.INTERVALS;
        List<String> seenTradables = new ArrayList<>();
        for(String interval : intervals){

            BestPerformers.PerformanceInterval performance = pIntervals.get(interval);
            Map<String, Double> volMap = tradables.stream().collect(
                    Collectors.toMap(Tradable::getMarketName, Tradable::getVolume));
            alert(performance.getMetricMap().get("top_avg_gains"),seenTradables,interval,volMap);
            alert(performance.getMetricMap().get("top_price_gains"),seenTradables,interval,volMap);
            alert(performance.getMetricMap().get("top_vol_gains"),seenTradables,interval,volMap);
        }
    }

    private static void alert(List<GeneralCurrencyStat> stats,
                              List<String> seenTradables,
                              String interval,
                              Map<String, Double> volMap){

        for(GeneralCurrencyStat stat: stats){
            double vol = volMap.get(stat.getSymbol());
            if(stat.getPriceVel() > 3 && stat.getValue() != 100
                    && stat.getVolVel() > 5
                    && !seenTradables.contains(stat.getSymbol())){
                seenTradables.add(stat.getSymbol());
                TelegramAlert.notify( stat.getSymbol() + "%20over%20the%20last%20" + interval +
                        ",%20had%20the%20following%20gains(%25);%20%20pr:" + stat.getPriceVel() + "%20vol:" + stat.getVolVel()
                        + "%20num_buys:%20" + stat.getNumBuyVel() + "%20num_sell:%20" + stat.getNumSellVel()
                        + "%20vol:%20" + vol,
                        TelegramAlert.CHAT_ID);
            }
        }
    }

    private static Map<String,BestPerformers.PerformanceInterval> getAndStoreBestMetrics(List<Tradable> tradables) {

        Map<String,BestPerformers.PerformanceInterval> bestMetricStore = new HashMap<>();

        getAndStoreBestMetrics(TradableConverter.ONE_MIN_KEY, tradables,bestMetricStore);
        getAndStoreBestMetrics(TradableConverter.FIVE_MIN_KEY,applyConstraint(tradables),bestMetricStore);
        getAndStoreBestMetrics(TradableConverter.TEN_MIN_KEY,applyConstraint(tradables),bestMetricStore);
        getAndStoreBestMetrics(TradableConverter.THIRTY_MIN_KEY, tradables,bestMetricStore);
        //getAndStoreBestMetrics(TradableConverter.ONE_HR_KEY,tradables,bestMetricStore);
        //getAndStoreBestMetrics(TradableConverter.ONE_DAY_KEY,tradables,bestMetricStore);
        return bestMetricStore;
    }

    private static void getAndStoreBestMetrics(String key, List<Tradable> tradables, Map<String,BestPerformers.PerformanceInterval> bestMetricStore) {

        TopCoinsExecutor topCoinsExecutor = new TopCoinsExecutor();
        List<GeneralCurrencyStat> generalCurrencyStats = tradables.stream()
                .filter(dynamoTradable -> dynamoTradable.getVolume() > 20)
                .map(dynamoTradable -> new GeneralCurrencyStat(dynamoTradable.getMarketName(),
                dynamoTradable.getMetricMap().get(key).getPriceVelocity(),
                dynamoTradable.getMetricMap().get(key).getVolumeVelocity(),
                dynamoTradable.getMetricMap().get(key).getNumBuyOrdersVelocity(),
                dynamoTradable.getMetricMap().get(key).getNumSellOrdersVelocity()
        )).collect(Collectors.toList());
        topCoinsExecutor.execute(generalCurrencyStats);
        Map<String,List<GeneralCurrencyStat>> bestMetrics = topCoinsExecutor.report();
        bestMetricStore.put(key,new BestPerformers.PerformanceInterval(key, bestMetrics));
    }

    private static List<Tradable> applyConstraint(List<Tradable> tradables){

        return tradables.stream().filter(dynamoTradable ->
            dynamoTradable.getMetricMap().get(TradableConverter.ONE_MIN_KEY).getPriceVelocity() > 0
        ).collect(Collectors.toList());
    }
}