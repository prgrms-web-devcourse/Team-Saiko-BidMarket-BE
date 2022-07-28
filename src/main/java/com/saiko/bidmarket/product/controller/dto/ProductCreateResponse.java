package com.saiko.bidmarket.product.controller.dto;

public class ProductCreateResponse {
  private final long id;

  public ProductCreateResponse(long id) {
    this.id = id;
  }

  public static ProductCreateResponse from(long id) {
    return new ProductCreateResponse(id);
  }

  public long getId() {
    return id;
  }
}
