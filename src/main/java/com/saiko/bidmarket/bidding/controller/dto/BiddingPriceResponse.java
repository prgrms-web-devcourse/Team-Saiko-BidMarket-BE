package com.saiko.bidmarket.bidding.controller.dto;

import lombok.Getter;

@Getter
public class BiddingPriceResponse {

  private final long biddingPrice;

  public BiddingPriceResponse(long biddingPrice) {
    this.biddingPrice = biddingPrice;
  }
}
