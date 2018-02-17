package org.personal.crypto_watcher.controller.watch;

import org.junit.Assert;
import org.junit.Test;
import org.personal.crypto_watcher.controller.trade.watch.WatchMgr;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.model.Tradable;

import java.util.List;

public class WatchMgrTest {


    @Test
    public void testAddFunction(){

        //WatchMgr.add("btc","price",40,"less");
        //WatchMgr.add("btc","vol",40,"less");
        //WatchMgr.add("BTC-steem","num-sell",40,"less");
        //List<String> x = WatchMgr.getCurrUnderWatch();
        //Assert.assertEquals(WatchMgr.getCurrUnderWatch().size(),2);
    }

    @Test
    public void testWatchFunction(){

        //WatchMgr.add("BTC-steem","num-sell",1950,"more");
        //WatchMgr.add("btc","vol",40,"less");
        //WatchMgr.add("steem","num-sell",40,"less");
        Tradable tradable = new Tradable(new RawTradable("BTC-steem",4,4,0,2000),"");
       // WatchMgr.watch(tradable);
        //symbol=USDT-ETC&&alert_key=num_buy&&alert_value=1380&&alert_type=more
    }
}
