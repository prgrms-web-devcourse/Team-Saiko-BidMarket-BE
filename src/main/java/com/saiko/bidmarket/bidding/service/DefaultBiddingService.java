package com.saiko.bidmarket.bidding.service;

import java.util.List;

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

    if (createDto.getBiddingPrice().getValue() < product.getMinimumPrice()) {
      throw new IllegalArgumentException("상품의 비딩 최소 금액 이하로는 비딩할 수 없습니다. ");
    }

    Bidding bidding = new Bidding(createDto.getBiddingPrice(), bidder, product);

    Bidding createdBidding = biddingRepository.save(bidding);

    return UnsignedLong.valueOf(createdBidding.getId());
  }

  @Override
  public long selectWinner(Product product) {
    Assert.notNull(product, "product must be provided");

    List<Bidding> biddings = biddingRepository.findAllByProductOrderByBiddingPriceDesc(product);

    //TODO: 비딩한 내역이 존재하지 않는 Product일 경우 로직 구현

    biddings.get(0).win();

    if (biddings.size() == 1) {
      return product.getMinimumPrice();
    }

    return biddings.get(1).getBiddingPrice() + 1000L;
  }
}
