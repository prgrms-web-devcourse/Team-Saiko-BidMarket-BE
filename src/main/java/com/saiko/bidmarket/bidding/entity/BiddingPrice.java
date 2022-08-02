package com.saiko.bidmarket.bidding.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class BiddingPrice {

  private static final long MIN_AMOUNT = 1_000L;

  private static final long UNIT_AMOUNT = 100L;

  private final long value;

  public BiddingPrice(long value) {
    validateMinAmount(value);
    validateUnitAmount(value);
    this.value = value;
  }

  private void validateMinAmount(long biddingPrice) {
    if (biddingPrice < MIN_AMOUNT) {
      throw new IllegalArgumentException();
    }
  }

  private void validateUnitAmount(long biddingPrice) {
    if (biddingPrice % UNIT_AMOUNT != 0) {
      throw new IllegalArgumentException();
    }
  }
}
