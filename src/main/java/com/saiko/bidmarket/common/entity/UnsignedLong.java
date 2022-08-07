package com.saiko.bidmarket.common.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "value")
public class UnsignedLong {
  static private final long MIN_VALUE = 1;

  @JsonValue
  private final long value;

  private UnsignedLong(long value) {
    this.value = value;
  }

  private static void valid(long value) {
    if (value < MIN_VALUE) {
      throw new IllegalArgumentException("UnsignedLong의 값은 1보다 작을 수 없습니다.");
    }
  }

  public static UnsignedLong valueOf(long value) {
    valid(value);
    return new UnsignedLong(value);
  }
}
