package com.saiko.bidmarket.comment.controller.dto;

import com.saiko.bidmarket.common.Sort;

import lombok.Getter;

@Getter
public class CommentSelectRequest {
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
