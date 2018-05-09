package org.personal.crypto_watcher.controller.metrics.stable_metrics.evaluator;

import org.personal.crypto_watcher.controller.scheduler.SixHrTasks;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.Tradable;

import java.util.List;
import java.util.stream.Collectors;

public class StabilityMgr {

    public static void run(List<Tradable> tradables){

        try {
            evaluatePriceStability(tradables);
            evaluateNumSellStability(tradables);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static void evaluatePriceStability(List<Tradable> tradables) {

        StablePriceMgr stablePriceMgr = new StablePriceMgr();
        List<CurrencyStat> currencyStats = stablePriceMgr.getStableCurr(SixHrTasks.WINDOW);
        currencyStats.stream().forEach(currStat -> {
            Tradable tradable = getTradableForStat(currStat, tradables);
            if(tradable != null) {
                if (currStat.getVolumeMean() < 30) {
                    StableCurrAlertMgr.checkSmallCurr(currStat, tradable);
                } else if (currStat.getVolumeMean() < 100) {
                    StableCurrAlertMgr.checkMediumCurr(currStat, tradable);
                } else if (currStat.getVolumeMean() < 1000) {
                    StableCurrAlertMgr.checkMediumCurr(currStat, tradable);
                } else {
                    StableCurrAlertMgr.checkMediumCurr(currStat, tradable);
                }
            }
        });
    }

    private static void evaluateNumSellStability(List<Tradable> tradables) {

        StableNumSellMgr stableNumSellMgr = new StableNumSellMgr();
        List<CurrencyStat> currencyStats = stableNumSellMgr.getStableCurr(SixHrTasks.WINDOW, tradables);
        currencyStats.stream().forEach(currStat -> {

            Tradable tradable = getTradableForStat(currStat, tradables);
            if(tradable != null) {
                StableCurrAlertMgr.checkSmallCurr(currStat, tradable);
            }
        });
    }

    private static Tradable getTradableForStat(CurrencyStat currencyStat, List<Tradable> tradables) {


        List<Tradable> filteredTradables = tradables.stream().filter(dynamoTradable ->{
                return dynamoTradable.getMarketName().equals(currencyStat.getSymbol());
        }).collect(Collectors.toList());
        if(filteredTradables.isEmpty()){
            return null;
        }
        return filteredTradables.get(0);
    }
}
