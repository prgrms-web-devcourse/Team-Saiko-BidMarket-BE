package com.saiko.bidmarket.notification.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;

@Getter
public class NotificationSelectRequest {
  @PositiveOrZero
  private final int offset;

  @Positive
  private final int limit;

  public NotificationSelectRequest(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }
}
