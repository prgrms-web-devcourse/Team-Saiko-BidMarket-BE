package com.saiko.bidmarket.comment.service;

import java.util.List;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectResponse;
import com.saiko.bidmarket.common.entity.UnsignedLong;

public interface CommentService {
  CommentCreateResponse create(UnsignedLong userId, CommentCreateRequest request);

  List<CommentSelectResponse> findAllByProduct(CommentSelectRequest request);
}
