package com.saiko.bidmarket.user.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.common.jwt.JwtAuthentication;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserHeartResponse;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserUpdateRequest;
import com.saiko.bidmarket.user.service.UserService;

@RestController
@RequestMapping("api/v1/users")
public class UserApiController {

  private final UserService userService;

  public UserApiController(UserService userService) {
    this.userService = userService;
  }

  @PatchMapping
  @ResponseStatus(HttpStatus.OK)
  public void updateUser(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @RequestBody @Valid UserUpdateRequest request
  ) {
    userService.updateUser(authentication.getUserId(), request);
  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserSelectResponse getUser(@PathVariable long id) {
    return userService.findById(id);
  }

  @GetMapping("auth")
  @ResponseStatus(HttpStatus.OK)
  public UserSelectResponse getUserIdInfo(
      @AuthenticationPrincipal JwtAuthentication authentication
  ) {
    final long userId = authentication.getUserId();
    return userService.findById(userId);
  }

  @GetMapping("{id}/products")
  @ResponseStatus(HttpStatus.OK)
  public List<UserProductSelectResponse> getAllUserProduct(
      @ModelAttribute @Valid UserProductSelectRequest request,
      @PathVariable long id
  ) {
    return userService.findAllUserProducts(id, request);
  }

  @GetMapping("biddings")
  @ResponseStatus(HttpStatus.OK)
  public List<UserBiddingSelectResponse> getAllUserBidding(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @ModelAttribute @Valid UserBiddingSelectRequest request
  ) {
    long userId = authentication.getUserId();
    return userService.findAllUserBiddings(userId, request);
  }

  @PutMapping("{productId}/hearts")
  @ResponseStatus(HttpStatus.OK)
  public UserHeartResponse toggleHeart(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @PathVariable long productId
  ) {
    long userId = authentication.getUserId();
    return userService.toggleHeart(userId, productId);
  }
}
