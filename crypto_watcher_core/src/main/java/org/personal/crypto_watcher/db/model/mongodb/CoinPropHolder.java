package org.personal.crypto_watcher.db.model.mongodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;

public class CoinPropHolder {

    @MongoId

    @JsonProperty("_id")
    private String id;
    private CoinProp value;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setValue(CoinProp coinProp) {
        this.value = coinProp;
    }


    public  CoinProp getValue() {
        return value;
    }
}
