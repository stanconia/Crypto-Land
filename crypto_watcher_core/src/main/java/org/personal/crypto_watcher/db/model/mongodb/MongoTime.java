package org.personal.crypto_watcher.db.model.mongodb;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoTime {


    @MongoId

    @JsonProperty("_id")
    private String time;


    private List<String> children;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

}
