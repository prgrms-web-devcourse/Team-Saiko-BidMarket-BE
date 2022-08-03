package com.saiko.bidmarket.bidding.service;

import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.product.entity.Product;

public interface BiddingService {

  UnsignedLong create(BiddingCreateDto createDto);

  long selectWinner(Product product);
}
