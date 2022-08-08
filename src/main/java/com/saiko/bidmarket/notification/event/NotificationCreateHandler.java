package com.saiko.bidmarket.notification.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.NotificationRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class NotificationCreateHandler {
  private final NotificationRepository notificationRepository;

  @EventListener
  public void create(NotificationCreateEvent event) {
    notificationRepository.save(Notification.builder()
                                    .content(event.getNotificationType().getMessage())
                                    .product(event.getProduct())
                                    .user(event.getUser())
                                    .build());
  }
}
