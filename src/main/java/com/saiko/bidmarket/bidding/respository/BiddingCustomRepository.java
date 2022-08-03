package com.saiko.bidmarket.bidding.respository;

import java.util.List;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;

public interface BiddingCustomRepository {
  List<Bidding> findAllUserBidding(long userId, UserBiddingSelectRequest request);
}
