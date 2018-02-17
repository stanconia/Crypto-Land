package org.personal.crypto_watcher.controller.metrics.stable_metrics.evaluator;

import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.CurrencyStat;

import java.util.*;

public class StablePriceMgr {

    private static final int LATEST_RECORD_SIZE = 6;
    private static final int GUARANTEED_SIZE = 3;

    public List<CurrencyStat> getStableCurr(String window){

        return new ArrayList<>(getMappedStableCurrStats(window).values());
    }

    private  Map<String, CurrencyStat> getMappedStableCurrStats(String window){

        Map<String, CurrencyStat> mappedStats = new HashMap<>();
        List<String> recordKeys = getLatestRecordKeys(window);
        int recordKeySize = Math.min(recordKeys.size(),GUARANTEED_SIZE);
        recordKeys.subList(0,recordKeySize).forEach(recordID -> {
            List<CurrencyStat> stats =  DBMgr.getDBMgr().getInterface().getStableCurr(recordID,window);
            addStatsToMap(mappedStats,stats);
        });
        return mappedStats;
    }

    private void addStatsToMap(Map<String, CurrencyStat> mappedStats, List<CurrencyStat> stats) {

        stats.stream().forEach(stat -> {
            String symbol = stat.getSymbol().split("-")[1];
            if(!mappedStats.containsKey(symbol)){
                mappedStats.put(symbol,stat);
            }
        });
    }

    public static List<String> getLatestRecordKeys(String window){

        String recordIDStr = DBMgr.getDBMgr().getInterface().getLatestStableCurrIDs(window);
        if(!recordIDStr.isEmpty()){
            String[] recordIDs = recordIDStr.split(",");
            List<String> recordKeys = Arrays.asList(recordIDs);
            recordKeys.sort(Comparator.reverseOrder());
            return recordKeys;
        }else{
            return new ArrayList<>();
        }
    }

    public void persistStableCurr(List<CurrencyStat> currencyStats, String time, String window){

        String ids = time + "_" + window;
        DBMgr.getDBMgr().getInterface().persistStableCurr(currencyStats,time,window);
        List<String> recordKeys = getLatestRecordKeys(window);
        for(int i = 0 ; i < LATEST_RECORD_SIZE -1 ; i ++){
            if(i < recordKeys.size()) {
                ids += "," + recordKeys.get(i);
            }
        }
        DBMgr.getDBMgr().getInterface().updateLatestID(ids,window);
    }
}
