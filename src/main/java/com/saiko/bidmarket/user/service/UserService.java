package com.saiko.bidmarket.user.service;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.saiko.bidmarket.user.controller.dto.UserUpdateRequest;
import com.saiko.bidmarket.user.entity.User;

public interface UserService {
  User findByProviderAndProviderId(String provider, String providerId);

  User join(OAuth2User oAuth2User, String authorizedClientRegistrationId);

  User findById(long id);

  void updateUser(long id, UserUpdateRequest request);
}
