package com.saiko.bidmarket.bidding.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.BaseTime;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bidding extends BaseTime {

  private static final long UNIT_AMOUNT = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private long biddingPrice;

  @ManyToOne(fetch = FetchType.LAZY)
  private User bidder;

  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;

  @Builder
  public Bidding(BiddingPrice biddingPrice, User bidder, Product product) {
    Assert.notNull(biddingPrice, "Bidding price must be provided");
    Assert.notNull(bidder, "Bidder must be provided");
    Assert.notNull(product, "Product must be provided");

    this.biddingPrice = biddingPrice.getValue();
    this.bidder = bidder;
    this.product = product;
  }

}
