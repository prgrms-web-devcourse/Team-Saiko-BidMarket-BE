package com.saiko.bidmarket.notification.event;

import org.springframework.util.Assert;

import com.saiko.bidmarket.notification.NotificationType;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationCreateEvent {
  private final User user;

  private final NotificationType notificationType;

  private final Product product;

  @Builder
  private NotificationCreateEvent(User user, NotificationType notificationType, Product product) {
    Assert.notNull(user, "User must be provided");
    Assert.notNull(notificationType, "NotificationType must be provided");
    Assert.notNull(product, "Product must be provided");

    this.user = user;
    this.notificationType = notificationType;
    this.product = product;
  }
}
