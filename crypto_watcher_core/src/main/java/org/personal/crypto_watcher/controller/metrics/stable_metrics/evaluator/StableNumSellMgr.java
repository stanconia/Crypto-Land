package org.personal.crypto_watcher.controller.metrics.stable_metrics.evaluator;

import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.Tradable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StableNumSellMgr {

    public List<CurrencyStat> getStableCurr(String window, List<Tradable> tradables) {

        List<String> ids = StablePriceMgr.getLatestRecordKeys(window);
        if(!ids.isEmpty()){
            String id  = ids.get(0);
            return tradables.stream()
                    .filter(tradable -> tradable.getMarketName().startsWith("USDT"))
                    .map(tradable -> DBMgr.getDBMgr().getInterface().getCurrStat(tradable.getMarketName(),id))
                    .filter( currStat -> currStat != null && currStat.getNumBuyWidth() < 0.07 )
                    .collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
    }
}
