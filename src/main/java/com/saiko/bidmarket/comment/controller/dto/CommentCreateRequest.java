package com.saiko.bidmarket.comment.controller.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class CommentCreateRequest {
  private final long productId;
  @NotBlank
  @Length(max = 500)
  private final String content;
}
