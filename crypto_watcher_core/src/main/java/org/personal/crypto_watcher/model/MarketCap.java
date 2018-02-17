package org.personal.crypto_watcher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "MarketCap")
public class MarketCap {

    @JsonProperty
    @Column(name = "total_market_cap_usd")
    private Long total_market_cap_usd;

    @JsonProperty
    @Column(name = "total_24h_volume_usd")
    private Long total_24h_volume_usd;

    @Column(name = "time")
    private String time;

    public Long getTotal_market_cap_usd() {
        return total_market_cap_usd;
    }

    public Long getTotal_24h_volume_usd() {
        return total_24h_volume_usd;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTotal_market_cap_usd(Long total_market_cap_usd) {
        this.total_market_cap_usd = total_market_cap_usd;
    }

    public String getTime() {
        return time;
    }

    public void setTotal_24h_volume_usd(Long total_24h_volume_usd) {
        this.total_24h_volume_usd = total_24h_volume_usd;
    }

}
