package org.personal.crypto_watcher.controller.metrics.trend_analyser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.TimeMgr;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class TrendCalculator {

    public static final String NUM_BUY_KEY  = "num_buy";
    public static final String NUM_SELL_KEY  = "num_sell";
    private static Logger logger = Logger.getLogger(TrendAnalysis.class);

    public static Map<String,TrendOutput> getTrendOutput(String symbol){

        DateTime currentTime = TimeMgr.getTime();
        DateTime oneHrAgo = currentTime.minusHours(1);
        DateTime twoHrAgo = currentTime.minusHours(2);
        DateTime fourHrAgo = currentTime.minusHours(4);
        DateTime sixHrAgo = currentTime.minusHours(6);
        DateTime twelveHrAgo = currentTime.minusHours(12);

        String period = "min";
        int periodNum = 0;
        int interval = 1;
        Tradable currentT = TradableLib.getHistoricalTradables(
                currentTime,symbol,period,periodNum,interval)
                .get(0).getTradable();
        Tradable  oneHrAgoT = TradableLib.getHistoricalTradables(
                oneHrAgo,symbol,period,periodNum,interval)
                .get(0).getTradable();
        Tradable  twoHrAgoT = TradableLib.getHistoricalTradables(
                twoHrAgo,symbol,period,periodNum,interval)
                .get(0).getTradable();
        Tradable  fourHrAgoT = TradableLib.getHistoricalTradables(
                fourHrAgo,symbol,period,periodNum,interval)
                .get(0).getTradable();
        Tradable  sixHrAgoT= TradableLib.getHistoricalTradables(
                sixHrAgo,symbol,period,periodNum,interval)
                .get(0).getTradable();
        Tradable  twelveHrT = TradableLib.getHistoricalTradables(
                twelveHrAgo,symbol,period,periodNum,interval)
                .get(0).getTradable();

        TrendOutput numBuyTrendOutput = new TrendOutput(
                symbol,
                currentT.getNumBuyOrders(),
                oneHrAgoT.getNumBuyOrders(),
                twoHrAgoT.getNumBuyOrders(),
                fourHrAgoT.getNumBuyOrders(),
                sixHrAgoT.getNumBuyOrders(),
                twelveHrT.getNumBuyOrders());

        numBuyTrendOutput.setAdditionalInfo(currentT.getNumSellOrders());


        TrendOutput numSellTrendOutput = new TrendOutput(
                symbol,
                currentT.getNumSellOrders(),
                oneHrAgoT.getNumSellOrders(),
                twoHrAgoT.getNumSellOrders(),
                fourHrAgoT.getNumSellOrders(),
                sixHrAgoT.getNumSellOrders(),
                twelveHrT.getNumSellOrders());


        computeNumBuyScore(numBuyTrendOutput);
        computeNumSellScore(numSellTrendOutput);

        Map<String,TrendOutput> result = new HashMap<>();
        result.put(NUM_BUY_KEY,numBuyTrendOutput);
        result.put(NUM_SELL_KEY, numSellTrendOutput);
        return result;
    }


    private static double computeSpread(List<Double> seq, BiPredicate<Double,Double> condition) {

        double maxSpread = 0;
        for(int i = 0; i < seq.size(); i++){
            for(int j = i + 1; j < seq.size(); j ++){
                if(condition.test(seq.get(i) , seq.get(j))){
                    double spread = Math.abs(seq.get(i) - seq.get(j));
                    if (spread > maxSpread) maxSpread = spread;
                }
            }
        }
        return maxSpread;
    }

    public static void computeNumBuyScore(TrendOutput trendOutput) {

        List<Double> numBuys = Arrays.asList(
                trendOutput.getCurrent(),
                trendOutput.getOneHr(),
                trendOutput.getTwoHr(),
                trendOutput.getFourHr(),
                trendOutput.getSixHr(),
                trendOutput.getTwelveHr());
        BiPredicate<Double,Double> condition = (Double current, Double prior) -> current > prior;
        computeScore(trendOutput, condition,
                numBuys, (Double current, Double prior) -> 1.0);
        trendOutput.setSpread(computeSpread(numBuys,condition));
    }

    public static void computeNumSellScore(TrendOutput trendOutput) {

        List<Double> numSells = Arrays.asList(
                trendOutput.getCurrent(),
                trendOutput.getOneHr(),
                trendOutput.getTwoHr());
        BiPredicate<Double,Double> condition = (Double current,Double prior) -> current < prior;
        computeScore(trendOutput,  condition
                ,numSells,(Double current, Double prior) -> {
                    if(prior != 0){
                        return (current-prior)/prior;
                    }else{
                        return 0.0;
                    }
                });
        trendOutput.setSpread(computeSpread(numSells,condition));
    }

    private static void computeScore(TrendOutput trendOutput, BiPredicate<Double,Double> condition,
                                     List<Double> sequence,BiFunction<Double, Double, Double> biFunction) {



        int score = 0;
        for(int i = 0; i < sequence.size(); i++){
            int maxScore = 5 - i;
            for(int j = i + 1; j < sequence.size(); j ++){
                if(condition.test(sequence.get(i) , sequence.get(j))){
                    score += (maxScore * biFunction.apply(sequence.get(i),sequence.get(j)));
                }
                maxScore--;
            }
        }
        trendOutput.setScore(score);
    }

}
