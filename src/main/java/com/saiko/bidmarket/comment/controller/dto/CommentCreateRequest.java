package com.saiko.bidmarket.comment.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Getter;

@Getter
public class CommentCreateRequest {
  @NotNull
  private UnsignedLong productId;
  @NotBlank
  @Length(max = 500)
  private String content;

  public CommentCreateRequest(
      long productId,
      String content
  ) {
    this.productId = UnsignedLong.valueOf(productId);
    this.content = content;
  }
}
