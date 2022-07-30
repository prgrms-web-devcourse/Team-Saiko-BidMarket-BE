package com.saiko.bidmarket.user.entity.dto;

import com.saiko.bidmarket.user.entity.User;

public class UserBasicResponse {

  private final long id;
  private final String name;
  private final String profileImageUrl;

  private UserBasicResponse(long id, String name, String profileImageUrl) {
    this.id = id;
    this.name = name;
    this.profileImageUrl = profileImageUrl;
  }

  public static UserBasicResponse from(User writer) {
    return new UserBasicResponse(writer.getId(),
                                 writer.getUsername(),
                                 writer.getProfileImage());
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

}
