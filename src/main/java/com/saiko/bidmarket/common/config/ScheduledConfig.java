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
      LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                                             now.getHour(), now.getMinute());

      LocalDateTime end;
      if (now.getMinute() != 59) {
        end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                               now.getHour(), now.getMinute() + 1);
      } else {
        end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                               now.getHour() + 1, 0);
      }

      List<Product> productsInProgress = productService.findAllThatNeedToClose(start, end);
      if (productsInProgress.size() != 0) {
        productService.executeClosingProduct(productsInProgress);
      }
    }
  }
}
