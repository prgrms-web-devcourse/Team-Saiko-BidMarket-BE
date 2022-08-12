package com.saiko.bidmarket.common.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.saiko.bidmarket.chat.service.ChatRoomService;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.service.ProductService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ScheduledConfig {

  private final ProductService productService;
  private final ChatRoomService chatRoomService;

  @Component
  public class Scheduler {

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void closeProduct() {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime nowTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                                             now.getHour(), now.getMinute());

      List<Product> productsInProgress = productService.findAllThatNeedToClose(nowTime);

      productsInProgress.forEach(productService::executeClosingProduct);
      productsInProgress.forEach(chatRoomService::create);
    }
  }
}
