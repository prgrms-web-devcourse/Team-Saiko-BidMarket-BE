package com.saiko.bidmarket.bidding.service;

import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateRequest;

public interface BiddingService {

  long create(
      long userId,
      BiddingCreateRequest createRequest
  );

  long findBiddingPriceByProductIdAndUserId(
      long userId,
      long productId
  );

}
