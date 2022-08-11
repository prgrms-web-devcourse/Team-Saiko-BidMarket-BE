package com.saiko.bidmarket.product.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.product.entity.Product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ProductSelectResponse {
  private final long id;
  private final String title;
  private final String thumbnailImage;
  private final int minimumPrice;
  private final LocalDateTime expireAt;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static ProductSelectResponse from(Product product) {
    return new ProductSelectResponse(product.getId(), product.getTitle(),
                                     product.getThumbnailImage(),
                                     product.getMinimumPrice(), product.getExpireAt(),
                                     product.getCreatedAt(), product.getUpdatedAt()
    );
  }
}
