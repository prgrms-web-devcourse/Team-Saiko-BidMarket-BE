package com.saiko.bidmarket.user.controller.dto;

public class UserEncodedIdResponse {

  private final String encodedUserId;

  public UserEncodedIdResponse(String encodedUserId) {
    this.encodedUserId = encodedUserId;
  }

  public String getEncodedUserId() {
    return encodedUserId;
  }
}
