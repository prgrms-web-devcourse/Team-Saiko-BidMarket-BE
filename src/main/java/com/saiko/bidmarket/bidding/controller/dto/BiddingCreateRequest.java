package com.saiko.bidmarket.bidding.controller.dto;

import javax.validation.constraints.NotNull;

import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Getter;

@Getter
public class BiddingCreateRequest {

  @NotNull
  private final UnsignedLong productId;

  @NotNull
  private final BiddingPrice biddingPrice;

  public BiddingCreateRequest(long productId, long biddingPrice) {
    this.productId = UnsignedLong.valueOf(productId);
    this.biddingPrice = BiddingPrice.valueOf(biddingPrice);
  }

}
