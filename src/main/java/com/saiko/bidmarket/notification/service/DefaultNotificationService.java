package com.saiko.bidmarket.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.notification.NotificationType;
import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.NotificationRepository;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

@Service
public class DefaultNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;

  public DefaultNotificationService(
      NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional
  @Override
  public void create(User user, NotificationType notificationType, Product product) {
    Assert.notNull(user, "User must be provided");
    Assert.notNull(notificationType, "NotificationType must be provided");
    Assert.notNull(product, "Product must be provided");

    Notification notification = Notification.builder()
        .content(notificationType.getMessage())
        .product(product)
        .user(user)
        .build();

    notificationRepository.save(notification);
  }
}
