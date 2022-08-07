package com.saiko.bidmarket.comment.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentSelectResponse {
  private final UnsignedLong userId;
  private final String username;
  private final String profileImage;
  private final String content;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;


  public static CommentSelectResponse from(Comment comment) {
    return CommentSelectResponse
        .builder()
        .userId(UnsignedLong.valueOf(comment.getWriter().getId()))
        .username(comment.getWriter().getUsername())
        .profileImage(comment.getWriter().getProfileImage())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }
}
