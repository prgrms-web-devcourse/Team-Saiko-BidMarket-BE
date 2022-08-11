package com.saiko.bidmarket.comment.controller.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CommentCreateResponse {
  private final long id;

  public static CommentCreateResponse from(long id) {
    return CommentCreateResponse
        .builder()
        .id(id)
        .build();
  }
}
