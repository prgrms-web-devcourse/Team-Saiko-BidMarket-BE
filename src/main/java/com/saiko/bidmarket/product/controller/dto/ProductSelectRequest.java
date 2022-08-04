package com.saiko.bidmarket.product.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.Sort;

public class ProductSelectRequest {
  private String progressed;
  private final Category category;
  @PositiveOrZero
  private final int offset;
  @Positive
  private final int limit;
  private final Sort sort;

  public ProductSelectRequest(String progressed, Category category, int offset, int limit,
                              Sort sort) {
    this.progressed = progressed;
    this.category = category;
    this.offset = offset;
    this.limit = limit;
    this.sort = sort == null ? Sort.END_DATE_ASC : sort;
  }

  public String getProgressed() {
    return progressed;
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
