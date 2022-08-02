package com.saiko.bidmarket.bidding.service;

import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;

public interface BiddingService {

  long create(BiddingCreateDto createDto);
}
