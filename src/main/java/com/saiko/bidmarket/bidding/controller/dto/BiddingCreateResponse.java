package com.saiko.bidmarket.bidding.controller.dto;

import lombok.Getter;

@Getter
public class BiddingCreateResponse {

  private final long id;

  public BiddingCreateResponse(long id) {
    this.id = id;
  }
}
