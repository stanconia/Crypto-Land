package org.personal.crypto_watcher.db.postgres.db_helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.util.postgres.PostgresDataType;
import org.personal.crypto_watcher.model.MarketCap;
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

public class MarketCapInterface {

    private static MarketCapInterface obj;
    private DSLContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketCapInterface.class);

    private MarketCapInterface(DSLContext context){

        this.context = context;
    }

    public static MarketCapInterface get(DSLContext dslContext){

        if(obj == null){
            obj = new MarketCapInterface(dslContext);
        }
        return obj;
    }

    public static String getDBName(){

        return "market_cap";
    }

    public MarketCap getMarketCap(String time) {

        Record record = context.selectFrom(table("\"market_cap\""))
                .where("time = '" + time + "'")
                .fetchOne();
        if(record != null){
            MarketCap mrktCap = new MarketCap();
            mrktCap.setTime(record.get(field("time",PostgresDataType.TEXT)));
            mrktCap.setTotal_market_cap_usd(record.get(field("cap",PostgresDataType.OID)));
            mrktCap.setTotal_24h_volume_usd(record.get(field("volume",PostgresDataType.OID)));
            return mrktCap;
        }else{
            return null;
        }
    }

    public void putMarketCap(MarketCap mrktCap) {

        context.insertInto(table("\"market_cap\""),
                field("time"), field("cap"), field("volume"))
                .values(mrktCap.getTime(), mrktCap.getTotal_market_cap_usd(),mrktCap.getTotal_24h_volume_usd())
                .execute();
    }
}
