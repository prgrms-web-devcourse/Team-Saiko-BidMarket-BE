package com.saiko.bidmarket.user.controller.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

public class UserUpdateRequest {

  @Length(max = 20)
  @NotBlank
  private final String username;

  @NotBlank
  private final String profileImage;

  public UserUpdateRequest(String username, String profileImage) {

    this.username = username;
    this.profileImage = profileImage;
  }

  public String getUsername() {
    return username;
  }

  public String getProfileImage() {
    return profileImage;
  }
}
