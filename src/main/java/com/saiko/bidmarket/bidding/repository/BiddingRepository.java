package com.saiko.bidmarket.bidding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.product.entity.Product;

public interface BiddingRepository extends BiddingCustomRepository, JpaRepository<Bidding, Long> {
  List<Bidding> findAllByProductOrderByBiddingPriceDesc(Product product);

  void deleteAllBatchByBidderId(long bidderId);

  void deleteAllBatchByProductId(long productId);
}
