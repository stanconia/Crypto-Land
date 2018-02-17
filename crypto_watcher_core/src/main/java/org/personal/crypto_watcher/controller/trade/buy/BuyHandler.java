package org.personal.crypto_watcher.controller.trade.buy;

import org.personal.crypto_watcher.controller.clients.bittrex.BittrexClient;
import org.personal.crypto_watcher.controller.clients.bittrex.CurrBalance;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.model.Tradable;

import java.util.Map;

public class BuyHandler {

    private static BuyHandler buyHandler;

    private  BuyHandler(){

    }

    public static BuyHandler getHandler(){

        if(buyHandler == null){
            buyHandler = new BuyHandler();
        }

        return buyHandler;
    }

    public void buy(Tradable tradable, CoinProp coinProp, Map<String,CurrBalance> balanceMap){

        if(tradable.getMarketName().equalsIgnoreCase(coinProp.getSymbol())){
            String symbol = tradable.getMarketName();
            double price = tradable.getPrice();
            double quantity = coinProp.getCash()/(price * 1.01);
            String currType = coinProp.getCurrencyType();
            if(hasBalance(currType, coinProp.getCash(),balanceMap)){
                BittrexClient.buy(symbol,quantity,(price * 1.01));
            }
        }
    }

    public void smartBuy(Tradable tradable, CoinProp coinProp, Map<String,CurrBalance> balanceMap){

        if(priceNotInflated(tradable)){
            buy(tradable, coinProp,balanceMap);
        }
    }

    private boolean priceNotInflated(Tradable tradable) {

        Tradable hrAgoTradable = TradableLib.getHistoricalTradables(tradable.getMarketName(),
                "hr",0,1).get(0).getTradable();
        return tradable.getPrice() < (hrAgoTradable.getPrice() * 1.15);
    }


    private boolean hasBalance(String currType,
                               double tradeQuantity, Map<String, CurrBalance> balanceMap) {

        if(currType.equalsIgnoreCase(CoinProp.BTC_CURRENCY)){
            if(balanceMap.containsKey("btc")){
                return balanceMap.get("btc").getAvailBalance() >= tradeQuantity;
            }
        }else if(currType.equalsIgnoreCase(CoinProp.USDT_CURRENCY)){
            if(balanceMap.containsKey("usdt")){
                return balanceMap.get("usdt").getAvailBalance() >= tradeQuantity;
            }
        }
        return false;
    }


}
