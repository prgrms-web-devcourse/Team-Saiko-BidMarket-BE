package com.saiko.bidmarket.bidding.repository.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BiddingPriceFindingRepoDto {

  private final UnsignedLong bidderId;

  private final UnsignedLong productId;

  @Builder
  private BiddingPriceFindingRepoDto(UnsignedLong bidderId, UnsignedLong productId) {
    Assert.notNull(bidderId, "Bidder id must be provided");
    Assert.notNull(productId, "Product id must be provided");

    this.bidderId = bidderId;
    this.productId = productId;
  }
}
