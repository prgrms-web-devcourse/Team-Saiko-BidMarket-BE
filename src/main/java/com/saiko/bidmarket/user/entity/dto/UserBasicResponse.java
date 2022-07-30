package com.saiko.bidmarket.user.entity.dto;

import com.saiko.bidmarket.user.entity.User;

public class UserBasicResponse {
  private final String name;
  private final String profileImageUrl;

  private UserBasicResponse(String name, String profileImageUrl) {
    this.name = name;
    this.profileImageUrl = profileImageUrl;
  }

  public static UserBasicResponse from(User writer) {
    return new UserBasicResponse(writer.getUsername(),
                                 writer.getProfileImage());
  }

  public String getName() {
    return name;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

}
