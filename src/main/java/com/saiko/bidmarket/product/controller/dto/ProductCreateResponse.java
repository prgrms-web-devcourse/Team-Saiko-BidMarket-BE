package com.saiko.bidmarket.product.controller.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ProductCreateResponse {
  private final long id;

  public static ProductCreateResponse from(long id) {
    return ProductCreateResponse
        .builder()
        .id(id)
        .build();
  }
}
