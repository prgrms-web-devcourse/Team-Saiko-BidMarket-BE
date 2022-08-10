package com.saiko.bidmarket.util;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.saiko.bidmarket.common.jwt.JwtAuthentication;
import com.saiko.bidmarket.common.jwt.JwtAuthenticationToken;

public class WithMockCustomLoginUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomLoginUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomLoginUser annotation) {

    final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

    final String token = "mockToken";
    final Long userId = 1L;
    final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

    JwtAuthenticationToken authentication =
        new JwtAuthenticationToken(new JwtAuthentication(token, userId), null,
                                   authorities
        );

    securityContext.setAuthentication(authentication);

    return securityContext;
  }
}
