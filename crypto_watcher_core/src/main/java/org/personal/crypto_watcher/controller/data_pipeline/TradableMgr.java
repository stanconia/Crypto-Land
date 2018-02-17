package org.personal.crypto_watcher.controller.data_pipeline;


import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.personal.crypto_watcher.controller.data_pipeline.bittrex.TradableConverter;
import org.personal.crypto_watcher.controller.metrics.trend_analyser.TrendAnalysis;
import org.personal.crypto_watcher.controller.metrics.num_sell_analyser.BuySellIndicator;
import org.personal.crypto_watcher.controller.metrics.stable_metrics.evaluator.StabilityMgr;
import org.personal.crypto_watcher.controller.metrics.best_performers.BestPerformersMgr;
import org.personal.crypto_watcher.controller.mrkt_cap.MrktCapMgr;
import org.personal.crypto_watcher.controller.trade.TradeMgr;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.CurrCap;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.util.TimeMgr;
import org.personal.crypto_watcher.service.RestService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradableMgr {

    private static final Logger logger = Logger.getLogger(TradableMgr.class);

    private Tradable getDynamoTradable(String symbol){

        List<Tradable> tradables = DBMgr.getDBMgr().getInterface().getTradables(TimeMgr.getTimeStr());
        if(tradables.size() > 0){
            return tradables.get(0);
        }else{
            tradables = DBMgr.getDBMgr().getInterface().getTradables(TimeMgr.fmtTime(TimeMgr.getOneMinBeforeTime()));
            if(tradables.size() > 0){
                return tradables.get(0);
            }else{
                return new Tradable();
            }
        }
    }

    public void runTasks(){

        try {
            List<Tradable> tradables = getDynamoTradables();
            logger.info("RawTradable convertion completed");
            persistToDB(tradables);
            //new MrktCapMgr().run();
            execTopPerformers(tradables);
            StabilityMgr.run(tradables);
            BuySellIndicator.run(tradables);
            runOneHourtasks();
            TradeMgr.getTradeMgr().run(tradables);
        }catch(Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }


    private void runOneHourtasks() {

        DateTime time = TimeMgr.getTime();
        int mins = time.getMinuteOfHour();
        if( mins % 30 == 0){     //mins == 0
            logger.info("Running trend analysis");
            TrendAnalysis.findAndAlertTrend();
        }
    }

    private List<Tradable> getDynamoTradables(){

        List<RawTradable> rawTradables = RestService.getBittrexTradables().getRawTradables();
        logger.info("Bittrex service returned " + rawTradables.size() + " trades");

        logger.info("Converting all data_pipeline to dynamo data_pipeline");
        rawTradables = rawTradables.stream().filter(tradable -> !tradable.getMarketName().startsWith("ETH")).collect(Collectors.toList());
        List<Tradable> tradables =  TradableConverter.convert(rawTradables);
        Map<String, CurrCap> currCaps = new MrktCapMgr().getCurrCaps();
        return tradables.stream().map(tradable -> adjustVolume(tradable,currCaps)).collect(Collectors.toList());
    }

    private Tradable adjustVolume(Tradable tradable, Map<String, CurrCap> currCaps) {

        String mrktCapSymbol = tradable.getMarketName().split("-")[1];
        CurrCap currCap = (currCaps.containsKey(mrktCapSymbol)) ? currCaps.get(mrktCapSymbol) : null;
        if(currCap != null){
            tradable.setVolume(currCap.getVolume()/1000000);
        }
        return  tradable;
    }

    private void persistToDB(List<Tradable> tradables){

        //tradables.stream().forEach(dynamoTradable -> DynamoDbInterface.persistTradable(dynamoTradable));
        DBMgr.getDBMgr().getInterface().persistTradables(tradables,TimeMgr.getTimeStr());
        logger.info("Trades persisted to database");
    }

    private void execTopPerformers(List<Tradable> tradables) {

        BestPerformersMgr.persistBestPerformers(tradables);
        logger.info("Best performing trades computed");
    }
}
