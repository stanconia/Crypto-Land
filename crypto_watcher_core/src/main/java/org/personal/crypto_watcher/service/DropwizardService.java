package org.personal.crypto_watcher.service;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.lifecycle.Managed;
import org.personal.crypto_watcher.api.*;
import org.personal.crypto_watcher.controller.scheduler.ManagedPeriodicTask;
import org.personal.crypto_watcher.controller.scheduler.ScheduledTaskMgr;
import org.personal.crypto_watcher.api.MarketCapResource;
import org.personal.crypto_watcher.api.WatchResource;
import org.personal.crypto_watcher.model.ServerConfig;

public class DropwizardService extends Service<ServerConfig> {

    public static void main(String[] args) throws Exception {
        new DropwizardService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ServerConfig> bootstrap) {
        bootstrap.setName("market-cap");
    }

    @Override
    public void run(ServerConfig configuration,
                    Environment environment) {

        /*HttpConfiguration config = new HttpConfiguration();
        config.setPort(Integer.parseInt(System.getenv().get("PORT")));
        configuration.setHttpConfiguration(config);*/

        final String template = configuration.getTemplate();
        final String defaultName = configuration.getDefaultName();
        environment.addResource(new MarketCapResource(template, defaultName));
        environment.addResource(new TradableResource());
        environment.addResource(new WatchResource());

        final Managed managedImplementer = new ManagedPeriodicTask(new ScheduledTaskMgr());
        environment.manage(managedImplementer);
    }
}