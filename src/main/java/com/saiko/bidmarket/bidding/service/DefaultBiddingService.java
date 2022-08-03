package com.saiko.bidmarket.bidding.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.respository.BiddingRepository;
import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@Service
public class DefaultBiddingService implements BiddingService {

  private final BiddingRepository biddingRepository;

  private final UserRepository userRepository;

  private final ProductRepository productRepository;

  public DefaultBiddingService(BiddingRepository biddingRepository, UserRepository userRepository,
                               ProductRepository productRepository) {
    this.biddingRepository = biddingRepository;
    this.userRepository = userRepository;
    this.productRepository = productRepository;
  }

  @Override
  public UnsignedLong create(BiddingCreateDto createDto) {
    Assert.notNull(createDto, "createDto must be provided");

    User bidder = userRepository.findById(createDto.getBidderId().getValue())
                                .orElseThrow(NotFoundException::new);
    Product product = productRepository.findById(createDto.getProductId().getValue())
                                       .orElseThrow(NotFoundException::new);

    if (!product.isProgressed()) {
      throw new IllegalArgumentException("비딩이 종료된 상품에 비딩할 수 없습니다.");
    }

    Bidding bidding = new Bidding(createDto.getBiddingPrice(), bidder, product);

    Bidding createdBidding = biddingRepository.save(bidding);

    return UnsignedLong.valueOf(createdBidding.getId());
  }
}
