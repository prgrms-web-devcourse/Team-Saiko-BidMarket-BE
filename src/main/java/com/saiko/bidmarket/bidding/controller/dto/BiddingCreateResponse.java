package com.saiko.bidmarket.bidding.controller.dto;

public class BiddingCreateResponse {
  private final long id;

  public BiddingCreateResponse(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

}
