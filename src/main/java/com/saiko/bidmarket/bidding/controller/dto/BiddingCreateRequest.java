package com.saiko.bidmarket.bidding.controller.dto;

import javax.validation.constraints.NotNull;

import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.common.entity.LongId;

public class BiddingCreateRequest {

  @NotNull
  private final LongId productId;

  @NotNull
  private final BiddingPrice biddingPrice;

  public BiddingCreateRequest(long productId, long biddingPrice) {
    this.productId = new LongId(productId);
    this.biddingPrice = new BiddingPrice(biddingPrice);
  }

  public LongId getProductId() {
    return productId;
  }

  public BiddingPrice getBiddingPrice() {
    return biddingPrice;
  }
}
