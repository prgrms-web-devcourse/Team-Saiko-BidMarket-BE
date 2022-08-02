package com.saiko.bidmarket.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
public class ScheduledConfig {
  @Component
  public class Scheduler {

    @Scheduled(cron = "0 * * * * *")
    public void checkAuctionState() {
    }
  }
}
