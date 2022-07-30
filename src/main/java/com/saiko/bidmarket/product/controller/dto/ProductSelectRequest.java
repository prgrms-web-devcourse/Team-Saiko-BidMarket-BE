package com.saiko.bidmarket.product.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.saiko.bidmarket.product.Sort;

public class ProductSelectRequest {
  @PositiveOrZero
  private int offset;
  @Positive
  private int limit;
  private Sort sort = Sort.END_DATE_ASC;

  public ProductSelectRequest(int offset, int limit, Sort sort) {
    this.offset = offset;
    this.limit = limit;
    this.sort = sort;
  }
}
