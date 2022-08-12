package com.saiko.bidmarket.comment.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.comment.entity.Comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CommentSelectResponse {
  private final long userId;
  private final String username;
  private final String profileImage;
  private final String content;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static CommentSelectResponse from(Comment comment) {
    return CommentSelectResponse
        .builder()
        .userId(comment
                    .getWriter()
                    .getId())
        .username(comment
                      .getWriter()
                      .getUsername())
        .profileImage(comment
                          .getWriter()
                          .getProfileImage())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }
}
