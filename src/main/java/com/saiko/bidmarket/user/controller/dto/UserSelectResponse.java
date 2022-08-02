package com.saiko.bidmarket.user.controller.dto;

import com.saiko.bidmarket.user.entity.User;

public class UserSelectResponse {

  private final String username;

  private final String profileImageUrl;

  public UserSelectResponse(String username, String profileImageUrl) {

    this.username = username;
    this.profileImageUrl = profileImageUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public static UserSelectResponse from(User user) {
    return new UserSelectResponse(user.getUsername(), user.getProfileImage());
  }
}
