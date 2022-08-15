package com.saiko.bidmarket.notification.repository;

import java.util.List;

import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.repository.dto.NotificationRepoDto;

public interface NotificationCustomRepository {
  List<NotificationRepoDto> findAllNotification(
      long userId,
      NotificationSelectRequest request
  );
}
