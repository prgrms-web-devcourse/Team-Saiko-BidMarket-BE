package com.saiko.bidmarket.common.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "jwt")
@ConstructorBinding
public class JwtConfig {

  private final String header;

  private final String issuer;

  private final String clientSecret;

  private final int expirySeconds;

  public JwtConfig(String header, String issuer, String clientSecret, int expirySeconds) {
    this.header = header;
    this.issuer = issuer;
    this.clientSecret = clientSecret;
    this.expirySeconds = expirySeconds;
  }

  public String getHeader() {

    return header;
  }

  public String getIssuer() {

    return issuer;
  }

  public String getClientSecret() {

    return clientSecret;
  }

  public int getExpirySeconds() {

    return expirySeconds;
  }

  @Override
  public String toString() {

    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("header", header)
        .append("issuer", issuer)
        .append("clientSecret", clientSecret)
        .append("expireSecond", expirySeconds)
        .build();
  }
}
