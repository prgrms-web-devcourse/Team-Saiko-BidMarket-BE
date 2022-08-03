package com.saiko.bidmarket.user.controller.dto;

import static com.saiko.bidmarket.product.Sort.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.saiko.bidmarket.product.Sort;

import lombok.Getter;

@Getter
public class UserBiddingSelectRequest {
  @PositiveOrZero
  private final int offset;

  @Positive
  private final int limit;

  private final Sort sort;

  public UserBiddingSelectRequest(int offset, int limit, Sort sort) {
    this.offset = offset;
    this.limit = limit;
    this.sort = sort != null ? sort : END_DATE_ASC;
  }
}
