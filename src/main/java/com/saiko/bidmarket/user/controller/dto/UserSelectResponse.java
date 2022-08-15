package com.saiko.bidmarket.user.controller.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.user.entity.User;

public class UserSelectResponse {

  private final long id;

  private final String username;

  private final String profileImage;

  public UserSelectResponse(
      long id,
      String username,
      String profileImage
  ) {
    Assert.notNull(username, "User name must be provided");
    Assert.notNull(profileImage, "User name must be provided");

    this.id = id;
    this.username = username;
    this.profileImage = profileImage;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getProfileImage() {
    return profileImage;
  }

  public static UserSelectResponse from(User user) {
    return new UserSelectResponse(user.getId(), user.getUsername(), user.getProfileImage());
  }
}
