package com.saiko.bidmarket.product;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
  SELLER("판매자"),
  BIDDER("입찰자"),
  ;
  private final String displayName;

  Role(String displayName) {
    this.displayName = displayName;
  }

  @JsonValue
  public String getDisplayName() {
    return displayName;
  }
}
