package org.personal.crypto_watcher.db;

import org.personal.crypto_watcher.db.mongodb.MongoDBInterface;
import org.personal.crypto_watcher.db.postgres.PostgresDBInterface;

public class DBMgr {

    private static DBMgr dbMgr;
    private final static String POSTGRES_INTERFACE = "postgres";
    private final static String MONGO_INTERFACE = "mongo";
    private DBInterface dbInterface;

    private DBMgr(){

    }

    public static DBMgr getDBMgr(){

        if(dbMgr == null){
            dbMgr = new DBMgr();
        }
        return dbMgr;
    }

    public DBInterface getInterface(){

        return getInterface(MONGO_INTERFACE);
    }

    public DBInterface getInterface(String interfaceType){

        if(dbInterface == null) {
            dbInterface = new MongoDBInterface();
            switch (interfaceType) {
                case POSTGRES_INTERFACE:
                    return dbInterface;
                case MONGO_INTERFACE:
                    return new MongoDBInterface();
                default:
                    return dbInterface;
            }
        }else{
            return dbInterface;
        }
    }
}
