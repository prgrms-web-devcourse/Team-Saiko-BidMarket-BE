package com.saiko.bidmarket.bidding.controller.dto;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Getter;

@Getter
public class BiddingCreateResponse {

  private final UnsignedLong id;

  public BiddingCreateResponse(UnsignedLong id) {
    this.id = id;
  }
}
