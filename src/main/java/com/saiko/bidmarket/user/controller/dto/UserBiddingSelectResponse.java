package com.saiko.bidmarket.user.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.product.entity.Product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UserBiddingSelectResponse {
  private final long id;

  private final String title;

  private final String thumbnailImage;

  private final int minimumPrice;

  private final LocalDateTime expireAt;

  private final LocalDateTime createdAt;

  private final LocalDateTime updatedAt;

  public static UserBiddingSelectResponse from(Product product) {
    return UserBiddingSelectResponse
        .builder()
        .id(product.getId())
        .title(product.getTitle())
        .thumbnailImage(product.getThumbnailImage())
        .minimumPrice(product.getMinimumPrice())
        .expireAt(product.getExpireAt())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }

}
