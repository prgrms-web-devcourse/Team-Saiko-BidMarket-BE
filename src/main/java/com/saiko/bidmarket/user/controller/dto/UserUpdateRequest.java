package com.saiko.bidmarket.user.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

public class UserUpdateRequest {

  @Length(max = 20)
  @NotBlank
  @Nullable
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
