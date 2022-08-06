package com.saiko.bidmarket.comment.service;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.common.entity.UnsignedLong;

public interface CommentService {
  CommentCreateResponse create(UnsignedLong userId, CommentCreateRequest request);
}
