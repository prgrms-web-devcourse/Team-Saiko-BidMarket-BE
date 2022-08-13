package com.saiko.bidmarket.chat.controller.dto;

import com.saiko.bidmarket.user.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class ChatUserInfo {

  private final long userId;
  private final String username;
  private final String profileImage;

  public static ChatUserInfo from(User user) {
    return ChatUserInfo
        .builder()
        .userId(user.getId())
        .username(user.getUsername())
        .profileImage(user.getProfileImage())
        .build();
  }
}
