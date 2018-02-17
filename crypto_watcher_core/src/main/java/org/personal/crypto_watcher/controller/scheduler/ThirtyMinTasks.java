package org.personal.crypto_watcher.controller.scheduler;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.personal.crypto_watcher.util.TimeMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ThirtyMinTasks extends AbstractScheduledService {

    private final Logger LOGGER = LoggerFactory.getLogger(ThirtyMinTasks.class);

    public ThirtyMinTasks(){

    }

    @Override
    protected void runOneIteration() throws Exception {

        TimeMgr.setTime();
        LOGGER.info("Ran one iteration of main command");

        LOGGER.info("Finished");
    }

    @Override
    protected AbstractScheduledService.Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedRateSchedule(0, 30, TimeUnit.MINUTES);
    }


}
