package com.saiko.bidmarket.product;

import static org.awaitility.Awaitility.*;
import static org.mockito.BDDMockito.atLeast;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.saiko.bidmarket.common.config.ScheduledConfig;
import com.saiko.bidmarket.common.config.ScheduledConfig.Scheduler;

@SpringJUnitConfig(ScheduledConfig.class)
public class SchedulerTest {
  @SpyBean
  private Scheduler scheduler;

  @Nested
  @DisplayName("스케줄러는")
  class DescribeScheduler {

    @Test
    @DisplayName("1분마다 CheckAuctionState 메서드를 호출한다")
    void ItCallCheckAuctionStateMethodEveryMinute() {
      // given

      // when, then
      await()
          .atMost(Duration.ofMinutes(1))
          .untilAsserted(() -> verify(scheduler, atLeast(1)).checkAuctionState());
    }
  }
}
