package com.saiko.bidmarket.user.controller.dto;

public class UserEncodedIdResponse {

  private final String encodedId;

  public UserEncodedIdResponse(String encodedUserId) {
    this.encodedId = encodedUserId;
  }

  public String getEncodedId() {
    return encodedId;
  }
}
