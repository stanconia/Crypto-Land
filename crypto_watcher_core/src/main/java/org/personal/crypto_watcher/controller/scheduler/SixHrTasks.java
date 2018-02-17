package org.personal.crypto_watcher.controller.scheduler;


import com.google.common.util.concurrent.AbstractScheduledService;
import org.personal.crypto_watcher.controller.metrics.stable_metrics.creator.StableCurrencyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class SixHrTasks extends AbstractScheduledService {


    private final Logger LOGGER = LoggerFactory.getLogger(SixHrTasks.class);
    public static final String WINDOW = "12HR";

    @Override
    protected void runOneIteration() throws Exception {

        try {
            LOGGER.info("Running 12 Hr tasks....");
            StableCurrencyFactory stableCurrencyFactory = new StableCurrencyFactory(WINDOW);
            stableCurrencyFactory.runTasks();
            LOGGER.info("Finished");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    protected AbstractScheduledService.Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedRateSchedule(0, 6, TimeUnit.HOURS);
    }


}
