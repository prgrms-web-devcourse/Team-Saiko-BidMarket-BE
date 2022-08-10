package com.saiko.bidmarket.notification.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;

@Getter
public class NotificationSelectRequest {
  @PositiveOrZero
  private final long offset;

  @Positive
  private final int limit;

  public NotificationSelectRequest(long offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }
}
