package com.saiko.bidmarket.bidding.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BiddingPriceResponse {

  private final long biddingPrice;

  public static BiddingPriceResponse from(long biddingPrice) {
    return new BiddingPriceResponse(biddingPrice);
  }
}
