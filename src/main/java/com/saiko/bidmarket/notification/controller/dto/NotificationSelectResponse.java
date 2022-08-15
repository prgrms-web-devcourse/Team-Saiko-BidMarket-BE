package com.saiko.bidmarket.notification.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.notification.repository.dto.NotificationRepoDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationSelectResponse {
  private final long id;

  private final long productId;

  private final String title;

  private final String thumbnailImage;

  private final String type;

  private final String content;

  private final boolean checked;

  private final LocalDateTime createdAt;

  private final LocalDateTime updatedAt;

  public static NotificationSelectResponse from(NotificationRepoDto notificationRepoDto) {
    return NotificationSelectResponse
        .builder()
        .id(notificationRepoDto.getId())
        .productId(notificationRepoDto.getProductId())
        .title(notificationRepoDto.getTitle())
        .thumbnailImage(notificationRepoDto.getThumbnailImage())
        .type(notificationRepoDto
                  .getType()
                  .getType())
        .content(notificationRepoDto
                     .getType()
                     .getMessage())
        .checked(notificationRepoDto.isChecked())
        .createdAt(notificationRepoDto.getCreatedAt())
        .updatedAt(notificationRepoDto.getUpdatedAt())
        .build();
  }
}
