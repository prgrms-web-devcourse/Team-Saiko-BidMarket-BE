package com.saiko.bidmarket.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.notification.entity.Notification;

public interface NotificationRepository
    extends NotificationCustomRepository, JpaRepository<Notification, Long> {
}
