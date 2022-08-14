package com.saiko.bidmarket.user.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserHeartCheckResponse {

  private final boolean heart;

  public static UserHeartCheckResponse from(boolean actived) {
    return new UserHeartCheckResponse(actived);
  }
}
