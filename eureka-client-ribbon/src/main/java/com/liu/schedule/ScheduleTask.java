package com.liu.schedule;

import com.liu.utils.DateUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

    @Scheduled(fixedRate = 1000)
    public void task(){
        System.out.println(DateUtil.Time());
    }
}
