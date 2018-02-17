package org.personal.crypto_watcher.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;
import org.personal.crypto_watcher.controller.metrics.best_performers.GeneralCurrencyStat;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BestPerformers {

    @MongoId
    @JsonProperty("_id")
    private String time;

    @JsonProperty("performance_intervals")
    private Map<String,PerformanceInterval> interval;

    public String getTime() {return time;}
    public void setTime(String time) {this.time = time;}


    public Map<String, PerformanceInterval> getInterval() {return interval;}
    public void setInterval(Map<String, PerformanceInterval> interval) {this.interval = interval;}


    public static class PerformanceInterval {


        @JsonProperty("interval_metrics")
        private Map<String,List<GeneralCurrencyStat>> metricMap;

        public PerformanceInterval(){

        }

        public PerformanceInterval(String intervalName, Map<String,List<GeneralCurrencyStat>> metricMap){
            //this.intervalName = intervalName;
            this.metricMap = metricMap;
        }

        /*@DynamoDBAttribute(attributeName="interval")
        public String getIntervalName() {return intervalName;}
        public void setIntervalName(String intervalName) {this.intervalName = intervalName;}*/

        @DynamoDBAttribute(attributeName="interval_metrics")
        public Map<String, List<GeneralCurrencyStat>> getMetricMap() {return metricMap;}
        public void setMetricMap(Map<String, List<GeneralCurrencyStat>> metricMap) {this.metricMap = metricMap;}
    }
}
