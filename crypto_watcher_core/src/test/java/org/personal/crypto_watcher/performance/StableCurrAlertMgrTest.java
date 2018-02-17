package org.personal.crypto_watcher.performance;

import org.junit.Test;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.Tradable;

public class StableCurrAlertMgrTest {

    @Test
    public void testSmallCurr(){

        CurrencyStat currencyStat = new CurrencyStat("BTC-ABC",100,0,0,0,5,0,0);
        Tradable tradable1 = new Tradable();
        tradable1.setPrice(105);
        Tradable tradable2 = new Tradable();
        tradable2.setPrice(107);
        Tradable tradable3 = new Tradable();
        tradable3.setPrice(115);
        Tradable tradable4 = new Tradable();
        tradable4.setPrice(111);
        Tradable tradable5 = new Tradable();
        tradable5.setPrice(105);
        /*StableCurrAlertMgr.checkSmallCurr(currencyStat,tradable1);
        StableCurrAlertMgr.checkSmallCurr(currencyStat,tradable2);
        StableCurrAlertMgr.checkSmallCurr(currencyStat,tradable3);
        StableCurrAlertMgr.checkSmallCurr(currencyStat,tradable4);
        StableCurrAlertMgr.checkSmallCurr(currencyStat,tradable5);*/
    }
}
