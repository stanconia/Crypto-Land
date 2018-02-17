package org.personal.crypto_watcher.api;

import com.google.common.base.Optional;
import org.personal.crypto_watcher.controller.trade.TradeMgr;
import org.personal.crypto_watcher.controller.trade.watch.CurrencyAlert;
import org.personal.crypto_watcher.controller.trade.watch.WatchMgr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/watch")
@Produces(MediaType.APPLICATION_JSON)
public class WatchResource {

    private static final int DEFAULT_VAL = 0 ;

    @GET
    @Path("/add")
    public String addAlert(@QueryParam("symbol") String symbol,
                              @QueryParam("alert_key") String alertKey,
                              @QueryParam("alert_value") Double alertValue,
                              @QueryParam("alert_type") String alertType,
                              @QueryParam("overide") Optional<Boolean> canOveride){

        WatchMgr.add(symbol,alertKey,alertValue,alertType,canOveride.or(true));
        return "Done!";
    }

    @GET
    @Path("/remove")
    public String removeAlert(@QueryParam("symbol") String symbol,
                              @QueryParam("alert_key") String alertKey){

        WatchMgr.remove(symbol,alertKey);
        return "Done!";
    }

    @GET
    @Path("/get")
    public Map<String,CurrencyAlert> getAlerts(){

        return WatchMgr.getAlerts();
    }

    @GET
    @Path("/trade")
    public String turnOnTrade(@QueryParam("turn-on") boolean isOn){

        //TradeMgr.setIsOn(isOn);
        return "String";
    }



}
