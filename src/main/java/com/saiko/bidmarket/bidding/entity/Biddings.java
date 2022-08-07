package com.saiko.bidmarket.bidding.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.saiko.bidmarket.user.entity.User;

public class Biddings {
  private List<Bidding> biddingList;

  public Biddings(List<Bidding> biddingList) {
    this.biddingList = biddingList;
  }

  public User selectWinner() {
    if (biddingList.isEmpty()) {
      return null;
    }

    Bidding wonBidding = biddingList.get(0);
    wonBidding.win();

    return wonBidding.getBidder();
  }

  public Long selectWinningPrice(int minimumPrice) {
    Assert.isTrue(minimumPrice > 0, "MinimumPrice must be positive");

    if (biddingList.size() == 1) {
      return (long)minimumPrice;
    }

    return biddingList.get(1).getBiddingPrice() + 1000L;
  }

  public List<User> getBiddersExceptWinner() {
    List<User> bidders = biddingList.stream()
                                    .map(b -> b.getBidder())
                                    .collect(Collectors.toList());

    bidders.remove(0);

    return bidders;
  }
}
