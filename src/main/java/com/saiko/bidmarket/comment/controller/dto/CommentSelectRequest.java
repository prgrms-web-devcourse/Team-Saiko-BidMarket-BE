package com.saiko.bidmarket.comment.controller.dto;

import javax.validation.constraints.Positive;

import com.saiko.bidmarket.common.Sort;

import lombok.Getter;

@Getter
public class CommentSelectRequest {
  @Positive
  private final long productId;
  private final Sort sort;

  public CommentSelectRequest(
      long productId,
      Sort sort
  ) {
    this.productId = productId;
    this.sort = sort == null ? Sort.CREATED_AT_ASC : sort;
  }
}
