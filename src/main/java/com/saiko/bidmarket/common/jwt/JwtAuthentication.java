package com.saiko.bidmarket.common.jwt;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

public class JwtAuthentication {

  private final String token;

  private final String userId;

  public JwtAuthentication(String token, String userId) {
    Assert.hasText(token, "token must be provided");
    Assert.hasText(userId, "userId must be provided");

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

  public String getUserId() {
    return userId;
  }
}
