package com.saiko.bidmarket.bidding.service;

import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;
import com.saiko.bidmarket.common.entity.UnsignedLong;

public interface BiddingService {

  UnsignedLong create(BiddingCreateDto createDto);
}
