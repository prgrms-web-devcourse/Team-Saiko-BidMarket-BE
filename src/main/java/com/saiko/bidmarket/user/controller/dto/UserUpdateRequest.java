package com.saiko.bidmarket.user.controller.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

public class UserUpdateRequest {

  @Length(max = 20)
  @NotBlank
  private final String username;

  @NotBlank
  private final String profileImageUrl;

  public UserUpdateRequest(String username, String profileImageUrl) {

    this.username = username;
    this.profileImageUrl = profileImageUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }
}
