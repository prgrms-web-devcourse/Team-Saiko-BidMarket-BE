package com.saiko.bidmarket.user.controller.dto;

import static com.saiko.bidmarket.common.Sort.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.saiko.bidmarket.common.Sort;

import lombok.Getter;

@Getter
public class UserHeartSelectRequest {
  @PositiveOrZero
  private final long offset;

  @Positive
  private final int limit;

  private final Sort sort;

  public UserHeartSelectRequest(long offset, int limit, Sort sort) {
    this.offset = offset;
    this.limit = limit;
    this.sort = sort != null ? sort : END_DATE_ASC;
  }
}
