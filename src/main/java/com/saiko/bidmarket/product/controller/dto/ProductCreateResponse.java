package com.saiko.bidmarket.product.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCreateResponse {
  private final long id;

  public static ProductCreateResponse from(long id) {
    return new ProductCreateResponse(id);
  }
}
