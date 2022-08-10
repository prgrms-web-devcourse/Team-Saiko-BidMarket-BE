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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private long biddingPrice;

  private boolean won;

  @ManyToOne(fetch = FetchType.LAZY)
  private User bidder;

  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;

  @Builder
  public Bidding(
      BiddingPrice biddingPrice,
      User bidder,
      Product product
  ) {
    Assert.notNull(biddingPrice, "Bidding price must be provided");
    Assert.notNull(bidder, "Bidder must be provided");
    Assert.notNull(product, "Product must be provided");

    this.biddingPrice = biddingPrice.getValue();
    this.bidder = bidder;
    this.product = product;
    this.won = false;
    product
        .getBiddings()
        .add(this);

    validateCreatedField();
  }

  private void validateCreatedField() {
    validateMyProduct();
    validateProductProgress();
    validateBiddingPrice();
  }

  private void validateMyProduct() {
    if (product.getWriter() == bidder) {
      throw new IllegalArgumentException("자신의 상품에 비딩할 수 없습니다.");
    }
  }

  private void validateProductProgress() {
    if (!product.isProgressed()) {
      throw new IllegalArgumentException("비딩이 종료된 상품에 비딩할 수 없습니다.");
    }
  }

  private void validateBiddingPrice() {
    if (biddingPrice < product.getMinimumPrice()) {
      throw new IllegalArgumentException("상품의 최소 금액 이하로는 비딩할 수 없습니다.");
    }
  }

  public void win() {
    this.won = true;
  }
}
