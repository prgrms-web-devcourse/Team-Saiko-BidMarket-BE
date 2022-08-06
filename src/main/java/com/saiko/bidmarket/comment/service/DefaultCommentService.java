package com.saiko.bidmarket.comment.service;

import org.springframework.stereotype.Service;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;

@Service
public class DefaultCommentService implements CommentService {
  @Override
  public CommentCreateResponse create(long userId, CommentCreateRequest request) {
    return null;
  }
}
