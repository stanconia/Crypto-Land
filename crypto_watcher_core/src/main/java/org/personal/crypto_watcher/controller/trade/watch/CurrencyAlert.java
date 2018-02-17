package org.personal.crypto_watcher.controller.trade.watch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.personal.crypto_watcher.model.Tradable;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyAlert {

    private String symbol;
    private Map<String,Metric> metricMap;


    public CurrencyAlert(String symbol){

        this.symbol = symbol;
        this.metricMap = new HashMap<>();
    }

    public  CurrencyAlert(){

    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Map<String, Metric> getMetricMap() {
        return metricMap;
    }

    public void setMetricMap(Map<String, Metric> metricMap) {
        this.metricMap = metricMap;
    }

    public String getSymbol(){
        return symbol;
    }

    public void add(String key, double value, String alertType,boolean canOverride){

        Metric metric = metricMap.get(key);
        if(metric == null){
            metricMap.put(key,new Metric(key,value,alertType,canOverride));
        }else{
            Metric presentMetric = metricMap.get(key);
            if(presentMetric.canOverride){
                if(presentMetric.alertType.equalsIgnoreCase(alertType)){
                    metricMap.put(key,new Metric(key,presentMetric.value,alertType,canOverride));
                }else{
                    metricMap.put(key,new Metric(key,value,alertType,canOverride));
                }
            }
        }
    }




    public void removeMetric(String key){
        metricMap.remove(key);
    }

    public boolean hasMetCondition(Tradable tradable){

        if(!metricMap.isEmpty()) {
            boolean priceCondition = applyPriceCondition(tradable);
            boolean volCondition = applyVolCondition(tradable);
            boolean numBuyCondition = applyNumBuyCondition(tradable);
            boolean numSellCondition = applyNumSellCondition(tradable);
            return priceCondition && volCondition && numBuyCondition && numSellCondition;
        }else{
            return false;
        }
    }

    private boolean applyPriceCondition(Tradable tradable) {

        return checkCondition(tradable.getPrice(),"price");
    }

    private boolean applyVolCondition(Tradable tradable) {

        return checkCondition(tradable.getVolume(),"vol");
    }

    private boolean applyNumBuyCondition(Tradable tradable) {

        return checkCondition(tradable.getNumBuyOrders(),"num-buy");
    }

    private boolean applyNumSellCondition(Tradable tradable) {

        return checkCondition(tradable.getNumSellOrders(),"num-sell");
    }

    private boolean checkCondition(double currentVal, String key) {

        if(metricMap.containsKey(key)){
            Metric metric = metricMap.get(key);
            return checkCondition(currentVal, metric.value, metric.alertType);
        }else{
            return true;
        }
    }

    public void cleanse(){

        metricMap.entrySet().forEach(entry -> {
            String symbol = entry.getKey();
            Metric metric = entry.getValue();
            if(metric.time.isBefore(new DateTime().minusHours(24))) {
                removeMetric(symbol);
            }
        });
    }


    private boolean checkCondition(double currentValue, double alertValue, String alertType) {


        if(alertType.equals("less")){
            //boolean x = currentValue < alertValue;
            return currentValue < alertValue;
        }else{
            return currentValue> alertValue;
        }

    }


    public static class Metric{

        private String key;
        private double value;
        private String alertType;
        private DateTime time;
        private boolean canOverride;

        public Metric(String key, double value, String alertType,boolean canOverride) {
            this.key = key;
            this.value = value;
            this.alertType = alertType;
            this.time = new DateTime();
            this.canOverride = canOverride;
        }

    }
}



