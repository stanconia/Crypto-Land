package org.personal.crypto_watcher.controller.trade;

import org.apache.log4j.Logger;
import org.personal.crypto_watcher.controller.clients.bittrex.BittrexClient;
import org.personal.crypto_watcher.controller.clients.bittrex.BittrexOrder;
import org.personal.crypto_watcher.controller.clients.bittrex.CurrBalance;
import org.personal.crypto_watcher.controller.trade.model.SellRule;
import org.personal.crypto_watcher.controller.trade.sell.SellHandler;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.controller.trade.watch.WatchMgr;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.CoinPropMgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradeMgr {

    private static TradeMgr tradeMgr;
    //private static com.google.common.cache.Cache<String, Double> tradeCache;
    private static boolean isOn;
    private static Logger logger = Logger.getLogger(TradeMgr.class);
    private  SellHandler sellHandler ;
    private CoinPropMgr coinPropMgr;


    private TradeMgr(){

        /*tradeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();*/
        isOn = false;
        sellHandler = SellHandler.getSellHandler();
        coinPropMgr = CoinPropMgr.getCoinPropMgr();
    }

    public static TradeMgr getTradeMgr(){

        if(tradeMgr == null){
            tradeMgr = new TradeMgr();
        }
        return tradeMgr;
    }

    public void run(List<Tradable> tradables) {

        tradables.forEach(tradable -> WatchMgr.watch(tradable));
        monitorBoughtCurrencies(tradables);
    }

/*    public void add(String key, double price){

        tradeCache.put(key,price);
    }*/

/*    public static void setIsOn(boolean shouldOn){
        logger.info("Setting isOn to " + shouldOn);
        isOn = shouldOn;
    }*/

/*    public void buy(Tradable tradable){

        if(isOn){
            String symbol = tradable.getMarketName();
            double price = tradable.getPrice();
            double quantity = 0.01/(price * 1.01);
            if(symbol.startsWith("BTC")){
                List<CurrBalance> balances =  BittrexClient.getBalances();
                if(!symbolIsBought(balances,symbol)){
                    CurrBalance btcBalance = balances.stream()
                        .filter(balance -> balance.getSymbol().equalsIgnoreCase("USDT-BTC")).collect(Collectors.toList()).get(0);
                    if(btcBalance.getAvailBalance() > 0.01 && priceNotInflated(tradable) ){
                        logger.info("Buying " + symbol);
                        BittrexClient.buy(symbol,quantity,(price * 1.01));
                    }
                }
            }
        }
    }


    private boolean symbolIsBought(List<CurrBalance> balances, String symbol) {

        return balances.stream()
                .map(balance -> balance.getSymbol())
                .collect(Collectors.toList())
                .contains(symbol);
    }*/

    public void monitorBoughtCurrencies(List<Tradable> tradables){

        sellHandler.monitorStopSells(tradables);
        if(isOn) {
            logger.info("monitoring bought currencies");
            Map<String, CurrBalance> currBalances = getMappedCurrBalance();
            List<Tradable> boughtTradables = getBoughtTradables(tradables, new ArrayList(currBalances.values()));
            takeAction( boughtTradables, currBalances);
        }
    }

    private void takeAction(List<Tradable> boughtTradables, Map<String, CurrBalance> currBalances) {


        boughtTradables.forEach(tradable -> {

                    String symbol = tradable.getMarketName();
                    CurrBalance currBalance = currBalances.get(symbol);
                    double boughtPrice = currBalance.getBuyOrders().get(0).getPrice();
                    CoinProp coinProp = coinPropMgr.getCoinProp(symbol);
                    String coinHealth = getHealthStatus(tradable,boughtPrice,coinProp);
                    takeAction(coinHealth,currBalance,coinProp,tradable);

            });
    }

    private void takeAction(String coinHealth, CurrBalance currBalance, CoinProp coinProp, Tradable tradable) {

        double totalBalance = currBalance.getBalance();
        double availBalance = currBalance.getAvailBalance();
        double boughtPrice = currBalance.getBuyOrders().get(0).getPrice();
        switch (coinHealth){
            case SellRule.LOSS:
                if(CurrHealthDecider.shouldSell(tradable,coinProp,boughtPrice,coinHealth)) {
                    sellAll(totalBalance,availBalance,tradable);
                }
            case SellRule.MOON:
                if(CurrHealthDecider.shouldSell(tradable,coinProp,boughtPrice,coinHealth)) {
                    sellAll(totalBalance,availBalance, tradable);
                }else{
                    placeStopSellOrder(tradable,totalBalance,availBalance,boughtPrice);

                }
        }
    }

    private void placeStopSellOrder(Tradable tradable, double totalBalance,
                                    double availBalance,double boughtPrice) {

        double profit = tradable.getPrice() - boughtPrice;
        double lmtPrice = (profit * 0.85) + boughtPrice;
        if (totalBalance != availBalance) {
            List<BittrexOrder> sellOrders = BittrexClient.getOpenSellOrders(tradable.getMarketName());
            sellOrders.stream().filter(order-> order.getLmtPrice() < lmtPrice)
                    .forEach(order ->  BittrexClient.cancelOrder(order.getUuid()));
        }
        if(availBalance > 0){
            sellHandler.stopSell(tradable.getMarketName(),availBalance,lmtPrice);
        }
    }

    private void sellAll(double totalBalance, double availBalance,Tradable tradable) {

        if (totalBalance != availBalance) {
            List<BittrexOrder> sellOrders = BittrexClient.getOpenSellOrders(tradable.getMarketName());
            sellOrders.forEach(order -> BittrexClient.cancelOrder(order.getUuid()));
        }
        sellHandler.sell(tradable.getMarketName(), totalBalance, tradable.getPrice() * 0.99);
    }


    private String getHealthStatus(Tradable tradable, double boughtPrice, CoinProp coinProp) {

        double ratio = tradable.getPrice()/boughtPrice;
        if(ratio < (1 - coinProp.getLossThresh())){
            return SellRule.LOSS;
        }else if(ratio < (1+ coinProp.getMoonThresh())){
            return SellRule.PROFIT;
        }else{
            return SellRule.MOON;
        }
    }


    private Map<String,CurrBalance> getMappedCurrBalance(){

        List<CurrBalance> currBalances = BittrexClient.getBalances();
        return currBalances.stream().collect(Collectors.toMap(CurrBalance::getMarket,(currBalance) -> currBalance));
    }

    private List<Tradable> getBoughtTradables(List<Tradable> tradables,List<CurrBalance> currBalances) {

        List<String> boughtCurrs = currBalances.stream().map(curr -> curr.getMarket()).collect(Collectors.toList());
        return
                tradables.stream()
                        .filter(tradable -> boughtCurrs.contains(tradable.getMarketName()))
                        .filter(tradable -> !tradable.getMarketName().equals("USDT-BTC") && !tradable.getMarketName().equals("BTC-PTOY"))
                        .collect(Collectors.toList());
    }


}
