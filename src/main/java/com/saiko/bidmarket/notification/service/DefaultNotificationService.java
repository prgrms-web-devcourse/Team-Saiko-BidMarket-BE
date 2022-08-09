package com.saiko.bidmarket.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;

@Service
public class DefaultNotificationService implements NotificationService {

  @Override
  public List<NotificationSelectResponse> findAllNotifications(UnsignedLong userId,
                                                               NotificationSelectRequest request) {
    return null;
  }
}
