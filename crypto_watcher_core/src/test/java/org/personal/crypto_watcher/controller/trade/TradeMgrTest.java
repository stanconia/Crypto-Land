package org.personal.crypto_watcher.controller.trade;

import org.junit.Test;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.model.Tradable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradeMgrTest {

    @Test
    public void testMonitor(){
        TradeMgr tradeMgr = TradeMgr.getTradeMgr();
        Tradable tradable = new Tradable(new RawTradable("BTC-VOX",0.000040,10000,1000,2000),
                "20161227");
        List<Tradable> tradables = Stream.of(tradable).collect(Collectors.toList());
        //tradeMgr.monitorBoughtCurrencies(tradables);

    }
}
