package com.saiko.bidmarket.notification.repository.dto;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;
import com.saiko.bidmarket.notification.NotificationType;

import lombok.Getter;

@Getter
public class NotificationRepoDto {
  private long id;

  private long productId;

  private String title;

  private String thumbnailImage;

  private NotificationType type;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @QueryProjection
  public NotificationRepoDto(long id, long productId, String title, String thumbnailImage,
                             NotificationType type, LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
    this.id = id;
    this.productId = productId;
    this.title = title;
    this.thumbnailImage = thumbnailImage;
    this.type = type;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
