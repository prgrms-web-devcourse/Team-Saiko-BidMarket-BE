package com.saiko.bidmarket.bidding.service.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.common.entity.LongId;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BiddingCreateDto {
  private final BiddingPrice biddingPrice;

  private final LongId productId;

  private final LongId bidderId;

  @Builder
  public BiddingCreateDto(BiddingPrice biddingPrice, LongId productId, LongId bidderId) {
    Assert.notNull(biddingPrice, "bidding price must be provided");
    Assert.notNull(productId, "product id must be provided");
    Assert.notNull(bidderId, "bidder id must be provided");

    this.biddingPrice = biddingPrice;
    this.productId = productId;
    this.bidderId = bidderId;
  }
}
