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

@Entity
public class Bidding extends BaseTime {

  private static final long UNIT_AMOUNT = 1000;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private long biddingPrice;

  @ManyToOne(fetch = FetchType.LAZY)
  private User bidder;

  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;

  protected Bidding() {
  }

  public Bidding(long biddingPrice, User bidder, Product product) {
    Assert.isTrue(isValidBiddingPrice(biddingPrice),
                  "Bidding price must be positive number and a multiple of the unit amount");
    Assert.notNull(bidder, "Bidder must be provided");
    Assert.notNull(product, "Product must be provided");

    this.biddingPrice = biddingPrice;
    this.bidder = bidder;
    this.product = product;
  }

  private boolean isValidBiddingPrice(long biddingPrice) {
    return 0 < biddingPrice && biddingPrice % UNIT_AMOUNT == 0;
  }

  public Long getId() {
    return id;
  }

  public long getBiddingPrice() {
    return biddingPrice;
  }

  public User getBidder() {
    return bidder;
  }

  public Product getProduct() {
    return product;
  }
}
