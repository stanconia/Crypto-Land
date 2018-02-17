package org.personal.crypto_watcher.controller.data_pipeline.bittrex;


import org.apache.log4j.Logger;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.util.TimeMgr;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradableConverter {

    private static final Logger logger = Logger.getLogger(TradableConverter.class);

    public static final String ONE_MIN_KEY = "one_min";
    public static final String FIVE_MIN_KEY = "five_min";
    public static final String TEN_MIN_KEY = "ten_min";
    public static final String THIRTY_MIN_KEY = "thirty_min";
    public static final String ONE_HR_KEY = "one_hr";
    public static final String ONE_DAY_KEY = "one_day";
    public static final List<String> INTERVALS = Stream.of(ONE_MIN_KEY,FIVE_MIN_KEY,TEN_MIN_KEY,THIRTY_MIN_KEY).collect(Collectors.toList());
    private static DecimalFormat df = new DecimalFormat(".##");
    private static Map<Integer,String> keyMap;
    static{
        keyMap = new HashMap<>();
        keyMap.put(0,ONE_MIN_KEY);
        keyMap.put(1,FIVE_MIN_KEY);
        keyMap.put(2,TEN_MIN_KEY);
        keyMap.put(3,THIRTY_MIN_KEY);
        //keyMap.put(4,ONE_HR_KEY);
        //keyMap.put(5,ONE_DAY_KEY);
    }

    public static List<Tradable> convert(List<RawTradable> rawTradables) {

        List<Tradable> tradables = new ArrayList<>();
        Map<String, Map<String, Tradable>> allPriorTradables = getAllPriorTradables();
        logger.info("All prior data_pipeline retrieved");
        for(RawTradable rawTradable : rawTradables){
            if(allPriorTradables.containsKey(rawTradable.getMarketName())){
                tradables.add(convert(rawTradable,allPriorTradables.get(rawTradable.getMarketName())));
            }else{
                tradables.add(convert(rawTradable,new HashMap<>()));
            }

        }
        return tradables;
    }

    private  static Map<String,Map<String,Tradable>> getAllPriorTradables() {

        Map<String,Map<String,Tradable>> priorTradablesMap = new HashMap<>();
        /*List<String> times =Stream.of(TimeMgr.fmtTime(TimeMgr.getOneMinBeforeTime()),TimeMgr.fmtTime(TimeMgr.getFiveMinBeforeTime())
                ,TimeMgr.fmtTime(TimeMgr.getTenMinBeforeTime()),TimeMgr.fmtTime(TimeMgr.getThirtyMinBeforeTime()),
                TimeMgr.fmtTime(TimeMgr.getOneHourBeforeTime()),TimeMgr.fmtTime(TimeMgr.getOneDayBeforeTime())).collect(Collectors.toList());*/
        List<String> times =Stream.of(TimeMgr.fmtTime(TimeMgr.getOneMinBeforeTime()),
                        TimeMgr.fmtTime(TimeMgr.getFiveMinBeforeTime()),
                        TimeMgr.fmtTime(TimeMgr.getTenMinBeforeTime()),
                        TimeMgr.fmtTime(TimeMgr.getThirtyMinBeforeTime())).collect(Collectors.toList());
        for(int i =0;i < times.size();i++){
            List<Tradable> tradables = DBMgr.getDBMgr().getInterface().getTradables(times.get(i));
            String timeKey = keyMap.get(i);
            for(Tradable tradable : tradables){
                if(priorTradablesMap.containsKey(tradable.getMarketName())){
                    priorTradablesMap.get(tradable.getMarketName()).put(timeKey, tradable);
                }else{
                    Map<String,Tradable> tradableMap = new HashMap<>();
                    tradableMap.put(timeKey, tradable);
                    priorTradablesMap.put(tradable.getMarketName(),tradableMap);
                }
            }
        }
        return priorTradablesMap;
    }

    private static Tradable convert(RawTradable rawTradable, Map<String,Tradable> priorTradables){

        Map<String,Tradable.Metrics> metrics = new HashMap<>();
        for(String key : keyMap.values()){
            Tradable priorTradable = new Tradable();
            if(priorTradables.containsKey(key)) {
                priorTradable = priorTradables.get(key);
            }
            computeMetrics(rawTradable, priorTradable,key,metrics);
        }

        Tradable tradable = new Tradable(rawTradable, TimeMgr.getTimeStr());
        tradable.setMetricMap(metrics);
        return tradable;
    }


    private static void computeMetrics(RawTradable rawTradable, Tradable tradable, String key, Map<String, Tradable.Metrics> metrics) {

        Tradable.Metrics metric = new Tradable.Metrics();

        metric.setVolumeVelocity(computePctDiff(rawTradable.getVolume(), tradable.getVolume()));
        metric.setPriceVelocity(computePctDiff(rawTradable.getPrice(), tradable.getPrice()));
        metric.setNumBuyOrdersVelocity(computePctDiff(rawTradable.getNumBuyOrders(), tradable.getNumBuyOrders()));
        metric.setNumSellOrdersVelocity(computePctDiff(rawTradable.getNumSellOrders(), tradable.getNumSellOrders()));

        //System.out.println(key);
        //System.out.println(tradable.getMetricMap().getCoinProp(key));
        Tradable.Metrics oldMetric = tradable.getMetricMap().get(key);
        if(oldMetric != null){
            metric.setVolumeAcceleration(metric.getVolumeVelocity() - oldMetric.getVolumeAcceleration());
            metric.setPriceAcceleration(metric.getPriceVelocity() - oldMetric.getPriceAcceleration());
            metric.setNumBuyOrdersAcceleration(metric.getNumBuyOrdersVelocity() - oldMetric.getNumBuyOrdersVelocity() );
            metric.setNumSellOrdersAcceleration(metric.getNumSellOrdersVelocity() - oldMetric.getNumSellOrdersVelocity());
        }else{
            metric.setVolumeAcceleration(0);
            metric.setPriceAcceleration(0);
            metric.setNumBuyOrdersAcceleration(0 );
            metric.setNumSellOrdersAcceleration(0);
        }

        metrics.put(key,metric);
    }

    private static double computePctDiff(double firstNum, double secNum){

        if(firstNum != 0){
            double ratio = (firstNum - secNum)/firstNum * 100;
            return Double.parseDouble(df.format(ratio));
        }else{
            return 0;
        }
    }


}
