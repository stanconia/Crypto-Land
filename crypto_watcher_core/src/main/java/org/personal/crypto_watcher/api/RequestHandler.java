package org.personal.crypto_watcher.api;


import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.jrestless.aws.gateway.GatewayFeature;
import com.jrestless.aws.gateway.handler.GatewayRequestObjectHandler;


public class RequestHandler extends GatewayRequestObjectHandler {
    public RequestHandler() {
        ResourceConfig config = new ResourceConfig(MultiPartFeature.class)
                .register(GatewayFeature.class)
                .register(MultiPartFeature.class)
                .packages("org.personal.crypto_watcher.api");
        init(config);
        start();
    }
}
