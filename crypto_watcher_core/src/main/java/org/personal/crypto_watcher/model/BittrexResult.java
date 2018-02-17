package org.personal.crypto_watcher.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BittrexResult {

    @JsonProperty("result")
    private List<RawTradable> rawTradables;

    public List<RawTradable> getRawTradables() {
        return rawTradables;
    }

    public void setRawTradables(List<RawTradable> rawTradables) {
        this.rawTradables = rawTradables;
    }

}
