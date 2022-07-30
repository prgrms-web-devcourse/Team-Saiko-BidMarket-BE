package com.saiko.bidmarket.product.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.saiko.bidmarket.product.Sort;

public class ProductSelectRequest {
  @PositiveOrZero
  private int offset;
  @Positive
  private int limit;
  private Sort sort;

  public ProductSelectRequest(int offset, int limit, Sort sort) {
    this.offset = offset;
    this.limit = limit;
    this.sort = sort;
    if (sort == null) {
      this.sort = Sort.END_DATE_ASC;
    }
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

  public Sort getSort() {
    return sort;
  }
}
