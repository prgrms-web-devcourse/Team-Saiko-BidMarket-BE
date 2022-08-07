package com.saiko.bidmarket.notification.service;


import com.saiko.bidmarket.notification.NotificationType;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

public interface NotificationService {
  void create(User user, NotificationType messageType, Product product);
}
