package com.saiko.bidmarket.product.controller.dto;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.product.Role;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BiddingResultResponse {
  private final Role role;
  private final boolean biddingSucceed;
  private final UnsignedLong chatRoomId;

  public static BiddingResultResponse responseForSuccessfulSeller(UnsignedLong chatRoomId) {
    return BiddingResultResponse
        .builder()
        .role(Role.SELLER)
        .biddingSucceed(true)
        .chatRoomId(chatRoomId)
        .build();
  }

  public static BiddingResultResponse responseForFailedSeller() {
    return BiddingResultResponse
        .builder()
        .role(Role.SELLER)
        .biddingSucceed(false)
        .chatRoomId(null)
        .build();
  }

  public static BiddingResultResponse responseForSuccessfulBidder(UnsignedLong chatRoomId) {
    return BiddingResultResponse
        .builder()
        .role(Role.BIDDER)
        .biddingSucceed(true)
        .chatRoomId(chatRoomId)
        .build();
  }

  public static BiddingResultResponse responseForFailedBidder() {
    return BiddingResultResponse
        .builder()
        .role(Role.BIDDER)
        .biddingSucceed(false)
        .chatRoomId(null)
        .build();
  }
}
