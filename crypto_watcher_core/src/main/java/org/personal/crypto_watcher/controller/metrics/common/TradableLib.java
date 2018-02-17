package org.personal.crypto_watcher.controller.metrics.common;

import org.joda.time.DateTime;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.TimeMgr;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.service.RestService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradableLib {

    public static CurrencyStat getDescriptiveStat(String symbol, int interval) {


        Map<String, List<Tradable>> data = getData(TimeMgr.getTime(), Stream.of(symbol).collect(Collectors.toList()), interval);
        return getCurrencyStat(symbol,data.get(symbol));
    }

    public static List<CurrencyStat> getDescriptiveStat(int interval) {

        return getDescriptiveStat(TimeMgr.getTime(),interval);
    }

    public static List<CurrencyStat> getDescriptiveStat(DateTime time, int interval) {

        List<String> cusips = TradableLib.getSymbols();
        Map<String, List<Tradable>> data = getData(time,cusips,interval);
        return data.entrySet().stream().map(entry -> getCurrencyStat(entry.getKey(),entry.getValue()))
                .collect(Collectors.toList());
    }

    private static List<String> getSymbols() {

        List<RawTradable> rawTradables = RestService.getBittrexTradables().getRawTradables();
        List<String> usdtSymbols = getUsdtSymbols(rawTradables);
        List<String> symbols =  rawTradables.stream().map(tradable -> tradable.getMarketName())
                .filter(cusip -> !cusip.startsWith("ETH"))
                .filter(cusip -> !(cusip.startsWith("BTC") && usdtSymbols.contains(cusip.split("-")[1])))
                .collect(Collectors.toList());

        return symbols;
    }

    private static List<String> getUsdtSymbols(List<RawTradable> rawTradables) {

        return rawTradables.stream().filter(tradable -> tradable.getMarketName().startsWith("USDT"))
                .map(tradable -> tradable.getMarketName().split("-")[1])
                .collect(Collectors.toList());
    }

    private static Map<String,List<Tradable>> getData(DateTime endTime,List<String> cusips, int interval) {


        DateTime startTime = endTime.minusMinutes(interval);
        Map<String,List<Tradable>> result = new HashMap<>();
        cusips.stream().forEach(cusip -> {
            List<Tradable> tradables = new ArrayList<>();
            for (DateTime date = startTime; date.isBefore(endTime); date = date.plusMinutes(15)){
                Tradable tradable = DBMgr.getDBMgr().getInterface().getTradable(cusip,TimeMgr.fmtTime(date));
                if(tradable != null){
                    tradables.add(tradable);
                }
            }
            result.put(cusip,tradables);
        });
        return result;
    }

    private static CurrencyStat getCurrencyStat(String symbol,List<Tradable> tradables){

        double[] prices = new double[tradables.size()];
        double[] volumes = new double[tradables.size()];
        double[] numSells = new double[tradables.size()];
        double[] numBuys = new double[tradables.size()];

        for (int i = 0; i < tradables.size(); i++) {
            prices[i] = tradables.get(i).getPrice();
            volumes[i] = tradables.get(i).getVolume();
            numSells[i] = tradables.get(i).getNumSellOrders();
            numBuys[i] = tradables.get(i).getNumBuyOrders();
        }
        if(prices.length > 0) {
            double priceWidth = DescriptiveStatLib.seqWidth(prices);
            double numBuyWidth = DescriptiveStatLib.seqWidth(numBuys);
            double numSellWidth = DescriptiveStatLib.seqWidth(numSells);
            double meanPrice = DescriptiveStatLib.getMean(prices);
            double meanVolume = DescriptiveStatLib.getMean(volumes);
            double numSellMean = DescriptiveStatLib.getMean(numSells);
            double numBuyMean = DescriptiveStatLib.getMean(numBuys);
            return new CurrencyStat(symbol, meanPrice,numBuyMean, priceWidth, numBuyWidth,meanVolume,numSellMean,numSellWidth);
        }else{
            return new CurrencyStat(symbol,0,0,0,0,0,0,0);
        }
    }

    private static double[][] get2DArray(double[] metric) {

        double[][] data = new double[metric.length][2];
        for(int i = 0; i < metric.length; i++){
            data[i][0] = i;
            data[i][1] = metric[i];
        }
        return data;
    }

    public static List<QueryResult> getHistoricalTradables(String symbol, String period, int numPeriod,int interval){

        DateTime currentTime = TimeMgr.getTime();
        return getHistoricalTradables(currentTime,symbol,period,numPeriod,interval);
    }

    public static List<QueryResult> getHistoricalTradables(DateTime startTime,
                                                           String symbol,
                                                           String period,
                                                           int numPeriod,
                                                           int interval){

        List<QueryResult> result = new ArrayList<>();
        List<DateTime> times = getTimes(startTime,period,numPeriod,interval);
        times.stream().forEach(time -> {
            String timeStr = TimeMgr.fmtTime(time);
            Tradable tradable = DBMgr.getDBMgr().getInterface().getTradable(symbol,timeStr);
            if(tradable != null) {
                result.add(new QueryResult(timeStr, tradable));
            }else{
                result.add(new QueryResult(timeStr, new Tradable()));
            }
        });
        return result;
    }

    private static List<DateTime> getTimes(DateTime currentTime, String timePeriod, int timePeriodNum,int interval) {

        DateTime startTime;
        List<DateTime> dateTimes = new ArrayList<>();
        switch(timePeriod) {
            case "min" :
                startTime = currentTime.minusMinutes(timePeriodNum);
                for (DateTime date = currentTime; date.isAfter(startTime); date = date.minusMinutes(interval))
                    dateTimes.add(date);
                break;
            case "hr" :
                startTime = currentTime.minusHours(timePeriodNum);
                for (DateTime date = startTime; date.isBefore(currentTime); date = date.plusHours(interval))
                    dateTimes.add(date);
                break;
            case "day" :
                startTime = currentTime.minusDays(timePeriodNum);
                for (DateTime date = startTime; date.isBefore(currentTime); date = date.plusDays(interval))
                    dateTimes.add(date);
                break;
            case "mnt" :
                startTime = currentTime.minusMonths(timePeriodNum);
                for (DateTime date = startTime; date.isBefore(currentTime); date = date.plusMonths(interval))
                    dateTimes.add(date);
                break;
            default :
                startTime = currentTime.minusMinutes(timePeriodNum);
                for (DateTime date = startTime; date.isBefore(currentTime); date = date.plusMinutes(interval))
                    dateTimes.add(date);
                break;
        }
        dateTimes.add(currentTime);
        return dateTimes;
    }

    public static class QueryResult{

        private String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Tradable getTradable() {
            return tradable;
        }

        public void setTradable(Tradable tradable) {
            this.tradable = tradable;
        }

        private Tradable tradable;

        public QueryResult(String time,Tradable tradable){

            this.time = time;
            this.tradable = tradable;
        }

        public QueryResult(){

        }
    }
}
