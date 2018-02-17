package org.personal.crypto_watcher.controller.trade;

import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.controller.trade.model.SellRule;
import org.personal.crypto_watcher.model.Tradable;


public class CurrHealthDecider {

    public static boolean shouldSell(Tradable tradable, CoinProp coinProp,
                                     double boughtPrice,String ruleType) {

        String symbol = tradable.getMarketName();
        if( symbol.equals(coinProp.getSymbol()) ){
            SellRule sellRule = coinProp.getRule(ruleType);
            String threshType = sellRule.getThreshType();
            switch(threshType){
                case SellRule.PRICE_THRESH:
                    double thresh = sellRule.getPriceThresh();
                    return (tradable.getPrice() < boughtPrice *(1 - (thresh/boughtPrice)))? true : false;
                case SellRule.NUM_BUY_THRESH:
                    thresh = sellRule.getNumBuyThresh();
                    return (tradable.getNumBuyOrders() < coinProp.getMaxNumBuy() - thresh)? true : false;
                case SellRule.NUM_SELL_THRESH:
                    thresh = sellRule.getNumSellThresh();
                    return (tradable.getNumSellOrders() > coinProp.getMaxNumSell() + thresh)? true : false;
            }
            return false;
        }else{
            return false;
        }
    }

}
