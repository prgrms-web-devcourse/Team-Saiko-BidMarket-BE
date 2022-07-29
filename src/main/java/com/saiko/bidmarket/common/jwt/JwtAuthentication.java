package com.saiko.bidmarket.common.jwt;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

public class JwtAuthentication {

  public final String token;

  public final String userid;

  public JwtAuthentication(String token, String userid) {
    Assert.hasText(token, "token must be provided");
    Assert.hasText(userid, "username must be provided");

    this.token = token;
    this.userid = userid;
  }

  @Override
  public String toString() {

    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("token", token)
        .append("userId", userid)
        .build();
  }
}
