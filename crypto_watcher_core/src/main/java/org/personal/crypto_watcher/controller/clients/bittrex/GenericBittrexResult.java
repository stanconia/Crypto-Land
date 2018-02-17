package org.personal.crypto_watcher.controller.clients.bittrex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericBittrexResult {

    @JsonProperty("result")
    private List<Map<String,String>> contents;

    public List<Map<String,String>> getContents() {
        return contents;
    }

    public void setContents(List<Map<String, String>> contents) {
        this.contents = contents;
    }
}
