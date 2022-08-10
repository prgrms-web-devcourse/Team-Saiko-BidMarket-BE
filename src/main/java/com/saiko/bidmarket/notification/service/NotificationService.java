package com.saiko.bidmarket.notification.service;

import java.util.List;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;

public interface NotificationService {
  List<NotificationSelectResponse> findAllNotifications(
      UnsignedLong userId,
      NotificationSelectRequest request
  );
}
