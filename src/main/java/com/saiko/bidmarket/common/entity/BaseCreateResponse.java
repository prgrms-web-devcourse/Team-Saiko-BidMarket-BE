package com.saiko.bidmarket.common.entity;

import lombok.Getter;

@Getter
public class BaseCreateResponse {

  private final UnsignedLong id;

  public BaseCreateResponse(UnsignedLong id) {
    this.id = id;
  }
}
