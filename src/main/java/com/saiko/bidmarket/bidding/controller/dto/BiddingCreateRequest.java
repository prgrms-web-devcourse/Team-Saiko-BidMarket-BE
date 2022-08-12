package com.saiko.bidmarket.bidding.controller.dto;

import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BiddingCreateRequest {

  @Positive
  private final long productId;

  @Positive
  private final long biddingPrice;

}
