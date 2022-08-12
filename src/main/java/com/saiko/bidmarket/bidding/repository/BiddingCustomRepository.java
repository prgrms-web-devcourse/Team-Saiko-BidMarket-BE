package com.saiko.bidmarket.bidding.repository;

import java.util.List;
import java.util.Optional;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;

public interface BiddingCustomRepository {
  List<Bidding> findAllUserBidding(
      long userId,
      UserBiddingSelectRequest request
  );

  Optional<Bidding> findByBidderIdAndProductId(
      long bidderId,
      long productId
  );
}
