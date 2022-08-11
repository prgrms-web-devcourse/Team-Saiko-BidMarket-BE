package com.saiko.bidmarket.user.service;

import java.util.List;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserUpdateRequest;
import com.saiko.bidmarket.user.entity.User;

public interface UserService {
  User findByProviderAndProviderId(String provider, String providerId);

  User join(OAuth2User oAuth2User, String authorizedClientRegistrationId);

  UserSelectResponse findById(long id);

  void updateUser(long id, UserUpdateRequest request);

  List<UserProductSelectResponse> findAllUserProducts(long userId,
                                                      UserProductSelectRequest request);

  List<UserBiddingSelectResponse> findAllUserBiddings(long userId,
                                                      UserBiddingSelectRequest request);

  void deleteUser(long userId);
}
