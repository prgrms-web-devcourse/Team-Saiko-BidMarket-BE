package com.saiko.bidmarket.bidding.service;

import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;
import com.saiko.bidmarket.bidding.service.dto.BiddingPriceFindingDto;
import com.saiko.bidmarket.common.entity.UnsignedLong;

public interface BiddingService {

  UnsignedLong create(BiddingCreateDto createDto);

  BiddingPrice findBiddingPriceByProductIdAndUserId(BiddingPriceFindingDto biddingPriceFindingDto);

}
