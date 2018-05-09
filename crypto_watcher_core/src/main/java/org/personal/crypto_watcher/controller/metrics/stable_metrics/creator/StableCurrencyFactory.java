package org.personal.crypto_watcher.controller.metrics.stable_metrics.creator;

import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.util.TimeMgr;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.controller.metrics.stable_metrics.evaluator.StablePriceMgr;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StableCurrencyFactory {

    private String window;

    public StableCurrencyFactory(String window){

        this.window = window;
    }

    private List<CurrencyStat> currencyStats;

//TODO
    public void runTasks() {

        TimeMgr.init();
        String time = TimeMgr.getTimeStr();
        int interval = 12 * 60;
        currencyStats = TradableLib.getDescriptiveStat(interval);
        DBMgr.getDBMgr().getInterface().persistCurrStat(currencyStats,time,window);
/*        MongoDBInterface.persistCurr(currencyStats.stream().filter(
                stat -> stat.getSymbol().startsWith("USDT")).collect(Collectors.toList()), time,window); */ //TODO
        List<CurrencyStat> stableCurr = getStableCurr();
        StablePriceMgr stablePriceMgr = new StablePriceMgr();
        stablePriceMgr.persistStableCurr(stableCurr, time,window);
        //stablePriceMgr.persistOldPerformingStats(currencyStats,window);
    }

    private List<CurrencyStat> getStableCurr() {

        List<CurrencyStat> stableCurr = currencyStats.stream().filter(currStat -> {
            if(currStat.getVolumeMean() < 60  ){
                return currStat.getPriceWidth() < 0.03 && currStat.getPriceWidth() != 0;
            }else if (currStat.getVolumeMean() < 200 ){
                return currStat.getPriceWidth() < 0.06 && currStat.getPriceWidth() != 0;
            }
            else{
                return currStat.getPriceWidth() < 0.08 && currStat.getPriceWidth() != 0;
            }}).collect(Collectors.toList());

        stableCurr.sort(Comparator.comparingDouble(CurrencyStat::getPriceWidth));
        return stableCurr;
    }
}
