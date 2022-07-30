package com.saiko.bidmarket.user.controller.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

public class UserUpdateRequest {

  @NotBlank
  @Length(max = 20)
  private final String username;

  private final String image;

  public UserUpdateRequest(String username, String image) {
    this.username = username;
    this.image = image;
  }

  public String getUsername() {
    return username;
  }

  public String getImage() {
    return image;
  }
}
