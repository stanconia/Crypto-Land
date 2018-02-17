package org.personal.crypto_watcher.controller.scheduler;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.personal.crypto_watcher.util.TimeMgr;
import org.personal.crypto_watcher.controller.mrkt_cap.MrktCapMgr;
import org.personal.crypto_watcher.controller.data_pipeline.TradableMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class OneMinTasks extends AbstractScheduledService {

    private final Logger LOGGER = LoggerFactory.getLogger(OneMinTasks.class);
    private MrktCapMgr mgr;
    private TradableMgr tradableMgr;

    public OneMinTasks(){

        //this.mgr = new MrktCapMgr();
        this.tradableMgr = new TradableMgr();
    }

    @Override
    protected void runOneIteration() throws Exception {

        try {
            TimeMgr.setTime();
            LOGGER.info("Ran one iteration of main command");
            //mgr.run();
            tradableMgr.runTasks();
            LOGGER.info("Finished");
        }catch (Exception e){
            LOGGER.info(e.getMessage());
        }
    }

    @Override
    protected AbstractScheduledService.Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedRateSchedule(0, 1, TimeUnit.MINUTES);
    }
}
