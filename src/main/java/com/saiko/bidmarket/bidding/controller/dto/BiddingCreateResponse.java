package com.saiko.bidmarket.bidding.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BiddingCreateResponse {

  private final long id;

  public static BiddingCreateResponse from(long id) {
    return new BiddingCreateResponse(id);
  }
}
