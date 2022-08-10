package com.saiko.bidmarket.bidding.service.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BiddingPriceFindingDto {
  private final UnsignedLong productId;
  private final UnsignedLong bidderId;

  @Builder
  private BiddingPriceFindingDto(
      UnsignedLong productId,
      UnsignedLong bidderId
  ) {
    Assert.notNull(productId, "findingDto productId must be provided");
    Assert.notNull(bidderId, "findingDto userId must be provided");

    this.productId = productId;
    this.bidderId = bidderId;
  }
}
