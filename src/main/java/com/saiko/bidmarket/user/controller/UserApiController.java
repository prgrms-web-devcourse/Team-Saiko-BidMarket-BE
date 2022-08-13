package com.saiko.bidmarket.user.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.saiko.bidmarket.user.controller.dto.UserHeartCheckResponse;
import com.saiko.bidmarket.user.controller.dto.UserHeartSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserHeartSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserUpdateRequest;
import com.saiko.bidmarket.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

  private final UserService userService;

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
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @ModelAttribute @Valid
      UserBiddingSelectRequest request
  ) {
    return userService.findAllUserBiddings(authentication.getUserId(), request);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  public void deleteUser(
      @AuthenticationPrincipal
      JwtAuthentication authentication
  ) {
    userService.deleteUser(authentication.getUserId());
  }

  @PutMapping("{productId}/hearts")
  @ResponseStatus(HttpStatus.OK)
  public void toggleHeart(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @PathVariable
      long productId
  ) {
    userService.toggleHeart(authentication.getUserId(), productId);
  }

  @GetMapping("hearts")
  @ResponseStatus(HttpStatus.OK)
  public List<UserHeartSelectResponse> getAllUserHearts(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @ModelAttribute @Valid
      UserHeartSelectRequest request
  ) {
    long userId = authentication.getUserId();
    return userService.findAllUserHearts(userId, request);
  }

  @GetMapping("{productId}/hearts")
  public UserHeartCheckResponse isUserHearts(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @PathVariable
      long productId
  ) {
    return userService.isUserHearts(
        authentication.getUserId(),
        productId
    );
  }
}
