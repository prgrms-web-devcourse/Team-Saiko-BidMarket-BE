package com.saiko.bidmarket.user.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;
import com.saiko.bidmarket.product.entity.Product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProductSelectResponse {

  private final long id;

  private final String title;

  private final String thumbnailImage;

  private final int minimumPrice;

  private final LocalDateTime expireAt;

  private final LocalDateTime createdAt;

  private final LocalDateTime updatedAt;

  public static UserProductSelectResponse from(Product product) {
    return new UserProductSelectResponse(product.getId(), product.getTitle(),
                                     product.getThumbnailImage(),
                                     product.getMinimumPrice(), product.getExpireAt(),
                                     product.getCreatedAt(), product.getUpdatedAt());
  }
}
