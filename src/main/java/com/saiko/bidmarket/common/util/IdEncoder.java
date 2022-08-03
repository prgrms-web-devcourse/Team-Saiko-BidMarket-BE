package com.saiko.bidmarket.common.util;

import static org.apache.logging.log4j.util.Strings.*;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class IdEncoder {
  private static final String CODEC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final String CODEC_PATTERN = "^[ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789]*$";
  private final static int RADIX = 36;

  public static String encode(long id) {
    Assert.isTrue(id > 0, "Id must be positive");

    long param = id == Long.MAX_VALUE ? id : Long.MAX_VALUE - id;
    StringBuilder sb = new StringBuilder();
    while (param > 0) {
      sb.append(CODEC.charAt((int)(param % RADIX)));
      param /= RADIX;
    }

    return sb.toString();
  }

  public static long decode(String encodedId) {
    Assert.isTrue(isNotBlank(encodedId), "EncodedId must be provided");
    Assert.isTrue(isValidHashVal(encodedId), "Invalid hash value");

    long sum = 0;
    long power = 1;
    for (int i = 0; i < encodedId.length(); i++) {
      sum += CODEC.indexOf(encodedId.charAt(i)) * power;
      power *= RADIX;
    }
    return Long.MAX_VALUE - sum;
  }

  private static boolean isValidHashVal(String param) {
    return Pattern.matches(CODEC_PATTERN, param);
  }
}

