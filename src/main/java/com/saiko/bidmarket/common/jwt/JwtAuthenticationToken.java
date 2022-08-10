package com.saiko.bidmarket.common.jwt;

import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final Object principal;

  private String credentials;

  public JwtAuthenticationToken(
      String principal,
      String credentials
  ) {

    super(null);
    super.setAuthenticated(false);

    this.principal = principal;
    this.credentials = credentials;
  }

  public JwtAuthenticationToken(
      Object principal,
      String credentials,
      Collection<? extends GrantedAuthority> authorities
  ) {

    super(authorities);
    super.setAuthenticated(true);

    this.principal = principal;
    this.credentials = credentials;
  }

  @Override
  public String getCredentials() {

    return credentials;
  }

  @Override
  public Object getPrincipal() {

    return principal;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    if (isAuthenticated) {
      throw new IllegalArgumentException("Cannot set this token to trusted"
                                             + " - use constructor which takes a "
                                             + "GrantedAuthority list instead");
    }

    super.setAuthenticated(false);
  }

  @Override
  public void eraseCredentials() {

    super.eraseCredentials();
    credentials = null;
  }

  @Override
  public String toString() {

    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("principal", principal)
        .append("credential", "[Protected]")
        .build();
  }
}
