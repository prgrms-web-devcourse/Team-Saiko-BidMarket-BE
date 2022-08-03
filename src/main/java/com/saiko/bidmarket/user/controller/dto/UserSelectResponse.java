package com.saiko.bidmarket.user.controller.dto;

import com.saiko.bidmarket.common.util.IdEncoder;
import com.saiko.bidmarket.user.entity.User;

public class UserSelectResponse {

  private final String encodedUserId;

  private final String username;

  private final String profileImageUrl;

  public UserSelectResponse(String encodedUserId, String username, String profileImageUrl) {

    this.encodedUserId = encodedUserId;
    this.username = username;
    this.profileImageUrl = profileImageUrl;
  }

  public String getEncodedUserId() {
    return encodedUserId;
  }

  public String getUsername() {
    return username;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public static UserSelectResponse from(User user) {
    final String encodedUserId = IdEncoder.encode(user.getId());
    return new UserSelectResponse(encodedUserId, user.getUsername(), user.getProfileImage());
  }
}
