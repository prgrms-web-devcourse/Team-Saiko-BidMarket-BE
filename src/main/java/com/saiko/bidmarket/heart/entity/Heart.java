package com.saiko.bidmarket.heart.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class Heart extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  private boolean actived;

  @Builder
  private Heart(User user, Product product) {
    Assert.notNull(product, "Product must be provided");

    this.user = user;
    this.product = product;
  }

  public static Heart of(
      User user,
      Product product
  ) {
    return Heart
        .builder()
        .user(user)
        .product(product)
        .build();
  }

  public void toggle() {
    actived = !actived;
  }
}
