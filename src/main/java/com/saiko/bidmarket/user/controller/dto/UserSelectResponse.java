package com.saiko.bidmarket.user.controller.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.user.entity.User;

public class UserSelectResponse {

  private final UnsignedLong id;

  private final String username;

  private final String thumbnailImg;

  public UserSelectResponse(long id, String username, String thumbnailImg) {
    Assert.notNull(username, "User name must be provided");
    Assert.notNull(thumbnailImg, "User name must be provided");

    this.id = UnsignedLong.valueOf(id);
    this.username = username;
    this.thumbnailImg = thumbnailImg;
  }

  public long getId() {
    return id.getValue();
  }

  public String getUsername() {
    return username;
  }

  public String getThumbnailImg() {
    return thumbnailImg;
  }

  public static UserSelectResponse from(User user) {
    return new UserSelectResponse(user.getId(), user.getUsername(), user.getProfileImage());
  }
}
