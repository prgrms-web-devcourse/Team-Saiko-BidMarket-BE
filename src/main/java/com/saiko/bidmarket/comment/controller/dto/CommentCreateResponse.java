package com.saiko.bidmarket.comment.controller.dto;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Getter;

@Getter
public class CommentCreateResponse {
  private final UnsignedLong id;

  public CommentCreateResponse(UnsignedLong id) {
    this.id = id;
  }
}
