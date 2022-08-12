package com.saiko.bidmarket.notification.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;
import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.NotificationRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Transactional
public class DefaultNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

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

  @Override
  public void checkNotification(
      long userId,
      long id
  ) {
    Assert.isTrue(userId > 0, "UserId must be positive");
    Assert.isTrue(id > 0, "NotificationId must be positive");

    Notification notification = notificationRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Notification not exist"));

    if (notification.isNotPossibleToAccessNotification(userId)) {
      throw new AuthorizationServiceException("다른 유저의 알림을 확인할 수 없습니다.");
    }

    notification.check();
  }
}
