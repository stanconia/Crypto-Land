package org.personal.crypto_watcher.controller.scheduler;


import com.yammer.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedPeriodicTask implements Managed {

    private final Logger LOGGER = LoggerFactory.getLogger(ManagedPeriodicTask.class);
    private final ScheduledTaskMgr taskMgr;

    public ManagedPeriodicTask(ScheduledTaskMgr taskMgr) {
        this.taskMgr = taskMgr;
    }

    @Override
    public void start() throws Exception {
        taskMgr.start();
    }

    @Override
    public void stop() throws Exception {
        taskMgr.stop();
    }
}
