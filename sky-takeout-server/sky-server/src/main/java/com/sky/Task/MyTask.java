package com.sky.Task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyTask {

    // @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask() {
        System.out.println("Executing task every 5 seconds");
    }
}
