package com.saiko.bidmarket.user.entity;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

  USER(new String[]{"ROLE_USER"});

  private final String[] authority;
}
