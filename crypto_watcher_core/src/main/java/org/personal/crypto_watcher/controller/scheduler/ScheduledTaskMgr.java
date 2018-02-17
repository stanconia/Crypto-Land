package org.personal.crypto_watcher.controller.scheduler;


import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import java.util.HashSet;
import java.util.Set;

public class ScheduledTaskMgr  {

    private ServiceManager serviceManager;

    public ScheduledTaskMgr(){

        Set<Service> services = new HashSet<>();
        services.add(new OneMinTasks());
        services.add(new SixHrTasks());
        serviceManager = new ServiceManager(services);
    }

    public void start(){

        try {
            serviceManager.startAsync(); //.awaitHealthy(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void stop(){
        try {
            serviceManager.stopAsync();//.awaitStopped(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
