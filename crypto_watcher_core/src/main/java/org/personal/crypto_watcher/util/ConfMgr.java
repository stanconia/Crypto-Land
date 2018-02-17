package org.personal.crypto_watcher.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfMgr {

    private static final Config appConfig = ConfigFactory.load().getConfig("app");

    public static String getDBProp(String key){

        Config dbConfig = appConfig.getConfig("db");
        return dbConfig.getString(key);
    }

    public static String getAppProp(String key){

        Config appPropConf = appConfig.getConfig("props");
        return appPropConf.getString(key);
    }

}
