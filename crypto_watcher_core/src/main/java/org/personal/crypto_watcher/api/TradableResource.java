package org.personal.crypto_watcher.api;

import com.google.common.base.Optional;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;
import org.personal.crypto_watcher.db.DBMgr;
import org.personal.crypto_watcher.model.CurrCap;
import org.personal.crypto_watcher.service.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/tradable")
@Produces(MediaType.APPLICATION_JSON)
public class TradableResource {


    @GET
    @Path("/query")
    public List<TradableLib.QueryResult> getTradables(@QueryParam("symbol") String symbol,
                                                      @QueryParam("period") Optional<String> period,
                                                      @QueryParam("period_num") Optional<Integer> periodNum,
                                                      @QueryParam("interval") Optional<Integer> usrInterval) {
        String timePeriod = period.or("min").toLowerCase();
        int numPeriod = periodNum.or(1);
        int interval = usrInterval.or(1);
        return TradableLib.getHistoricalTradables(symbol,timePeriod,numPeriod,interval);
    }

    @GET
    @Path("/flush")
    public String flush() {

        try {
            List<CurrCap> currCaps = RestService.getCurrCaps();
            DBMgr.getDBMgr().getInterface().persistGlobalCurr(currCaps);
            return "OK!";
        }catch (Exception e){
            return e.getMessage();
        }

    }


}
