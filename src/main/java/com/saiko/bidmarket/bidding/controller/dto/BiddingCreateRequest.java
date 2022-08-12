package com.saiko.bidmarket.bidding.controller.dto;

import javax.validation.constraints.Positive;

import lombok.Getter;

@Getter
public class BiddingCreateRequest {

  @Positive
  private final long productId;

  @Positive
  private final long biddingPrice;

  public BiddingCreateRequest(
      long productId,
      long biddingPrice
  ) {
    this.productId = productId;
    this.biddingPrice = biddingPrice;
  }

}
