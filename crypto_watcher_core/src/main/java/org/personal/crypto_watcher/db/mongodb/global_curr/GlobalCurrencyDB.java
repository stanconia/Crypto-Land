package org.personal.crypto_watcher.db.mongodb.global_curr;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.personal.crypto_watcher.model.CurrCap;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalCurrencyDB {

    private static Jongo currDB;


    public static void init(MongoClient client) {

        currDB = new Jongo(client.getDB("global_curr"));
    }

    public static void persistCurr(List<CurrCap> currCaps) {

            MongoCollection globalCurrs = currDB.getCollection("global_currs");


        globalCurrs.update("{_id: 'id'}")
                .upsert()
                .with("{$set: {values: #}}",
                        currCaps.stream().collect(Collectors.toMap(CurrCap::getSymbol,(currCap) -> currCap)));
    }

    public static List<CurrCap> getCurr() {

        MongoCollection globalCurrs = currDB.getCollection("global_currs");
        Map<String,Map<String,Map>> resultMap = globalCurrs.findOne("{_id : 'id'}").as(Map.class);
        if(resultMap != null){
            Map<String,Map> resultValue = resultMap.get("values");
            return resultValue.entrySet().stream().map(entry ->{
                String symbol = entry.getKey();
                Map<String,Object> map = entry.getValue();
                CurrCap currCap = new CurrCap();
                currCap.setSymbol(symbol);
                currCap.setPrice((double) map.get("price_usd"));
                currCap.setVolume((double) map.get("24h_volume_usd"));
                currCap.setMarketCap((double) map.get("market_cap_usd"));
                return currCap;
            }).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }

    }


    private static class CurrCapHolder{

        private List<CurrCap> currCaps;


        public CurrCapHolder(List<CurrCap> currCaps){

            this.currCaps = currCaps;
        }

        public  CurrCapHolder(){}

        public List<CurrCap> getCurrCaps() {
            return currCaps;
        }

        public void setCurrCaps(List<CurrCap> currCaps) {
            this.currCaps = currCaps;
        }

    }

}
