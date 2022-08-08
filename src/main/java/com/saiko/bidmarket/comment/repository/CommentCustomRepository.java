package com.saiko.bidmarket.comment.repository;

import java.util.List;

import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.entity.Comment;

public interface CommentCustomRepository {
  List<Comment> findAllByProduct(CommentSelectRequest commentSelectRequest);
}
