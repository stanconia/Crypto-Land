package org.personal.crypto_watcher.api;


import com.google.common.base.Optional;
import org.personal.crypto_watcher.model.MarketCap;
import org.personal.crypto_watcher.db.postgres.PostgresInterface;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/market-cap")
@Produces(MediaType.APPLICATION_JSON)
public class MarketCapResource {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public MarketCapResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    public MarketCap sayHello(@QueryParam("name") Optional<String> name) {

        return null;
                //PostgresInterface.getLatestMarketCap();
                //String.format(template, name.or(defaultName)));
    }
}
