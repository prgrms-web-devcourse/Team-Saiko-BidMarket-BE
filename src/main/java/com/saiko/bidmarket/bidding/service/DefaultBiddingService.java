package com.saiko.bidmarket.bidding.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateRequest;
import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateResponse;
import com.saiko.bidmarket.bidding.controller.dto.BiddingPriceResponse;
import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBiddingService implements BiddingService {

  private final BiddingRepository biddingRepository;

  private final UserRepository userRepository;

  private final ProductRepository productRepository;

  @Transactional
  @Override
  public BiddingCreateResponse create(
      long userId,
      BiddingCreateRequest createRequest
  ) {
    Assert.notNull(createRequest, "createRequest must be provided");

    User bidder = userRepository
        .findById(userId)
        .orElseThrow(NotFoundException::new);

    Product product = productRepository
        .findById(createRequest.getProductId())
        .orElseThrow(NotFoundException::new);

    Bidding bidding = new Bidding(createRequest.getBiddingPrice(), bidder, product);
    long createdBiddingId = biddingRepository
        .save(bidding)
        .getId();

    return new BiddingCreateResponse(createdBiddingId);
  }

  @Override
  public BiddingPriceResponse findBiddingPriceByProductIdAndUserId(
      long productId,
      long userId
  ) {
    Bidding bidding = biddingRepository
        .findByBidderIdAndProductId(userId, productId)
        .orElseThrow(NotFoundException::new);

    return new BiddingPriceResponse(bidding.getBiddingPrice());
  }
}
