package org.personal.crypto_watcher.db.postgres;



import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.db.DBInterface;
import org.personal.crypto_watcher.db.postgres.db_helpers.*;
import org.personal.crypto_watcher.model.CurrCap;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.MarketCap;
import org.personal.crypto_watcher.model.Tradable;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;


public class PostgresDBInterface implements DBInterface{

    private static DataInterface dataInterface;
    private static MarketCapInterface mrktCapInterface;
    private static CurrPropInterface currPropInterface;
    private static CurrAlertInterface currAlertInterface;
    private static CommonInterface commonInterface;
    private static CurrencyStatInterface currStatInterface;
    private static GlobalCurrInterface globalCurrInterface;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDBInterface.class);

    public PostgresDBInterface(){

        if(dataInterface == null) dataInterface = DataInterface.get(DSL.using(getDataSource(DataInterface.getDBName()), SQLDialect.POSTGRES));
        if(mrktCapInterface == null) mrktCapInterface = MarketCapInterface.get(DSL.using(getDataSource(MarketCapInterface.getDBName()), SQLDialect.POSTGRES));
        if(currStatInterface == null) currStatInterface = CurrencyStatInterface.get(DSL.using(getDataSource(CurrencyStatInterface.getDBName()), SQLDialect.POSTGRES));
        if(currAlertInterface == null) currAlertInterface = CurrAlertInterface.get(DSL.using(getDataSource(CurrAlertInterface.getDBName()), SQLDialect.POSTGRES));
        if(currPropInterface == null) currPropInterface = CurrPropInterface.get(DSL.using(getDataSource(CurrPropInterface.getDBName()), SQLDialect.POSTGRES));
        if(commonInterface == null) commonInterface = CommonInterface.get(DSL.using(getDataSource(CommonInterface.getDBName()), SQLDialect.POSTGRES));
        if(globalCurrInterface == null) globalCurrInterface = GlobalCurrInterface.get(DSL.using(getDataSource(GlobalCurrInterface.getDBName()), SQLDialect.POSTGRES));
    }

    private static DataSource getDataSource(String dbName){

        int randNum = 0 + (int)(Math.random() * ((1000 - 0) + 1));
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName(dbName + randNum );
        source.setPortNumber( 5432);
        source.setServerName("crypto-land.postgres.database.azure.com");
        source.setDatabaseName(dbName);
        source.setUser("stanwizzy@crypto-land");
        source.setPassword("Ab34567@");
        source.setMaxConnections(10);
        return source;
    }


    @Override
    public void persistTradables(List<Tradable> bittrexTradables, String time) {

        dataInterface.persistTradables(bittrexTradables,time);
    }


    @Override
    public List<Tradable> getTradables(String time) {

        List<Tradable> result = dataInterface.getTradables(time);
        return  ( result == null) ? new ArrayList<>() : result;
    }


    @Override
    public Tradable getTradable(String cusip, String time)  {

        return dataInterface.getTradable(cusip,time);
    }

    @Override
    public Tradable getTradable(String key) {

        String symbol = key.split("_")[0];
        String time = key.split("_")[1];
        return getTradable(symbol,time);
    }

    @Override
    public void persistCurrStat(List<CurrencyStat> currStats, String time, String window) {

        currStatInterface.persistCurrStat(currStats,time,window);
    }

    @Override
    public CurrencyStat getCurrStat(String symbol, String id) {

        return currStatInterface.getCurrStat(symbol,id);
    }

    @Override
    public String getLatestStableCurrIDs(String window) {

        return commonInterface.getLatestStableCurrIDs(window);
    }

    @Override
    public List<CurrencyStat> getStableCurr(String id, String window) {

        List<String> stableCurrs = commonInterface.getStableCurr(id);
        List<CurrencyStat> result = stableCurrs.stream()
                .map(currName -> currStatInterface.getCurrStat(currName,id) )
                .collect(Collectors.toList());
        return  ( result == null) ? new ArrayList<>() : result;
    }

    @Override
    public void updateLatestID(String id, String window) {

        commonInterface.updateLatestID(id, window);

    }

    @Override
    public void persistStableCurr(List<CurrencyStat> stableCurrs, String time, String window) {

        List<String> currNames = stableCurrs.stream().map(curr-> curr.getSymbol()).collect(Collectors.toList());
        commonInterface.persistStableCurr(currNames, time + "_" + window);
    }

    @Override
    public Map<String, Double> getNumSellMeans(String window) {

        Map<String, Double> result = commonInterface.getNumSellMeans(window);
        return  ( result == null) ? new HashMap<>() : result;
    }

    @Override
    public void persistNumSellMean(Map<String, Double> numSellMean, String window) {

        commonInterface.persistNumSellMean(numSellMean,window);
    }

    @Override
    public void persistCoinAlert(CurrencyAlert currAlert) {

        currAlertInterface.persistCoinAlert(currAlert);
    }

    @Override
    public CurrencyAlert getCoinAlert(String symbol) {

        return currAlertInterface.getCoinAlert(symbol);
    }

    @Override
    public void persistCoinProp(CoinProp coinProp) {

        currPropInterface.persistCoinProp(coinProp);
    }

    @Override
    public CoinProp getCoinProp(String symbol) {
        return null;
    }

    public MarketCap getMarketCap(String time) {

        return mrktCapInterface.getMarketCap(time);
    }

    public void putMarketCap(MarketCap marketCap) {

        mrktCapInterface.putMarketCap(marketCap);
    }

    @Override
    public void persistGlobalCurr(List<CurrCap> currs) {

        globalCurrInterface.persistCurr(currs);
    }

    @Override
    public List<CurrCap> getGlobalCurr() {

        return globalCurrInterface.getCurr();
    }

    public void deleteData(String symbol, String time){

        dataInterface.deleteData(symbol,time);
    }

}
