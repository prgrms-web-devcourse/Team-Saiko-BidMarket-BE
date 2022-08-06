package com.saiko.bidmarket.comment.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.service.CommentService;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.jwt.JwtAuthentication;

@RestController
@RequestMapping("api/v1/comments")
public class CommentApiController {
  private final CommentService commentService;

  public CommentApiController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommentCreateResponse create(@AuthenticationPrincipal JwtAuthentication authentication,
                                      @RequestBody @Valid CommentCreateRequest request) {
    return commentService.create(UnsignedLong.valueOf(authentication.getUserId()), request);
  }
}
