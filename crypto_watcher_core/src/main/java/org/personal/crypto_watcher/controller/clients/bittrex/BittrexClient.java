package org.personal.crypto_watcher.controller.clients.bittrex;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.WebResource;
import org.jongo.Mapper;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.service.EncryptionUtility;
import org.personal.crypto_watcher.service.RestService;

import javax.ws.rs.client.Invocation;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BittrexClient {

    private static final String maleey = "df2ec71f940444b38e81ad96b9064596";
    private static final String macret = "6df11681269e4279a52eab455b7d75d8";
    private static final String encryptionAlgorithm = "HmacSHA512";
    private static final String BITTREX_MARKET = "https://bittrex.com/api/v1.1/public/getmarketsummaries";
    private static final String BITTREX_BALANCE = "https://bittrex.com/api/v1.1/account/getbalance?apikey=API_KEY&currency=";
    private static List<String> usdtMrkts;

    static{
        usdtMrkts = Stream.of("USDT-BTC","USDT-BCC","USDT-OMG","USDT-NEO","USDT-DASH"
        ,"USDT-XMR","USDT-ETC","USDT-BTG","USDT-ETH","USDT-LTC","USDT-XRP","USDT-ZEC",
                "USDT-ADA","USDT-XVG","USDT-NXT").collect(Collectors.toList());
    }


    public static RawTradable getTradables() throws IOException {

        WebResource webResource = RestService.getResource(BITTREX_MARKET);
        String result = webResource.get(Map.class).get("result").toString();
        return new ObjectMapper().readValue(result, RawTradable.class);
    }

    public static void cancelOrder(String uuid){

        String url = "https://bittrex.com/api/v1.1/market/cancel?uuid=" + uuid +
                "&apikey=" + maleey + "&nonce=" + EncryptionUtility.generateNonce();
        WebResource webResource = RestService.getResource(url);
        webResource
                .header("apisign", EncryptionUtility.calculateHash(macret, url, encryptionAlgorithm))
                .get(String.class);
    }

    public static List<BittrexOrder> getOpenSellOrders(String symbol){

        String url = "https://bittrex.com/api/v1.1/market/getopenorders?market=" + symbol +
                "&apikey=" + maleey + "&nonce=" + EncryptionUtility.generateNonce();
        WebResource webResource = RestService.getResource(url);
        List<Map<String,String>> orderMaps = webResource
                .header("apisign", EncryptionUtility.calculateHash(macret, url, encryptionAlgorithm))
                .get(GenericBittrexResult.class).getContents();
        return orderMaps.stream().filter(order -> order.get("OrderType").equals("LIMIT_SELL"))
                .map(order -> new BittrexOrder(symbol,order.get("Quantity"),
                        order.get("PricePerUnit"),order.get("OrderUuid"),order.get("Limit")))
                .collect(Collectors.toList());

    }

    public static CurrBalance getOrderHistory(String cusip){

        CurrBalance currBalance  = new CurrBalance();
        computeOrderHistory(cusip,currBalance);
        return currBalance;
    }

    private static void computeOrderHistory(String cusip, CurrBalance currBalance){

        String market = getMarket(cusip);
        String url = "https://bittrex.com/api/v1.1/account/getorderhistory?market=" + market +
                    "&apikey=" + maleey + "&nonce=" + EncryptionUtility.generateNonce();
        WebResource webResource = RestService.getResource(url);
            List<Map<String,String>> pastOrders = webResource
                    .header("apisign", EncryptionUtility.calculateHash(macret, url, encryptionAlgorithm))
                    .get(GenericBittrexResult.class).getContents();
            if(pastOrders != null && pastOrders.size() > 0) {
                List<BittrexOrder> buyOrders = pastOrders.stream().filter(order -> order.get("OrderType").equals("LIMIT_BUY") && !order.get("PricePerUnit").equalsIgnoreCase("null"))
                        .map(order -> new BittrexOrder(market, order.get("Quantity"), order.get("PricePerUnit")
                                , order.get("OrderUuid"),order.get("Limit")))
                        .collect(Collectors.toList());
                buyOrders = (buyOrders.size() > 0) ? buyOrders.subList(0, 1) : buyOrders;
                List<BittrexOrder> sellOrders = pastOrders.stream().filter(order -> order.get("OrderType").equals("LIMIT_SELL") && !order.get("PricePerUnit").equalsIgnoreCase("null"))
                        .map(order -> new BittrexOrder(market, order.get("Quantity"), order.get("PricePerUnit"),
                                order.get("OrderUuid"),order.get("Limit")))
                        .collect(Collectors.toList());
                sellOrders = (sellOrders.size() > 0) ? sellOrders.subList(0, 1) : sellOrders;
                currBalance.setMarket(market);
                currBalance.setBuyOrders(buyOrders);
                currBalance.setSellOrders(sellOrders);
            }

    }



    private static String getMarket(String cusip) {

        List<String> usdtCusips = usdtMrkts.stream().map(mrkt -> mrkt.split("-")[1]).collect(Collectors.toList());
        if(usdtCusips.contains(cusip)){
            return "USDT-" + cusip.toUpperCase();
        }else{
            return "BTC-" + cusip.toUpperCase();
        }
    }


    public static List<CurrBalance> getBalances(){

        try {
            String url = "https://bittrex.com/api/v1.1/account/getbalances?" + "apikey=" + maleey +
                    "&nonce=" + EncryptionUtility.generateNonce();
            WebResource webResource = RestService.getResource(url);
            List<Map<String,String>> result = webResource
                    .header("apisign", EncryptionUtility.calculateHash(macret, url, encryptionAlgorithm))
                    .get(GenericBittrexResult.class).getContents();
            List<CurrBalance> balances =
                    result.stream().map(content ->{
                        return new CurrBalance(content.get("Currency"),
                                Double.parseDouble(content.get("Available")),
                                Double.parseDouble(content.get("Balance")));
                    }).filter(currBalance -> currBalance.getBalance() > 0 )
                    .map(currBalance -> {
                        computeOrderHistory(currBalance.getSymbol(),currBalance);
                        return currBalance;
                    }).collect(Collectors.toList());
            return balances;
            }catch (Exception e){
                e.printStackTrace();
                return new ArrayList();
            }
        }

    public static String buy(String symbol,double quantity,double rate){

        return trade(symbol,quantity,rate,"buylimit");
    }

    public static String sell(String symbol,double quantity,double rate){

        return trade(symbol,quantity,rate,"selllimit");
    }

    private static String trade(String symbol,double quantity,double rate,String tradeType){

        String url = "https://bittrex.com/api/v1.1/market/" + tradeType + "?" + "apikey=" + maleey +
                "&nonce=" + EncryptionUtility.generateNonce() +
                "&market=" + symbol +
                "&quantity=" + quantity +
                "&rate=" + rate;
        WebResource webResource = RestService.getResource(url);
        return webResource
                .header("apisign",EncryptionUtility.calculateHash(macret, url, encryptionAlgorithm))
                .get(String.class);
    }
}
