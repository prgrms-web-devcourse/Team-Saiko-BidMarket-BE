package com.saiko.bidmarket.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
  ROLE_USER(new String[]{"ROLE_USER"});

  private final String[] roles;
}
