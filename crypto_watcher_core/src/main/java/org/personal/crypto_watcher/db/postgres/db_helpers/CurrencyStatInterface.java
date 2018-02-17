package org.personal.crypto_watcher.db.postgres.db_helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.util.postgres.PostgresDataType;
import org.personal.crypto_watcher.model.CurrencyStat;
import org.personal.crypto_watcher.model.Tradable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class CurrencyStatInterface {

    private static CurrencyStatInterface obj;
    private DSLContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyStatInterface.class);

    private CurrencyStatInterface(DSLContext context){

        this.context = context;
    }

    public static CurrencyStatInterface get(DSLContext dslContext){

        if(obj == null){
            obj = new CurrencyStatInterface(dslContext);
        }
        return obj;
    }

    public static String getDBName(){

        return "coin_stat";
    }


    private void createTable(String tblName){


        context.createTable(table(tblName))
                .column(field("time", PostgresDataType.TEXT), PostgresDataType.TEXT)
                .column(field("priceMean",PostgresDataType.FLOAT4), PostgresDataType.FLOAT4)
                .column(field("numBuyMean",PostgresDataType.FLOAT4), PostgresDataType.FLOAT4)
                .column(field("volumeMean",PostgresDataType.FLOAT4), PostgresDataType.FLOAT4)
                .column(field("numSellMean",PostgresDataType.FLOAT4), PostgresDataType.FLOAT4)
                .column(field("priceWidth",PostgresDataType.FLOAT4), PostgresDataType.FLOAT4)
                .column(field("numSellWidth",PostgresDataType.FLOAT4), PostgresDataType.FLOAT4)
                .column(field("numBuyWidth",PostgresDataType.FLOAT4), PostgresDataType.FLOAT4)
                .constraints(
                        constraint("PK_" + tblName).primaryKey(field("time")))
                .execute();
    }

    public void persistCurrStat(List<CurrencyStat> currStats, String time, String window) {

        List<String> existingTblNames = getTableNames();
        currStats.forEach(currStat -> { String tblName = "\"" + currStat.getSymbol() + "\"";
            if (!existingTblNames.contains(currStat.getSymbol())) createTable(tblName);
            context.insertInto(table(tblName),
                    field("time"), field("priceMean"), field("numBuyMean"),
                    field("volumeMean"), field("numSellMean"), field("priceWidth"),
                    field("numSellWidth"), field("numBuyWidth"))
                    .values(time + "_" + window, currStat.getPriceMean(), currStat.getNumBuyMean(), currStat.getVolumeMean(),
                            currStat.getNumSellMean(), currStat.getPriceWidth(),currStat.getNumSellWidth(),
                            currStat.getNumBuyWidth())
                    .execute();
        });
    }

    private List<String> getTableNames(){

        List<String> tblNames = context.selectFrom(table("information_schema.tables"))
                .where("table_schema = 'public'")
                .fetch().stream()
                .map(record -> record.get(field("table_name",PostgresDataType.TEXT)))
                .collect(Collectors.toList());
        return tblNames;
    }



    public CurrencyStat getCurrStat(String symbol, String id) {

        Record record = context.selectFrom(table("\"" + symbol + "\""))
                .where("time = '" + id + "'")
                .fetchOne();

        if(record != null) {
            return new CurrencyStat(
                    symbol,
                    record.get(field("pricemean", PostgresDataType.REAL)),
                    record.get(field("numbuymean", PostgresDataType.REAL)),
                    record.get(field("pricewidth", PostgresDataType.REAL)),
                    record.get(field("numbuywidth", PostgresDataType.REAL)),
                    record.get(field("volumemean", PostgresDataType.REAL)),
                    record.get(field("numsellmean", PostgresDataType.REAL)),
                    record.get(field("numsellwidth", PostgresDataType.REAL)));
        }else{
            return null;
        }
    }

}
