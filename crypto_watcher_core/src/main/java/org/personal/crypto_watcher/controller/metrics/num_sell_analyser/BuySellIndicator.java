package org.personal.crypto_watcher.controller.metrics.num_sell_analyser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.personal.crypto_watcher.controller.alerts.TelegramAlert;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.TimeMgr;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;
import org.personal.crypto_watcher.model.CurrencyStat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuySellIndicator {

    private static Map<String, Double> cusipStore;
    private static Map<String,Double> alerts;
    private static final String window = "1HR";
    private static Logger logger = Logger.getLogger(BuySellIndicator.class);

    static{
        cusipStore = DBMgr.getDBMgr().getInterface().getNumSellMeans(window);
        alerts = new HashMap<>();
    }

    public static void run(List<Tradable> tradables) {

        logger.info("BuySell Indicator Running...");
        DateTime time = TimeMgr.getTime();
        int mins = time.getMinuteOfHour();
        if( mins % 30 == 0){
            persistAvgNumSell();
            alerts = new HashMap<>();
            identifyEvents(tradables);
        }else{
            identifyEvents(tradables);
        }
    }

    public static void persistAvgNumSell(){

        int interval = 60;
        List<CurrencyStat> currStats = TradableLib.getDescriptiveStat(TimeMgr.getTime().minusHours(1),interval);
        Map<String,Double> numSellMeans = currStats.stream().collect(Collectors.toMap(CurrencyStat::getSymbol,CurrencyStat::getNumSellMean));
        DBMgr.getDBMgr().getInterface().persistNumSellMean(numSellMeans,window);
        cusipStore = numSellMeans;
    }

    public static void identifyEvents(List<Tradable> tradables){

        tradables.forEach(tradable -> {
            String symbol = tradable.getMarketName();
            if(cusipStore.containsKey(symbol)
                    && filterLargeCurrencies(symbol,tradable.getVolume())){
                double numSellMean = cusipStore.get(symbol);
                int numSellOrders = tradable.getNumSellOrders();
                analyseBuyEvent(numSellMean,symbol,numSellOrders);
                analyseSellEvent(numSellMean,symbol,numSellOrders);
            }
        });
    }

    private static void analyseSellEvent(double numSellMean,String symbol,double numSellOrders) {

        if(numSellMean != 0){
            double numSellIncr = getNumSellIncrRatio(numSellMean,symbol,numSellOrders);
            if( numSellIncr > 1.15){
                //alert("not_good",symbol, numSellIncr);
                //alerts.put(symbol,(numSellMean + numSellOrders)/2);

            }
        }
    }

    private static double getNumSellIncrRatio(double numSellMean,String symbol,double numSellOrders){

        if(alerts.containsKey(symbol)){
            numSellMean = alerts.get(symbol);
        }
        return numSellOrders/numSellMean;
    }

    private static void analyseBuyEvent(double numSellMean,String symbol,double numSellOrders) {

        if(numSellMean != 0){
            double numSellIncr = getNumSellIncrRatio(numSellMean,symbol,numSellOrders);
            if (numSellIncr < 0.85 ){
                alert("good",symbol, numSellIncr);
                alerts.put(symbol,numSellOrders);
            }
        }
    }

    public static boolean filterLargeCurrencies(String symbol, double volume){


        return symbol.startsWith("USDT") || volume > 200;
    }

    private static void alert(String event,String symbol,double value){

        String msg = event + "%20" +symbol + ":%20num_sell%20" + String.format("%.2f",value);
        TelegramAlert.notify( msg,TelegramAlert.MRKT_EVT_ID );
    }

}
