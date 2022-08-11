package com.saiko.bidmarket.comment.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class CommentCreateRequest {
  @Positive
  private final long productId;
  @NotBlank
  @Length(max = 500)
  private final String content;
}
