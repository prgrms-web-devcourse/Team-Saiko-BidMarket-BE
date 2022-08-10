package com.saiko.bidmarket.bidding.service.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BiddingCreateDto {
  private final BiddingPrice biddingPrice;

  private final UnsignedLong productId;

  private final UnsignedLong bidderId;

  @Builder
  public BiddingCreateDto(
      BiddingPrice biddingPrice,
      UnsignedLong productId,
      UnsignedLong bidderId
  ) {
    Assert.notNull(biddingPrice, "bidding price must be provided");
    Assert.notNull(productId, "product id must be provided");
    Assert.notNull(bidderId, "bidder id must be provided");

    this.biddingPrice = biddingPrice;
    this.productId = productId;
    this.bidderId = bidderId;
  }
}
