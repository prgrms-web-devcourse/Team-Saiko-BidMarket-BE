package com.saiko.bidmarket.bidding.service.dto;

import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.common.entity.LongId;

import lombok.Getter;

@Getter
public class BiddingCreateDto {
  private final BiddingPrice biddingPrice;

  private final LongId productId;

  private final LongId bidderId;

  public BiddingCreateDto(BiddingPrice biddingPrice, LongId productId, LongId bidderId) {
    this.biddingPrice = biddingPrice;
    this.productId = productId;
    this.bidderId = bidderId;
  }
}
