package com.saiko.bidmarket.user.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.heart.entity.Heart;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.product.repository.dto.UserProductSelectQueryParameter;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserHeartResponse;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectRequest;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserSelectResponse;
import com.saiko.bidmarket.user.controller.dto.UserUpdateRequest;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class DefaultUserService implements UserService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final ProductRepository productRepository;
  private final BiddingRepository biddingRepository;
  private final UserRepository userRepository;
  private final GroupService groupService;

  @Override
  @Transactional(readOnly = true)
  public User findByProviderAndProviderId(String provider, String providerId) {
    Assert.hasText(provider, "Provider must be provided");
    Assert.hasText(providerId, "ProviderId must be provided");

    return userRepository.findByProviderAndProviderId(provider, providerId)
                         .orElseThrow(() -> new NotFoundException("User does not exist"));
  }

  @Override
  public User join(OAuth2User oAuth2User, String authorizedClientRegistrationId) {
    Assert.notNull(oAuth2User, "OAuth2User must be provided");
    Assert.hasText(authorizedClientRegistrationId,
                   "AuthorizedClientRegistrationId must be provided");

    String providerId = oAuth2User.getName();
    try {
      User user = findByProviderAndProviderId(authorizedClientRegistrationId, providerId);
      log.warn("Already exists: {} for (provider: {}, providerId: {})", user,
               authorizedClientRegistrationId, providerId);
      return user;
    } catch (NotFoundException e) {
      Map<String, Object> attributes = oAuth2User.getAttributes();

      String username = (String)attributes.get("name");
      String profileImage = (String)attributes.get("picture");

      log.info("username : {} profileImage : {}", username, profileImage);
      Group group = groupService.findByName("USER_GROUP");

      User user = new User(username, profileImage, authorizedClientRegistrationId, providerId,
                           group);
      return userRepository.save(user);
    }
  }

  @Override
  public UserSelectResponse findById(long id) {
    Assert.isTrue(id > 0, "userId must be positive");

    return UserSelectResponse.from(userRepository.findById(id)
                                                 .orElseThrow(() -> new NotFoundException(
                                                     "User does not exist")));
  }

  @Override
  public void updateUser(long id, UserUpdateRequest request) {
    Assert.notNull(request, "request must be provide");

    final User user = userRepository.findById(id)
                                    .orElseThrow(
                                        () -> new NotFoundException("User does not exist"));
    user.update(request.getUsername(), request.getProfileImage());
  }

  @Override
  public List<UserProductSelectResponse> findAllUserProducts(
      long userId,
      UserProductSelectRequest request
  ) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.notNull(request, "Request must be provided");

    final UserProductSelectQueryParameter queryParameter = UserProductSelectQueryParameter.of(
        userId, request);

    return productRepository.findAllUserProduct(queryParameter)
                            .stream()
                            .map(UserProductSelectResponse::from)
                            .collect(Collectors.toList());
  }

  @Override
  public List<UserBiddingSelectResponse> findAllUserBiddings(long userId,
                                                             UserBiddingSelectRequest request) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.notNull(request, "Request must be provided");

    return biddingRepository.findAllUserBidding(userId, request)
                            .stream()
                            .map((bidding) -> bidding.getProduct())
                            .map(UserBiddingSelectResponse::from)
                            .collect(Collectors.toList());
  }

  @Override
  public UserHeartResponse toggleHeart(long userId, long productId) {
    Assert.isTrue(userId > 0, "User id must be positive");
    Assert.isTrue(productId > 0, "Product id must be positive");

    User user = userRepository.findById(userId)
                                .orElseThrow(NotFoundException::new);

    Product product = productRepository.findById(productId)
                                       .orElseThrow(NotFoundException::new);

    Heart heart = Heart.of(user, product);
    return UserHeartResponse.from(user.toggleHeart(heart));
  }
}
