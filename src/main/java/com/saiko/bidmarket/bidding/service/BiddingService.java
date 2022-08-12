package com.saiko.bidmarket.bidding.service;

import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateRequest;
import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateResponse;
import com.saiko.bidmarket.bidding.controller.dto.BiddingPriceResponse;

public interface BiddingService {

  BiddingCreateResponse create(
      long userId,
      BiddingCreateRequest createRequest
  );

  BiddingPriceResponse findBiddingPriceByProductIdAndUserId(
      long userId,
      long productId
  );

}
