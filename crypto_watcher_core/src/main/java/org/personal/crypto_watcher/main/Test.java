package org.personal.crypto_watcher.main;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.textmagic.sdk.RestClient;
import com.textmagic.sdk.RestException;
import com.textmagic.sdk.resource.instance.TMNewMessage;
import org.joda.time.DateTime;
import org.personal.crypto_watcher.controller.alerts.TextAlert;
import org.personal.crypto_watcher.controller.data_pipeline.TradableMgr;
import org.personal.crypto_watcher.controller.metrics.trend_analyser.TrendAnalysis;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.service.RestService;
import org.personal.crypto_watcher.util.TimeMgr;


public class Test {



    public static void main(String[] args) throws Exception {

        TimeMgr.setTime();
        TrendAnalysis.findAndAlertTrend();
        TrendAnalysis.findAndAlertTrend();

        /*Map<String,BtcPrice> btcPrices = getBTCPrices().stream().collect(Collectors.toMap(BtcPrice::getTime,((btcPrice) -> btcPrice)));
        List<Transaction> transactions = getTransactions();
        double cost = 0;
        double neg = 0;
        double pos = 0 ;
        for(Transaction trans : transactions) {
            String time = trans.getTime().substring(0,10);
            cost += trans.getBtcAmt() * btcPrices.get(time).price;
            if(trans.getBtcAmt() > 0){
                pos += trans.getBtcAmt() * btcPrices.get(time).price;
            }else{
                neg += trans.getBtcAmt() * btcPrices.get(time).price;
            }
        }
        System.out.println(pos);
        System.out.println(neg);
        System.out.println(cost);*/
    }

    private static List<BtcPrice> getBTCPrices() throws Exception {

        String res = RestService.get("https://api.blockchain.info/charts/market-price?timespan=450days&format=json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode array = mapper.readValue(res, JsonNode.class).get("values");
        Iterator<JsonNode> nodes = array.elements();
        List<BtcPrice> btcPrices = new ArrayList<>();
        while (nodes.hasNext()) {
            JsonNode node = nodes.next();
            BtcPrice btcPrice = new BtcPrice(node.get("x").asInt(), node.get("y").asDouble());
            btcPrices.add(btcPrice);
        }
        return btcPrices;
    }

    public static List<Transaction> getTransactions() throws IOException {

        String csvFile = "/Users/stanleyopara/Downloads/coinbase.csv";
        CSVReader reader = new CSVReader(new FileReader(csvFile));
        reader.readNext();
        String[] line;
        List<Transaction> transactions = new ArrayList<>();
        while ((line = reader.readNext()) != null) {
            String time = line[0].substring(0,10).replace("-","/");
            Transaction tran = new Transaction(time ,Double.parseDouble(line[2]));
            transactions.add(tran);
        }
        return transactions;
    }


    public static class BtcPrice {

        private String time;
        private double price;

        public BtcPrice(int time, double price){

            this.time = new DateTime((long)time*1000).toString("yyyy/MM/dd");
            this.price = price;
        }

        public String getTime() {
            return time;
        }

        public double getPrice() {
            return price;
        }

    }

    public static class Transaction{

        private String time;
        private double btcAmt;

        public Transaction(String time, double btcAmt){

            this.time = time;
            this.btcAmt = btcAmt;
        }

        public String getTime() {
            return time;
        }

        public double getBtcAmt() {
            return btcAmt;
        }

    }

}
