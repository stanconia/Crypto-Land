package org.personal.crypto_watcher.db.postgres.db_helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDataType;
import org.personal.crypto_watcher.db.postgres.PostgresDBInterface;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.model.Tradable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class DataInterface {

    private static DataInterface obj;
    private  DSLContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInterface.class);

    private DataInterface(DSLContext context){

        this.context = context;
    }

    public static DataInterface get(DSLContext dslContext){

        if(obj == null){
            obj = new DataInterface(dslContext);
        }
        return obj;
    }

    public static String getDBName(){

        return "data";
    }


    private void createTable(String tblName){

        context.createTable(table(tblName))
                .column(field("time", PostgresDataType.TEXT), PostgresDataType.TEXT)
                .column(field("price",PostgresDataType.FLOAT8), PostgresDataType.FLOAT8)
                .column(field("volume",PostgresDataType.INT), PostgresDataType.INT)
                .column(field("num_buys",PostgresDataType.INT), PostgresDataType.INT)
                .column(field("num_sells",PostgresDataType.INT), PostgresDataType.INT)
                .column(field("metrics",PostgresDataType.TEXT), PostgresDataType.TEXT)
                .constraints(
                        constraint("PK_" + tblName).primaryKey(field("time")))
                .execute();
    }


    public void persistTradables(List<Tradable> bittrexTradables, String time) {

        List<String> existingTblNames = getTableNames();
        bittrexTradables.forEach(tradable -> {
            try {
                String tblName = "\"" + tradable.getMarketName() + "\"";
                ObjectMapper mapper = new ObjectMapper();
                String metricJson = mapper.writeValueAsString(tradable.getMetricMap());
                if (!existingTblNames.contains(tradable.getMarketName())) createTable(tblName);
                    context.insertInto(table(tblName),
                        field("time"), field("price"), field("volume"),
                        field("num_buys"), field("num_sells"), field("metrics"))
                        .values(time, tradable.getPrice(), tradable.getVolume(), tradable.getNumBuyOrders(),
                                tradable.getNumSellOrders(), metricJson)
                        .execute();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    private List<String> getTableNames(){

        List<String> tblNames = context.selectFrom(table("information_schema.tables"))
                .where("table_schema = 'public'")
                .fetch().stream()
                .map(record -> record.get(field("table_name",PostgresDataType.TEXT)))
                .filter(tblName -> !tblName.startsWith("pg_"))
                .collect(Collectors.toList());
        return tblNames;
    }

    public List<Tradable> getTradables(String time){

        List<String> cusips = getTableNames();
        List<Tradable> tradables =  cusips.stream().map(cusip -> getTradable(cusip,time))
                .filter(tradable -> tradable != null)
                .collect(Collectors.toList());
        return (tradables == null) ? new ArrayList<>() : tradables;
    }


    public Tradable getTradable(String cusip, String time)  {

        Record record = context.selectFrom(table("\"" + cusip + "\""))
                .where("time = '" + time + "'")
                .fetchOne();
        if(record != null) {
            RawTradable rawTradable = new RawTradable(
                    cusip,
                    record.get(field("price", PostgresDataType.DOUBLEPRECISION)),
                    record.get(field("volume", PostgresDataType.INT)),
                    record.get(field("num_buys", PostgresDataType.INT)),
                    record.get(field("num_sells", PostgresDataType.INT)));
            Tradable tradable = new Tradable(rawTradable, time);
            TypeReference<HashMap<String, Tradable.Metrics>> typeRef
                    = new TypeReference<HashMap<String, Tradable.Metrics>>() {
            };
            try {
                String metrics = record.get(field("metrics", PostgresDataType.TEXT));
                tradable.setMetricMap(
                        new ObjectMapper().readValue(metrics, typeRef));
            } catch (IOException e) {
                e.printStackTrace();
                tradable.setMetricMap(new HashMap<>());
            }

            return tradable;
        }else{
            return null;
        }
    }

    public void deleteData(String symbol, String time) {

        context.deleteFrom(table("\"" + symbol + "\""))
                .where("time < '" + time + "'").execute();
    }
}
