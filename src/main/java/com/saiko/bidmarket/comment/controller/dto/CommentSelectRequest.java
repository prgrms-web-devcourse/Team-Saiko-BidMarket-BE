package com.saiko.bidmarket.comment.controller.dto;

import javax.validation.constraints.NotNull;

import com.saiko.bidmarket.common.Sort;
import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Getter;

@Getter
public class CommentSelectRequest {
  @NotNull
  private final UnsignedLong productId;
  private final Sort sort;

  public CommentSelectRequest(long productId,
                              Sort sort) {
    this.productId = UnsignedLong.valueOf(productId);
    this.sort = sort == null ? Sort.CREATED_AT_ASC : sort;
  }
}
