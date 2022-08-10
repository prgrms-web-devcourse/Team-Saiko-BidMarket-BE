package com.saiko.bidmarket.common.jwt;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

public class JwtAuthentication {

  private final String token;

  private final Long userId;

  public JwtAuthentication(
      String token,
      Long userId
  ) {
    Assert.hasText(token, "token must be provided");
    Assert.isTrue(userId > 0, "userId must be provided");

    this.token = token;
    this.userId = userId;
  }

  @Override
  public String toString() {

    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("token", token)
        .append("userId", userId)
        .build();
  }

  public Long getUserId() {
    return userId;
  }
}
