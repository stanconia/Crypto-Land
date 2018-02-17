package org.personal.crypto_watcher.db.postgres.db_helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.util.postgres.PostgresDataType;
import org.personal.crypto_watcher.controller.trade.model.CoinProp;
import org.personal.crypto_watcher.model.RawTradable;
import org.personal.crypto_watcher.model.Tradable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class CurrPropInterface {

    private static CurrPropInterface obj;
    private DSLContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrPropInterface.class);

    private CurrPropInterface(DSLContext context){

        this.context = context;
    }

    public static CurrPropInterface get(DSLContext dslContext){

        if(obj == null){
            obj = new CurrPropInterface(dslContext);
        }
        return obj;
    }

    public static String getDBName(){

        return "coin_prop";
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


    private List<String> getTableNames(){

        List<String> tblNames = context.selectFrom(table("information_schema.tables"))
                .where("table_schema = 'public'")
                .fetch().stream()
                .map(record -> record.get(field("table_name",PostgresDataType.TEXT)))
                .collect(Collectors.toList());
        return tblNames;
    }



    public void persistCoinProp(CoinProp coinProp) {
    }
}
