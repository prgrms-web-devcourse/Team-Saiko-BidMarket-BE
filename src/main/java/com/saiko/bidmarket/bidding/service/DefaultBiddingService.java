package com.saiko.bidmarket.bidding.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.respository.BiddingRepository;
import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.service.ProductService;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.service.UserService;

@Service
public class DefaultBiddingService implements BiddingService {

  private final BiddingRepository biddingRepository;

  private final UserService userService;

  private final ProductService productService;

  public DefaultBiddingService(BiddingRepository biddingRepository, UserService userService,
                               ProductService productService) {
    this.biddingRepository = biddingRepository;
    this.userService = userService;
    this.productService = productService;
  }

  @Override
  public long create(BiddingCreateDto createDto) {
    Assert.notNull(createDto, "createDto must be provided");

    User bidder = userService.findById(createDto.getBidderId().getValue());
    Product product = productService.findById(createDto.getProductId().getValue());

    Bidding bidding = new Bidding(createDto.getBiddingPrice(), bidder, product);
    return biddingRepository.save(bidding).getId();
  }
}
