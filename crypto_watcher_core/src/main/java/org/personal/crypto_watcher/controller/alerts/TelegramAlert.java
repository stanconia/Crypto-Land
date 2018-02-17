package org.personal.crypto_watcher.controller.alerts;


import org.apache.log4j.Logger;
import org.personal.crypto_watcher.service.RestService;

public class TelegramAlert {

    private static Logger logger = Logger.getLogger(TelegramAlert.class);
    private static final String BASE_URL= "https://api.telegram.org/bot";
    private static final String TOKEN = "405626746:AAGZRa7N12kXxZ0qlGSZH7hWzLgJaE0l_l8";
    public static final String CHAT_ID = "@stanwizzy";
    public static final String VOL_CHAT_ID = "@volumemonitor";
    public static final String MRKT_EVT_ID = "@marketevent";
    public static final String TRADE_EVT_ID = "@tradeevent";


    //405626746:AAGZRa7N12kXxZ0qlGSZH7hWzLgJaE0l_l8/sendMessage?chat_id=429697653&text=Hello+World

    public static void notify(String msg,String chatId){

        try{
            String url = BASE_URL + TOKEN + "/sendMessage?chat_id=" + chatId + "&text=" + msg;
            String env = System.getProperty("env");
            //if(env == null){
                String response = RestService.get(url);
                logger.info(response);
            //}
        }catch (Exception e){
            logger.error(e.getMessage());
        }

    }
}
