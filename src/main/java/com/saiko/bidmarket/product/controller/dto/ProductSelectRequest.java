package com.saiko.bidmarket.product.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.Length;

import com.saiko.bidmarket.common.Sort;
import com.saiko.bidmarket.product.Category;

import lombok.Getter;

@Getter
public class ProductSelectRequest {
  @Length(max = 32)
  private final String title;
  private final String progressed;
  private final Category category;
  @PositiveOrZero
  private final long offset;
  @Positive
  private final int limit;
  private final Sort sort;

  public ProductSelectRequest(
      String title,
      String progressed,
      Category category,
      long offset,
      int limit,
      Sort sort
  ) {
    this.title = title;
    this.progressed = progressed;
    this.category = category;
    this.offset = offset;
    this.limit = limit;
    this.sort = sort == null ? Sort.END_DATE_ASC : sort;
  }
}
