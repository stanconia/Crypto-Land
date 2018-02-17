package org.personal.crypto_watcher.controller.metrics.stable_metrics.evaluator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.personal.crypto_watcher.controller.alerts.TelegramAlert;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.Tradable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class StableCurrAlertMgr {

    private static Treshold smallTreshold = new Treshold(0.04,1,2);
    private static Treshold medTreshold = new Treshold(0.03,1, 2);
    private static final int MAX_ALERT = 10;
    private static String msgID = "";
    private static Map<String,Alert> alertStore = new HashMap<>();
    private static com.google.common.cache.LoadingCache<String, Alert>
    cache = CacheBuilder.newBuilder()
            .expireAfterAccess(24,TimeUnit.HOURS)
                .build(new CacheLoader<String, Alert>() {
                    public Alert load(String symbol) {
                        return new Alert();
                    }
                });


    public static void checkSmallCurr(CurrencyStat currencyStat, Tradable tradable){

        checkCurr(currencyStat, tradable,smallTreshold);
        msgID = TelegramAlert.VOL_CHAT_ID;
    }

    public static void checkMediumCurr(CurrencyStat currencyStat, Tradable tradable){

        checkCurr(currencyStat, tradable,medTreshold);
        msgID = TelegramAlert.VOL_CHAT_ID;
    }

    private static void checkCurr(CurrencyStat currencyStat, Tradable tradable, Treshold treshold) {

        String symbol = tradable.getMarketName();
        if(currencyStat != null && tradable != null
                && currencyStat.getPriceMean() > 0 && currencyStat.getVolumeMean() > 0){

            Alert alert = new Alert();
            try {
                alert = cache.get(symbol);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getMessage());
            }
            checkVol(currencyStat,tradable,alert,treshold);
        }
    }

    private static void checkVol(CurrencyStat currencyStat, Tradable tradable, Alert alert, Treshold treshold) {

        double volIncr = tradable.getVolume()/ currencyStat.getVolumeMean();
        double priceIncr = tradable.getPrice()/ currencyStat.getPriceMean();
        String symbol = currencyStat.getSymbol();
        double volIncrTreshold = treshold.getVolTreshold();
        int volAlertCount = alert.volAlertCount;
        double adjustedMag = volAlertCount + 1;
        if(volIncr > 1 + (volIncrTreshold * adjustedMag)
                && volAlertCount < MAX_ALERT
                && tradable.getPrice() > currencyStat.getPriceMean()){
                //&& tradable.getNumSellOrders() < 0.96 * currencyStat.getNumSellMean()){
            alert.incrementVolCount();
            String msg = symbol +
                    "%20vol%20incr(%25):%20" + String.format("%.2f",(volIncr -1)*100) +
                    "%20price%20incr(%25):%20" + String.format("%.2f",(priceIncr -1)*100) +
                    "%20price%20stability:" + String.format("%.2f", currencyStat.getPriceWidth()*100) +
                    "%20numSell%20stability:" + String.format("%.2f", currencyStat.getNumBuyWidth()*100) +
                    "%20vol:" +  String.format("%.2f", currencyStat.getVolumeMean()) +
                    "%20alert%20count:" +  alert.volAlertCount;
            storeAlertAndNotify(symbol,alert,msg);
        }
    }

    private static void storeAlertAndNotify(String symbol,Alert alert, String msg){

        if(!alertStore.containsKey(symbol)){
            alertStore.put(symbol,alert);
        }
        TelegramAlert.notify( msg,msgID );
        //System.out.println("dddccc");  //TODO

    }


    private static class Alert{

        private int priceAlertCount;
        private int volAlertCount;

        public Alert(){

            priceAlertCount = 0;
            volAlertCount = 0;
        }

        public void incrementPriceCount(){
            priceAlertCount += 1;
        }

        public  void incrementVolCount(){
            volAlertCount +=1;
        }

    }

    private static class Treshold{

        private double priceTreshold;
        private double volTreshold;
        private double magnifier;

        public Treshold(double priceTreshold, double volTreshold,double mag) {
            this.priceTreshold = priceTreshold;
            this.volTreshold = volTreshold;
            this.magnifier = mag;
        }

        public double getPriceTreshold() {
            return priceTreshold;
        }

        public double getVolTreshold() {
            return volTreshold;
        }

        public double getMagnifier() {
            return magnifier;
        }
    }
}
