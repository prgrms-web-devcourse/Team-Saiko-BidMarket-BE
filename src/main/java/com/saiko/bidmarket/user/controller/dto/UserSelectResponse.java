package com.saiko.bidmarket.user.controller.dto;

import com.saiko.bidmarket.common.util.IdEncoder;
import com.saiko.bidmarket.user.entity.User;

public class UserSelectResponse {

  private final String encodedId;

  private final String username;

  private final String thumbnailImg;

  public UserSelectResponse(String encodedUserId, String username, String thumbnailImg) {

    this.encodedId = encodedUserId;
    this.username = username;
    this.thumbnailImg = thumbnailImg;
  }

  public String getEncodedId() {
    return encodedId;
  }

  public String getUsername() {
    return username;
  }

  public String getThumbnailImg() {
    return thumbnailImg;
  }

  public static UserSelectResponse from(User user) {
    final String encodedUserId = IdEncoder.encode(user.getId());
    return new UserSelectResponse(encodedUserId, user.getUsername(), user.getProfileImage());
  }
}
