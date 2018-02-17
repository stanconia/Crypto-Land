package org.personal.crypto_watcher.db.model.mongodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;

import java.util.ArrayList;
import java.util.List;

public class CoinAlertHolder {

    @MongoId

    @JsonProperty("_id")
    private String id;
    private CurrencyAlert value;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CurrencyAlert getValue() {
        return value;
    }

    public void setValue(CurrencyAlert alert) {
        this.value = alert;
    }


}
