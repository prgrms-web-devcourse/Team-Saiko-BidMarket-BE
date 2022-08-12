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

  private static final long PRICE_MIN_AMOUNT = 1_000L;

  private static final long PRICE_UNIT_AMOUNT = 100L;

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
      long biddingPrice,
      User bidder,
      Product product
  ) {
    Assert.notNull(bidder, "Bidder must be provided");
    Assert.notNull(product, "Product must be provided");

    this.biddingPrice = biddingPrice;
    this.bidder = bidder;
    this.product = product;
    this.won = false;
    product
        .getBiddings()
        .add(this);

    validate();
  }

  private void validate() {
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
    if (biddingPrice < PRICE_MIN_AMOUNT) {
      throw new IllegalArgumentException("비딩 최소 금액보다 작게 비딩할 수 없습니다.");
    }

    if (biddingPrice % PRICE_UNIT_AMOUNT != 0) {
      throw new IllegalArgumentException("비딩 단위 금액보다 적은 단위 금액을 사용할 수 없습니다.");
    }

    if (biddingPrice < product.getMinimumPrice()) {
      throw new IllegalArgumentException("상품의 최소 금액 이하로는 비딩할 수 없습니다.");
    }
  }

  public void win() {
    this.won = true;
  }
}
