package org.personal.crypto_watcher.controller.metrics.trend_analyser;

import org.apache.log4j.Logger;
import org.personal.crypto_watcher.api.TradableResource;
import org.personal.crypto_watcher.controller.alerts.TelegramAlert;
import org.personal.crypto_watcher.controller.alerts.TextAlert;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;
import org.personal.crypto_watcher.controller.trade.watch.WatchMgr;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.CurrCap;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.MarketCap;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.service.RestService;
import org.personal.crypto_watcher.util.TimeMgr;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrendAnalysis {

    private static Logger logger = Logger.getLogger(TrendAnalysis.class);
    private static double mrktCapHolder = 0;
    private static List<VolIncrOutput> priorVolIncrOutputs;

    public static void findAndAlertTrend(){

        logger.info("Running Trend Analysis");
        List<String> btc = Stream.of("USDT-BTC").collect(Collectors.toList());
        List<String> currs = getBittrexBTCCurrs(btc);
        btc.addAll(currs);
        findAndAlertTrend(currs);
    }

    private static  List<String> getBittrexBTCCurrs(List<String> currs) {

        List<String> cusips = currs.stream().map(curr -> curr.split("-")[1]).collect(Collectors.toList());
        return RestService.getBittrexTradables().getRawTradables().stream()
                .filter(tradable -> !tradable.getMarketName().startsWith("ETH") && !tradable.getMarketName().startsWith("USDT"))
                .filter(tradable -> !cusips.contains(tradable.getMarketName().split("-")[1]))
                .filter(tradable -> tradable.getNumBuyOrders() > 150 && tradable.getVolume() > 100)
                .map(tradable -> tradable.getMarketName())
                .collect(Collectors.toList());
    }


    private  static void findAndAlertTrend(List<String> symbols){

        String mrktCapTrendOutput = getMrktCapTrend(symbols);
        String numBuyTrendOutput = getNumBuyTrendOutput(symbols);
        String volIncrOutput = getVolIncreases();
        String output = mrktCapTrendOutput + "%0A" + numBuyTrendOutput + "%0A" + volIncrOutput;
        TelegramAlert.notify(  output,TelegramAlert.TRADE_EVT_ID );
    }


    private static String getMrktCapTrend(List<String> symbols) {

        MarketCap marketCap = RestService.getMarketCap();
        String mrktCap = String.format("%.2f",marketCap.getTotal_market_cap_usd()/1E9);
        sendMrktUpdate(marketCap.getTotal_market_cap_usd()/1E9);
        String vol = String.format("%.2f",marketCap.getTotal_24h_volume_usd()/1E9);

        Tradable btcT = TradableLib.getHistoricalTradables(
                TimeMgr.getTime(),"USDT-BTC","min",0,1)
                .get(0).getTradable();
        return "mrkt_cap:%20" + mrktCap + "%20" + vol + "%20%20" + "btc:%20" + btcT.getNumBuyOrders()  +  "%20" + btcT.getNumSellOrders();
    }

    private static void sendMrktUpdate(double mrktCap) {

        if (mrktCapHolder > 0){
            if(mrktCap < mrktCapHolder - 4){
                TextAlert.send("Market is bad " + mrktCapHolder + " " + mrktCap);
            }else if(mrktCap > mrktCapHolder + 4){
                TextAlert.send("Market is milky " + mrktCapHolder + " " + mrktCap);
            }
        }
        mrktCapHolder = mrktCap;
    }

    private static String getNumBuyTrendOutput(List<String> symbols) {

        List<Map<String,TrendOutput>> mappedTrendList = symbols.stream()
                .map( symbol -> TrendCalculator.getTrendOutput(symbol))
                .collect(Collectors.toList());
        List<TrendOutput> numBuyTrends = mappedTrendList.stream().map(mappedTrends ->
                mappedTrends.get(TrendCalculator.NUM_BUY_KEY))
                .filter(trendOutput -> trendOutput.getScore() > 25)
                .filter(trendOutput -> filterNumBuyTrendOutput(trendOutput))
                .sorted(Comparator.comparing(TrendOutput::getSpread).reversed())
                .collect(Collectors.toList());
        watch(numBuyTrends);
        return getOutput(numBuyTrends.stream().collect(Collectors.toList()), "");
    }

    private static void watch(List<TrendOutput> numBuyTrends) {

        numBuyTrends.forEach(trend -> {

            double value = trend.getAdditionalDouble() * 0.9;
            WatchMgr.add(trend.getSymbol(),"num-sell",value,"less");
        });
    }

    private static boolean filterNumBuyTrendOutput(TrendOutput trendOutput) {

        CurrencyStat stat = TradableLib.getDescriptiveStat(trendOutput.getSymbol(),60 * 6);
        if(stat.getPriceWidth() < 0.10){
            return true;
        }else{
            return false;
        }

    }

    private static String getOutput(List<Object> objects,String prefix) {

        String output = prefix;
        for(Object obj : objects){
            output += obj.toString() + "%0A";
        }
        return output;
    }


    private static String getVolIncreases(){

        try {
            List<CurrCap> currCaps = RestService.getCurrCaps();
            List<CurrCap> priorCaps = DBMgr.getDBMgr().getInterface().getGlobalCurr();
            Map<String,CurrCap> priorCapMap = priorCaps.stream().collect(Collectors.toMap(CurrCap::getSymbol, (currCap) -> currCap));
            List<CurrCap> topCaps = currCaps.stream().filter(cap -> cap.getMarketCap() > 400E6).collect(Collectors.toList());
            List<CurrCap> midCaps = currCaps.stream().filter(cap -> cap.getMarketCap() > 50E6 && cap.getMarketCap() < 400E6).collect(Collectors.toList());
            String topResult = getVolIncreases(topCaps,priorCapMap,true);
            String midResult = getVolIncreases(midCaps,priorCapMap,false);
            String result =  "top_vol_incr:%0A" + topResult + "%0A%0A" + "mid_vol_incr:%20" + midResult;
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getVolIncreases(List<CurrCap> currCaps,Map<String,CurrCap> priorCapMap, boolean shouldInspect)  {

        try {
            List<VolIncrOutput> results = getIncr(priorCapMap, currCaps);
            if (shouldInspect) interpreteVolResults(results);
            String output = "";
            results = (results.size() < 8)? results: results.subList(0, 7);
            for (VolIncrOutput result : results) {
                output += result.toString().replace(",","(").replace(":","%20:") + ")%7C%20";
            }
            return output;
            //TelegramAlert.notify("vol_incr:%0A" + output, TelegramAlert.TRADE_EVT_ID);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }


    private static List<VolIncrOutput> getIncr(Map<String,CurrCap> priorCaps,List<CurrCap> currCaps) throws NoSuchAlgorithmException, IOException {


        List<VolIncrOutput> results = currCaps.stream()
                .filter(currCap -> {
                    CurrCap priorCap = priorCaps.get(currCap.getSymbol());
                    return priorCap != null && currCap.getPrice() > priorCap.getPrice() && currCap.getVolume() > 10E6;
                })
                .map(currCap -> {
                    double vol = (currCap.getVolume());
                    CurrCap priorCap = priorCaps.get(currCap.getSymbol());
                    //String volIncr =  (priorCap != null )?  String.format("%.0f",(vol/priorCap.getVolume() - 1) * 100): "0";
                    double volIncr =  (priorCap != null )?  (vol/priorCap.getVolume() - 1) * 100 : 0;
                    return new VolIncrOutput(currCap.getSymbol(),(int)volIncr,(int) (currCap.getVolume()/1E6));
                    //return currCap.getSymbol() + "," + volIncr + ":" + (int) (currCap.getVolume()/1E6);
                })
                .filter(res -> res.volIncr > 0 )
                .collect(Collectors.toList());


        results.sort((VolIncrOutput res1, VolIncrOutput res2)-> {

            Integer ans1 = res1.volIncr;
            Integer ans2 = res2.volIncr;
            return ans2.compareTo(ans1);
        });
        return results;
    }

    private static void interpreteVolResults(List<VolIncrOutput> volIncrOutputs) {

        if(priorVolIncrOutputs == null){
            priorVolIncrOutputs = volIncrOutputs;
        }else{
            Map<String,VolIncrOutput> priorVolIncrMap =
                    priorVolIncrOutputs.stream().collect(Collectors.toMap(VolIncrOutput::getSymbol,(volInc) ->volInc));
            volIncrOutputs.forEach(volIncr ->{
                if(priorVolIncrOutputs.contains(volIncr)){
                    VolIncrOutput priorVolIncr = priorVolIncrMap.get(volIncr.symbol);
                    if(volIncr.volIncr > priorVolIncr.volIncr + 80){
                        TextAlert.send(volIncr.symbol+ " is still cooking " + volIncr.volIncr + " with vol " + volIncr.vol); }
                }else{
                    if(volIncr.volIncr > 70){
                        TextAlert.send(volIncr.symbol+ "(new) is going gaga " + volIncr.volIncr + " with vol " + volIncr.vol); }
                }
            });
            priorVolIncrOutputs = volIncrOutputs;
        }
        /*results.forEach(result -> {

            String symbol = result.substring(0,result.indexOf(","));
            int volIncr = Integer.parseInt(result.substring(result.indexOf(",") + 1, result.indexOf(":") ));
            int vol = Integer.parseInt(result.substring(result.indexOf(":") + 1 ));
        });*/
    }

    private static class VolIncrOutput {

        private String symbol;
        private int volIncr;
        private int vol;

        @Override
        public boolean equals(Object obj) {

            VolIncrOutput otherOutput = (VolIncrOutput)obj;
            return otherOutput.symbol.equals(this.symbol);
        }

        public VolIncrOutput(String symbol, int volIncr, int vol){

            this.symbol = symbol;
            this.volIncr = volIncr;
            this.vol = vol;
        }

        public String getSymbol(){
            return symbol;
        }


        @Override
        public String toString() {
            return symbol + "," + volIncr + ":" + vol;
        }
    }
}
