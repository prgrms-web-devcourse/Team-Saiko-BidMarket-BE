package com.saiko.bidmarket.user.controller.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

public class UserUpdateRequest {

  @Length(max = 20)
  @NotBlank
  private final String username;

  private final String profileImageURL;

  public UserUpdateRequest(String username, String profileImageURL) {

    this.username = username;
    this.profileImageURL = profileImageURL;
  }

  public String getUsername() {
    return username;
  }

  public String getProfileImageURL() {
    return profileImageURL;
  }
}
