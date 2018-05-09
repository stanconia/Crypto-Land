package org.personal.crypto_watcher.db.postgres.db_helpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.util.postgres.PostgresDataType;
import org.personal.crypto_watcher.model.CurrCap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class GlobalCurrInterface {

    private static GlobalCurrInterface obj;
    private DSLContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyStatInterface.class);

    private GlobalCurrInterface(DSLContext context){

        this.context = context;
    }

    public static GlobalCurrInterface get(DSLContext dslContext){

        if(obj == null){
            obj = new GlobalCurrInterface(dslContext);
        }
        return obj;
    }

    public static String getDBName(){

        return "global_curr";
    }

    public void persistCurr(List<CurrCap> currs) {

        try {
            currs = filterDuplicates(currs);
            String content = createMap(currs);
            updateTable(content);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private List<CurrCap> filterDuplicates(List<CurrCap> currs) {

        Set<String> set = new HashSet<>();
        return currs.stream().filter(curr -> {
            if(set.contains(curr.getSymbol())) {
                return true; }
            else{
                set.add(curr.getSymbol());
                return false; }
        }).collect(Collectors.toList());
    }

    private String createMap(List<CurrCap> currs) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> valueMap = currs.stream().collect(Collectors.toMap(CurrCap::getSymbol,(currCap) -> {
            try {
                return mapper.writeValueAsString(currCap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "";
            }
        }));
        return mapper.writeValueAsString(valueMap);
    }

    private void updateTable(String content) {

        context.update(table("global_currs"))
                .set(field("values"),content)
                .where(field("id").eq("id"))
                //.onDuplicateKeyUpdate()
                //.set(field("means"), mapper.writeValueAsString(numSellMean))
                .execute();
    }

    public List<CurrCap> getCurr() {

        try {
            Record record = context.select(field("values"))
                    .from(table("global_currs"))
                    .where("id = '" + "id" + "'")
                    .fetchOne();
            if(record != null){
                ObjectMapper mapper = new ObjectMapper();
                String result = record.get(field("values", PostgresDataType.TEXT));
                TypeReference<HashMap<String, CurrCap>> typeRef
                        = new TypeReference<HashMap<String, CurrCap>>() {};
                Map<String,CurrCap> valueMap =  mapper.readValue(result, typeRef);
                return new ArrayList<>(valueMap.values());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
