package org.personal.crypto_watcher.db.dynamoDB;


import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.personal.crypto_watcher.model.Tradable;
import org.personal.crypto_watcher.model.BestPerformers;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

 class DynamoDbInterface {

    private static AmazonDynamoDB client;
    private static DynamoDB dynamoDB;
    private static DynamoDBMapper mapper;
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    static{
        client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
        /*client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("dynamodb.us-east-1.amazonaws.com", "us-east-1"))
                .build();*/
        mapper = new DynamoDBMapper(client);
        dynamoDB = new DynamoDB(client);
    }

    public static void persistTradables(List<Tradable> tradables) {

        mapper.batchSave(tradables);
    }

    public static void persistTopPerformers(BestPerformers bestPerformers){

        mapper.save(bestPerformers);
    }

    public static List<Tradable> getTradable(String mrktName, String time){

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(mrktName));
        eav.put(":v2",new AttributeValue().withS(time));

        DynamoDBQueryExpression<Tradable> queryExpression = new DynamoDBQueryExpression<Tradable>()
                .withKeyConditionExpression("symbol = :v1 and inst_time > :v2")
                .withExpressionAttributeValues(eav);

        return mapper.query(Tradable.class, queryExpression);
    }

    public static List<BestPerformers> getTopPerformer(String date,String time){

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(date));
        eav.put(":v2",new AttributeValue().withS(time));

        DynamoDBQueryExpression<BestPerformers> queryExpression = new DynamoDBQueryExpression<BestPerformers>()
                .withKeyConditionExpression("inst_date = :v1 and inst_time > :v2")
                .withExpressionAttributeValues(eav);

        return mapper.query(BestPerformers.class, queryExpression);
    }

    public static List<Tradable> getTradables(String time, List<String> symbols ){

        /*List<KeyPair> keyPairList = new ArrayList<>();
        for(String symbol : symbols){

            KeyPair keyPair = new KeyPair();
            keyPair.withHashKey(time);
            keyPair.withRangeKey(symbol);
            keyPairList.add(keyPair);
        }

        Map<Class<?>, List<KeyPair>> keyPairForTable = new HashMap<>();
        keyPairForTable.put(Tradable.class, keyPairList);

        Map<String, List<Object>> batchResults = mapper.batchLoad(keyPairForTable);

        return batchResults.getCoinProp("Tradables").stream()
                .map(tradable -> (Tradable)tradable)
                //.sorted(Comparator.comparing(Tradable::getId).reversed())
                .collect(Collectors.toList());*/
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(time));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("begins_with(inst_time,:v1)")
                .withExpressionAttributeValues(eav);

        return mapper.scan(Tradable.class, scanExpression);
    }

    public static Map<String,List<Tradable>> getTradables(List<String> times){

        List<Callable<Map<String,List<Tradable>>>> callables = new ArrayList<>();
        for(String time: times){
            callables.add(() -> doTask(time));
        }

        Map<String,List<Tradable>> dynamoTradables = new HashMap<>();

        try {
            List<Map<String, List<Tradable>>> v = executor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        }
                        catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }).collect(Collectors.toList());
            v.stream().forEach(res -> res.entrySet().stream().forEach(a -> dynamoTradables.put(a.getKey(),a.getValue())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dynamoTradables;
    }

    private static Map<String,List<Tradable>> doTask(String time) {

        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(time));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("begins_with(inst_time,:v1)")
                .withExpressionAttributeValues(eav);
        Map<String,List<Tradable>> result = new HashMap<>();
        result.put(time,new DynamoDBMapper(client).scan(Tradable.class, scanExpression));
        return result;
    }

    public static List<Tradable> scanTradables(){

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS("10/10/2017 20:42"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("inst_time = :val1").withExpressionAttributeValues(eav);

        return mapper.scan(Tradable.class, scanExpression);
    }

    public static void createTable(String tableName,String hashKey,String rangeKey){

        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement(hashKey, KeyType.HASH), // Partition
                            // key
                            new KeySchemaElement(rangeKey, KeyType.RANGE)), // Sort key
                    Arrays.asList(new AttributeDefinition(hashKey, ScalarAttributeType.S),
                            new AttributeDefinition(rangeKey, ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }



    public static void deleteTable(String tableName){

        Table table = dynamoDB.getTable(tableName);
        table.delete();
    }

    public static void deleteTable(List<Tradable> tradables){

        mapper.batchDelete(tradables);
    }


}
