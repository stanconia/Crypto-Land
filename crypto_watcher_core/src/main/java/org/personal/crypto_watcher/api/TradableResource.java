package org.personal.crypto_watcher.api;

import com.google.common.base.Optional;
import org.personal.crypto_watcher.controller.metrics.common.TradableLib;

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


}
