package com.saiko.bidmarket.common.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.service.ProductService;

@Configuration
@EnableScheduling
public class ScheduledConfig {

  private final ProductService productService;

  public ScheduledConfig(ProductService productService) {
    this.productService = productService;
  }

  @Component
  public class Scheduler {

    @Scheduled(cron = "0 * * * * *")
    public void closeProduct() {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime nowTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                                             now.getHour(), now.getMinute());

      List<Product> productsInProgress = productService.findAllThatNeedToClose(nowTime);
      if (productsInProgress.size() != 0) {
        productService.executeClosingProduct(productsInProgress);
      }
    }
  }
}
