package com.saiko.bidmarket.comment.service;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;

public interface CommentService {
  CommentCreateResponse create(long userId, CommentCreateRequest request);
}
