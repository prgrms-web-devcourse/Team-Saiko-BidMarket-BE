package com.saiko.bidmarket.common.entity;

import lombok.Getter;

@Getter
public class LongId {
  static private final long MIN_VALUE = 1;

  private final long value;

  public LongId(long value) {
    valid(value);
    this.value = value;
  }

  private void valid(long value) {
    if (value < MIN_VALUE) {
      throw new IllegalArgumentException("LongId의 값은 1보다 작을 수 없습니다.");
    }
  }
}
