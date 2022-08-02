package com.saiko.bidmarket.user.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.common.jwt.JwtAuthentication;
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
}
