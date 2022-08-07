package com.saiko.bidmarket.bidding.entity;

import java.util.List;

import org.springframework.util.Assert;

public class Biddings {
  private List<Bidding> biddingList;

  public Biddings(List<Bidding> biddingList) {
    this.biddingList = biddingList;
  }

  public Long selectWinner(int minimumPrice) {
    Assert.isTrue(minimumPrice > 0, "MinimumPrice must be positive");

    if (biddingList.isEmpty()) {
      return null;
    }

    biddingList.get(0).win();

    if (biddingList.size() == 1) {
      return (long)minimumPrice;
    }

    return biddingList.get(1).getBiddingPrice() + 1000L;
  }
}
