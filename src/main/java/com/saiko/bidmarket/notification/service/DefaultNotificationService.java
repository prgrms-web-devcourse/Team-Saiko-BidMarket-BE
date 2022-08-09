package com.saiko.bidmarket.notification.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;
import com.saiko.bidmarket.notification.repository.NotificationRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class DefaultNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Override
  public List<NotificationSelectResponse> findAllNotifications(UnsignedLong userId,
                                                               NotificationSelectRequest request) {
    Assert.notNull(userId, "UserId must be provided");
    Assert.notNull(request, "Request must be provided");

    return notificationRepository.findAllNotification(userId, request)
                            .stream()
                            .map((NotificationSelectResponse::from))
                            .collect(Collectors.toList());
  }
}
