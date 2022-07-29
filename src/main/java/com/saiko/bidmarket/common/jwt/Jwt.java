package com.saiko.bidmarket.common.jwt;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Jwt {

  private final String issuer;
  private final String clientSecret;
  private final int expirySecond;
  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;

  public Jwt(String issuer, String clientSecret,
             int expirySeconds) {

    this.issuer = issuer;
    this.clientSecret = clientSecret;
    this.expirySecond = expirySeconds;
    this.algorithm = Algorithm.HMAC512(clientSecret);
    this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
                                        .withIssuer(issuer)
                                        .build();
  }

  public String sign(Claims claims) {

    Date now = new Date();
    JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
    builder.withIssuer(issuer);
    builder.withIssuedAt(now);
    if (expirySecond > 0) {
      builder.withExpiresAt(new Date(now.getTime() + expirySecond * 1000L));
    }
    builder.withClaim("username", claims.username);
    builder.withArrayClaim("roles", claims.roles);
    return builder.sign(algorithm);
  }

  public Claims verify(String token) throws JWTVerificationException {

    return new Claims(jwtVerifier.verify(token));
  }

  public String getIssuer() {

    return issuer;
  }

  public String getClientSecret() {

    return clientSecret;
  }

  public int getExpirySecond() {

    return expirySecond;
  }

  public Algorithm getAlgorithm() {

    return algorithm;
  }

  public JWTVerifier getJwtVerifier() {

    return jwtVerifier;
  }

  public static class Claims {

    String username;
    String[] roles;
    Date iat;
    Date exp;

    private Claims() {
    }

    Claims(DecodedJWT decodedJWT) {

      Claim username = decodedJWT.getClaim("username");
      if (!username.isNull())
        this.username = username.asString();
      Claim roles = decodedJWT.getClaim("role");
      if (!roles.isNull()) {
        this.roles = roles.asArray(String.class);
      }
      this.iat = decodedJWT.getIssuedAt();
      this.exp = decodedJWT.getExpiresAt();
    }

    public static Claims from(String username, String[] roles) {

      Claims claims = new Claims();
      claims.username = username;
      claims.roles = roles;
      return claims;
    }

    public Map<String, Object> asMap() {

      Map<String, Object> map = new HashMap<>();
      map.put("username", username);
      map.put("roles", roles);
      map.put("iat", iat());
      return map;
    }

    long iat() {

      return iat != null ? iat.getTime() : -1;
    }

    long exp() {

      return exp != null ? exp.getTime() : -1;
    }

    void eraseIat() {

      iat = null;
    }

    void eraseExp() {

      exp = null;
    }

    @Override
    public String toString() {

      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
          .append("username", username)
          .append("roles", Arrays.toString(roles))
          .append("iat", iat)
          .append("exp", exp)
          .toString();
    }
  }
}
