package org.personal.crypto_watcher.db.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NumSellCollection {

    @MongoId

    @JsonProperty("_id")
    private String id;
    private Map<String,Double> values;

    public NumSellCollection (){
        id = "";
        values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,Double> getCurr() {
        return values;
    }

    public void setCurr(Map<String,Double> curr) {
        this.values = curr;
    }
}
