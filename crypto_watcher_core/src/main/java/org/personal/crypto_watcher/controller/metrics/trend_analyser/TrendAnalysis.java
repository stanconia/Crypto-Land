package org.personal.crypto_watcher.controller.metrics.trend_analyser;

import org.apache.log4j.Logger;
import org.personal.crypto_watcher.api.TradableResource;
import org.personal.crypto_watcher.controller.alerts.TelegramAlert;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;
import org.personal.crypto_watcher.controller.trade.watch.WatchMgr;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.service.RestService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrendAnalysis {

    private static TradableResource resource = new TradableResource();
    private static Logger logger = Logger.getLogger(TrendAnalysis.class);

    public static void findAndAlertTrend(){

        logger.info("Running Trend Analysis");
        List<String> currs = getFocusCurrencies();
        List<String> smallCurrs = getSmallCurrencies(currs);
        currs.addAll(smallCurrs);
        findAndAlertTrend(currs);
    }


    private  static void findAndAlertTrend(List<String> symbols){

         List<Map<String,TrendOutput>> stream = symbols.stream()
                .map( symbol -> TrendCalculator.getTrendOutput(symbol))
                 .collect(Collectors.toList());
         List<TrendOutput> numBuyTrends = stream.stream().map(mappedTrends ->
                 mappedTrends.get(TrendCalculator.NUM_BUY_KEY))
                 .filter(trendOutput -> trendOutput.getScore() > 25)
                 .filter(trendOutput -> filterTrendOutput(trendOutput))
                 .sorted(Comparator.comparing(TrendOutput::getSpread).reversed())
                 .collect(Collectors.toList());
        /*List<TrendOutput> numSellTrends = stream.stream().map(mappedTrends ->
                mappedTrends.get(TrendCalculator.NUM_SELL_KEY))
                .filter(trendOutput -> trendOutput.getScore() > 0.00009)
                .filter(trendOutput -> filterTrendOutput(trendOutput))
                .sorted(Comparator.comparing(TrendOutput::getSpread).reversed())
                .collect(Collectors.toList());*/
        watch(numBuyTrends);
        TelegramAlert.notify( "num_buy_analyser%0A" + getOutput(numBuyTrends),TelegramAlert.TRADE_EVT_ID );
        //TelegramAlert.notify( "num_sell_analyser%0A" + getOutput(numSellTrends),TelegramAlert.TRADE_EVT_ID );

    }

    private static void watch(List<TrendOutput> numBuyTrends) {

        numBuyTrends.forEach(trend -> {

            double value = trend.getAdditionalInfo() - 200;
            WatchMgr.add(trend.getSymbol(),"num-sell",value,"less");
        });
    }

    private static boolean filterTrendOutput(TrendOutput trendOutput) {

       CurrencyStat stat = TradableLib.getDescriptiveStat(trendOutput.getSymbol(),60 * 6);
        if(stat.getPriceWidth() < 0.10){
            return true;
        }else{
            return false;
        }

    }

    private static String getOutput(List<TrendOutput> trends) {

        String output = "";
        for(TrendOutput trend : trends){
            output += trend.toString() + "%0A";
        }
        return output;
    }

    private static  List<String> getFocusCurrencies() {

        return  Stream.of("USDT-BTC"
                //,"USDT-BCC","USDT-ETH","USDT-BTG",
                //"USDT-NEO","USDT-LTC","USDT-XRP","USDT-ZEC",
                //"USDT-OMG","USDT-ETC","USDT-DASH","USDT-XMR",
                //"USDT-XVG","USDT-NXT","USDT-ADA"
                ).collect(Collectors.toList());
        /*return Stream.of("USDT-BTC")
                .collect(Collectors.toList());*/
    }

    private static  List<String> getSmallCurrencies(List<String> currs) {

        List<String> cusips = currs.stream().map(curr -> curr.split("-")[1]).collect(Collectors.toList());
        return RestService.getBittrexTradables().getRawTradables().stream()
                .filter(tradable -> !tradable.getMarketName().startsWith("ETH") && !tradable.getMarketName().startsWith("USDT"))
                .filter(tradable -> !cusips.contains(tradable.getMarketName().split("-")[1]))
                .filter(tradable -> tradable.getNumBuyOrders() > 150 && tradable.getVolume() > 100)
                .map(tradable -> tradable.getMarketName())
                .collect(Collectors.toList());
    }
}
