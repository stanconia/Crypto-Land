package org.personal.crypto_watcher.main;


import org.personal.crypto_watcher.controller.alerts.TelegramAlert;
import org.personal.crypto_watcher.controller.clients.bittrex.BittrexClient;
import org.personal.crypto_watcher.controller.clients.bittrex.BittrexOrder;
import org.personal.crypto_watcher.controller.clients.bittrex.CurrBalance;
import org.personal.crypto_watcher.controller.data_pipeline.TradableMgr;
import org.personal.crypto_watcher.controller.mrkt_cap.MrktCapMgr;
import org.personal.crypto_watcher.db.postgres.PostgresDBInterface;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.util.TimeMgr;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        TimeMgr.setTime();
        //Tradable tradable = new PostgresDBInterface().getTradable("USDT-ADA","02/10/2018 16:41");
        //System.out.println("praaa");
        new TradableMgr().runTasks();
        //new MrktCapMgr().run();

       /* List<RawTradable> data_pipeline = RestService.getBittrexTradables().getRawTradables();
        data_pipeline = data_pipeline.stream().filter(tradable -> !tradable.getMarketName().startsWith("ETH")).collect(Collectors.toList());
        RawTradable tradable = data_pipeline.get(0);
        Tradable dynamoTradable = TradableConverter.convert(tradable,new HashMap<String,Tradable>(),new DateTime().toString("MM/dd/yyyy HH:mm"));
        DynamoDbInterface.persistTradable(dynamoTradable);*/
        //DynamoDbInterface.getTradable("BTC-1ST","10/02/2017 15:49");


        //double[][] data =  {{0, 2000}, {10,2100}, {20, 2200},{30, 2300},{40, 2400},{50, 2500},{60, 2600}};
        //System.out.println(DescriptiveStatLib.computeReg(data));

        //TimeMgr.setTime();
        //System.out.println(BittrexClient.getBalance("USDT-BTC"));

        //500 600 700 800 900 1000 -> 500 400 300 200 100  // 1| 1 2| 1 2 3| 1 2 3 4| 1 2 3 4 5
       /* TrendOutput trendOutput = new TrendOutput(500,600,400,300,200,100);
        TrendCalculator.computeNumSellScore(trendOutput);
        System.out.println(trendOutput.getScore());*/

       //BittrexClient.cancelOrder("a5b059dc-4283-4d00-ae3d-abbcd2c30c2d");
       //List<BittrexOrder> currBalance = BittrexClient.getOpenSellOrders("BTC-VOX");
        //System.out.println("dddf");
        //System.out.println(BittrexClient.getBalance("USDT-BTC"));
        //System.out.println(RestService.buy("USDT-BTG",1,370));
        /*TimeMgr.setTime();
        TradableMgr mgr = new TradableMgr();
        mgr.runTasks();*/
        /*TimeMgr.setTime();
        StableCurrencyFactory stableCurrencyFactory = new StableCurrencyFactory("12HR");
        stableCurrencyFactory.runTasks();*/
        //StableNumSellMgr.findAndAlertTrend("12HR");

/*
        String url = "http://crypto-dev6.us-east-1.elasticbeanstalk.com:9090/tradable/query?symbol=USDT-BTC&&period=min&&period_num=1000&&interval=5";
        ObjectMapper mapper = new ObjectMapper();
        List<TradableResource.QueryResult> results =mapper.readValue(RestService.get(url),
                new TypeReference<List<TradableResource.QueryResult>>(){});
        final StringBuilder price = new StringBuilder();
        final StringBuilder numSell = new StringBuilder();
        results.forEach(result -> {
            //price.append(String.format("%.2f",result.getTradable().getPrice()) + ",");
            numSell.append(result.getTradable().getNumSellOrders() + ",");
        });*/
/*        String numSellssss = "4428,4424,4436,4452,4460,4469,4470,4459,4459,4494,4513,4530,4529," +
                         "4579,4583,4581,4568,4590,4603,4610,4595,4601,4609,4607,4597,4604," +
                         "4584,4580,4538,4561,4565,4566,4570,4575,4524,4450,4466,4495,4499," +
                         "4481,4493,4504,4421,4326,4290,4316,4326,4229,4048,4088,4114,4129," +
                         "4139,4155,4168,4174,4193,4199,4206,4222,4211,4210,4202,4186,4197," +
                         "4181,4165,4186,4179,4206,4220,4208,4188,3917,3738,3641,3694,3638," +
                         "3621,3631,3673,3671,3679,3716,3728,3618,3467,3427,3340,3391,3407," +
                         "3316,3253,2971,2845,2908,2938,2927,2985,3039,3014,3059,3119,3165," +
                         "3161,3176,3195,3201,3196,3210,3242,3227,3236,3234,3229,3216,3237," +
                         "3255,3273,3273,3268,3292,3311,3336,3353,3357,3297,3261,3274,3288," +
                         "3274,3208,3265,3235,3256,3253,3279,3289,3294,3289,3319,3329,3364," +
                         "3409,3416,3449,3448,3459,3453,3459,3438,3431,3445,3439,3460,3471," +
                         "3483,3506,3475,3468,3450,3461,3481,3407,3431,3388,3412,3427,3443," +
                         "3459,3474,3478,3470,3477,3480,3307,3314,3103,3095,3120,3161,3173," +
                         "3166,3184,3204,3206,3215,3210,3142,3025,2921,2990,3018,3033,3064," +
                         "3064,3054,3100,3134,3156,3183";
        String numSellsss =  "4579,4583,4581,4568,4590,4603,4610,4595,4601,4609,4607,4597,4604," ;
        //System.out.println(numSell);
        String[] numSellss = numSellsss.toString().split(",");
        int index = 0;
        while(index < numSellss.length) {
            int r = (index + 13 < numSellss.length) ? index + 13: numSellss.length;
            String[] numSells = Arrays.copyOfRange(numSellss,index,r);
            System.out.println(Arrays.toString(numSells));
            index = index + 13;
            double[][] data = new double[numSells.length][2];
            for (int i = 0; i < numSells.length; i++) {
                data[i][0] = i;
                data[i][1] = Double.parseDouble(numSells[i]);
            }
            System.out.println(DescriptiveStatLib.computeReg(data));
        }*/

     }
};
