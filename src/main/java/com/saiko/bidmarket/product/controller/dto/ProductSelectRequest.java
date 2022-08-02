package com.saiko.bidmarket.product.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.Sort;

public class ProductSelectRequest {
  private final Category category;
  @PositiveOrZero
  private final int offset;
  @Positive
  private final int limit;
  private Sort sort;

  public ProductSelectRequest(Category category, int offset, int limit, Sort sort) {
    this.category = category;
    this.offset = offset;
    this.limit = limit;
    this.sort = sort;
    if (sort == null) {
      this.sort = Sort.END_DATE_ASC;
    }
  }

  public Category getCategory() {
    return category;
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
