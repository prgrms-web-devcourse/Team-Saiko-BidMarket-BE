package com.saiko.bidmarket.comment.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectResponse;
import com.saiko.bidmarket.comment.service.CommentService;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.jwt.JwtAuthentication;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@RequestMapping("api/v1/comments")
public class CommentApiController {
  private final CommentService commentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommentCreateResponse create(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @RequestBody @Valid CommentCreateRequest request
  ) {
    return commentService.create(UnsignedLong.valueOf(authentication.getUserId()), request);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<CommentSelectResponse> findAll(@ModelAttribute @Valid CommentSelectRequest request) {
    return commentService.findAllByProduct(request);
  }
}
