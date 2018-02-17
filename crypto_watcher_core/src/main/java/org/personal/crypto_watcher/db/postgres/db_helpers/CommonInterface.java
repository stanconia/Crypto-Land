package org.personal.crypto_watcher.db.postgres.db_helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.util.postgres.PostgresDataType;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.model.Tradable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class CommonInterface {

    private static CommonInterface obj;
    private DSLContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonInterface.class);

    private CommonInterface(DSLContext context){

        this.context = context;
    }

    public static CommonInterface get(DSLContext dslContext){

        if(obj == null){
            obj = new CommonInterface(dslContext);
        }
        return obj;
    }

    public static String getDBName(){

        return "common";
    }


    public void persistStableCurr(List<String> stableCurrs, String timeInterval){

        String joinStr = stableCurrs.stream().collect(Collectors.joining(","));
        context.insertInto(table("\"stable_curr\""),
                field("time_interval"), field("currencies"))
                .values(timeInterval,joinStr)
                .execute();
    }

    public List<String> getStableCurr(String timeInterval) {

            Record record = context.select(field("currencies"))
                    .from(table("\"stable_curr\""))
                    .where("time_interval = '" + timeInterval + "'")
                    .fetchOne();
            if (record != null) {
                String result = record.get(field("currencies", PostgresDataType.TEXT));
                return (!result.isEmpty()) ? Arrays.asList(result.split(",")) : new ArrayList<>();

            }
        return new ArrayList<>();
    }

    public void persistNumSellMean(Map<String, Double> numSellMean, String window) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            context.update(table("\"num_sell_mean\""))
                    .set(field("means"),mapper.writeValueAsString(numSellMean))
                    .where(field("time_window").eq(window))
                    //.onDuplicateKeyUpdate()
                    //.set(field("means"), mapper.writeValueAsString(numSellMean))
                    .execute();
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }

    }

    public Map<String,Double> getNumSellMeans(String window) {

        try {
            Record record = context.select(field("means"))
                .from(table("\"num_sell_mean\""))
                .where("time_window = '" + window + "'")
                .fetchOne();
            if(record != null){
                ObjectMapper mapper = new ObjectMapper();
                String result = record.get(field("means", PostgresDataType.TEXT));
                TypeReference<HashMap<String, Double>> typeRef
                    = new TypeReference<HashMap<String, Double>>() {};
                return mapper.readValue(result, typeRef);}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public void updateLatestID(String timeIDs, String window) {

        context.update(table("\"current_stable_ids\""))
                .set(field("ids"),timeIDs)
                .where(field("time_window").eq(window))
                .execute();
    }

    public String getLatestStableCurrIDs(String window) {

        Record record = context.select(field("ids"))
                .from(table("\"current_stable_ids\""))
                .where("time_window = '" + window + "'")
                .fetchOne();
        if(record == null){
            return "";
        }else{
            return record.get(field("ids",PostgresDataType.TEXT));
        }
    }
}
