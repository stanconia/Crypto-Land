package org.personal.crypto_watcher.controller.mrkt_cap;

import org.joda.time.DateTime;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.CurrCap;
import org.personal.crypto_watcher.util.TimeMgr;
import org.personal.crypto_watcher.model.MarketCap;
import org.personal.crypto_watcher.service.RestService;
import org.personal.crypto_watcher.db.postgres.PostgresInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class MrktCapMgr {

    private final Logger LOGGER = LoggerFactory.getLogger(MrktCapMgr.class);
    public static final String TIME_FORMAT = "MM/dd/yyyy HH:mm";

    public MrktCapMgr(){

    }

    public void run(){

        MarketCap mrktCap = RestService.getMarketCap();
        if(mrktCap != null) {
            process(mrktCap);
            LOGGER.info("External service sucessful, launching process cap");
            persistToDb(mrktCap);
            LOGGER.info("Persist to DB successful");
        }
    }

    private void persistToDb(MarketCap mrktCap) {

        DBMgr.getDBMgr().getInterface().putMarketCap(mrktCap);
    }

    private void process(MarketCap marketCap){

        String time = TimeMgr.getTimeStr();
        marketCap.setTime(time);
        marketCap.setTotal_24h_volume_usd(marketCap.getTotal_24h_volume_usd()/1000000);
        marketCap.setTotal_market_cap_usd(marketCap.getTotal_market_cap_usd()/1000000);
    }

    public Map<String,CurrCap> getCurrCaps(){

        try {
            List<CurrCap> currCaps = RestService.getCurrCaps();
            return currCaps.stream().collect(Collectors.toMap(CurrCap::getSymbol,(currCap) -> currCap));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
