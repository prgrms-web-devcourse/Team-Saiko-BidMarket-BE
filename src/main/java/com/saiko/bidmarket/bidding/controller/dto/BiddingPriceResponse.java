package com.saiko.bidmarket.bidding.controller.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.entity.BiddingPrice;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BiddingPriceResponse {

  private final BiddingPrice biddingPrice;

  @Builder
  private BiddingPriceResponse(BiddingPrice biddingPrice) {
    Assert.notNull(biddingPrice, "Bidding price must be provided");

    this.biddingPrice = biddingPrice;
  }
}
