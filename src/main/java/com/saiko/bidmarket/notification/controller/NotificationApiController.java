package com.saiko.bidmarket.notification.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.common.jwt.JwtAuthentication;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;
import com.saiko.bidmarket.notification.service.NotificationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class NotificationApiController {

  private final NotificationService notificationService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<NotificationSelectResponse> getAllNotification(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @ModelAttribute @Valid
      NotificationSelectRequest request
      ) {
    return notificationService.findAllNotifications(authentication.getUserId(), request);
  }

  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public void checkNotification(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @PathVariable
      long id
  ) {
    notificationService.checkNotification(authentication.getUserId(), id);
  }
}
