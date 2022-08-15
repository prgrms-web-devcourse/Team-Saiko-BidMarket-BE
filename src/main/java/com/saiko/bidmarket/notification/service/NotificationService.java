package com.saiko.bidmarket.notification.service;

import java.util.List;

import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;

public interface NotificationService {
  List<NotificationSelectResponse> findAllNotifications(
      long userId,
      NotificationSelectRequest request
  );

  void checkNotification(
      long userId,
      long id
  );
}
