package com.saiko.bidmarket.user.service;

import static com.saiko.bidmarket.product.Category.*;
import static com.saiko.bidmarket.common.Sort.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.heart.entity.Heart;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
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

@ExtendWith(MockitoExtension.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class DefaultUserServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  ProductRepository productRepository;

  @Mock
  BiddingRepository biddingRepository;

  @Mock
  GroupService groupService;

  @InjectMocks
  DefaultUserService defaultUserService;

  @Order(1)
  @Nested
  @DisplayName("findByProviderAndProviderId 는")
  class DescribeFindByProviderAndProviderId {

    @Nested
    @DisplayName("provider 가 빈 값이면")
    class ContextWithProviderIsBlank {

      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(strings = {"\n", "\t"})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException(String src) {
        assertThrows(IllegalArgumentException.class,
                     () -> defaultUserService.findByProviderAndProviderId(src, "123"));
      }
    }

    @Nested
    @DisplayName("providerId 가 빈 값이면")
    class ContextWithProviderIdIsBlank {

      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(strings = {"\n", "\t"})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException(String src) {
        assertThrows(IllegalArgumentException.class,
                     () -> defaultUserService.findByProviderAndProviderId("123", src));

      }
    }
  }

  @Order(2)
  @Nested
  @DisplayName("join 메서드는")
  class DescribeJoin {

    @Nested
    @DisplayName("oAuth2User 값이 null 이면")
    class ContextWithOAuth2UserNull {

      @Test
      @DisplayName("IllegalArgumentException")
      void ItIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                     () -> defaultUserService.join(null, "123"));
      }
    }

    @Nested
    @DisplayName("authorizedClientRegistrationId 값이 빈값 이면")
    class ContextWithBlankAuthorizedClientRegistrationId {

      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(strings = {"\t", "\n"})
      @DisplayName("IllegalArgumentException 에러를 발생 시킨다")
      void ItThrowsIllegalArgumentException(String src) {
        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(),
                                                      Map.of("1", "1"),
                                                      "1");

        assertThrows(IllegalArgumentException.class,
                     () -> defaultUserService.join(oAuth2User, src));
      }
    }

    @Nested
    @DisplayName("해당 하는 유저가 존재하면")
    class ContextWithUserAlreadyExist {

      @Test
      @DisplayName("해당 유저를 반환한다")
      void ItReturnUser() {
        //given
        String provider = "google";
        String providerId = "testProviderId";
        User user = new User("username", "test", provider, providerId, new Group());
        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(),
                                                      Map.of("name", providerId), "name");
        given(userRepository.findByProviderAndProviderId(anyString(), anyString())).willReturn(
            Optional.of(user));
        //when
        User joinedUser = defaultUserService.join(oAuth2User, provider);

        //then
        String joinedUserProvider = (String)ReflectionTestUtils.getField(joinedUser, "provider");
        String joinedUserProviderId = (String)ReflectionTestUtils.getField(joinedUser,
                                                                           "providerId");
        assertAll(
            () -> assertEquals(provider, joinedUserProvider),
            () -> assertEquals(providerId, joinedUserProviderId)
        );
      }
    }

    @Nested
    @DisplayName("해당 하는 유저가 존재하지 않으면")
    class ContextWithNotExist {

      @Test
      @DisplayName("유저를 생성하고 반환한다.")
      void It() {
        //given
        Group userGroup = new Group();
        ReflectionTestUtils.setField(userGroup, "name", "USER_GROUP");
        given(groupService.findByName(anyString())).willReturn(userGroup);
        given(userRepository.findByProviderAndProviderId(anyString(), anyString()))
            .willReturn(Optional.empty());

        given(groupService.findByName(anyString())).willReturn(new Group());
        Map<String, Object> attributes = Map.of("name", "test", "picture", "testUrl");
        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "name");

        //when
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        User user = defaultUserService.join(oAuth2User, "google");

        //then
        String savedUserUsername = (String)ReflectionTestUtils.getField(user, User.class,
                                                                        "username");
        String savedUserProfileImg = (String)ReflectionTestUtils.getField(user, User.class,
                                                                          "profileImage");

        assertAll(
            () -> assertEquals("test", savedUserUsername),
            () -> assertEquals("testUrl", savedUserProfileImg)
        );
      }
    }

  }

  @Nested
  @DisplayName("findById 메서드는")
  class DescribeFindById {

    @Nested
    @DisplayName("id에 음수나 0이 들어오면")
    class ContextReceiveNegativeValue {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException을 반환한다.")
      void itThrowIllegalArgumentException(long id) {
        //then
        assertThatThrownBy(() -> defaultUserService.findById(id))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("존재하지 않는 userId가 들어오면")
    class ContextNotExistUserId {

      @Test
      @DisplayName("NotFoundException을 반환한다.")
      void it() {
        //given
        final long notExistUserId = 1;

        //then
        assertThatThrownBy(() -> defaultUserService.findById(notExistUserId))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("존재하는 userId가 들어오면")
    class ContextExistUserId {

      @Test
      @DisplayName("해당 유저를 반환한다.")
      void itReturnExistUser() {
        //given
        final long existUserId = 1;
        final User existUser = new User(
            "test",
            "test",
            "test",
            "test",
            new Group()
        );
        ReflectionTestUtils.setField(existUser, "id", 1L);
        final UserSelectResponse expected = UserSelectResponse.from(existUser);

        //when
        when(userRepository.findById(existUserId)).thenReturn(Optional.of(existUser));

        final UserSelectResponse actualUser = defaultUserService.findById(existUserId);

        //then
        Assertions.assertThat(actualUser.getUsername()).isEqualTo(expected.getUsername());
        Assertions.assertThat(actualUser.getProfileImage())
                  .isEqualTo(expected.getProfileImage());
      }
    }
  }

  @Nested
  @DisplayName("updateUser 메서드는")
  class DescribeUpdateUser {

    @Nested
    @DisplayName("null 값인 userUpdateRequest가 인자로 들어오면")
    class ContextReceiveNullUserUpdateRequest {

      @Test
      @DisplayName("IllegalArgumentException을 반환한다.")
      void itThrowIllegalArgumentException() {
        //given
        final long userId = 1;
        final UserUpdateRequest request = null;

        //then
        assertThatThrownBy(() -> defaultUserService.updateUser(userId, request))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("존재하지 않는 유저의 id값이 들어오면")
    class ContextReceiveNotExistUserId {

      @Test
      @DisplayName("NotFoundException을 반환한다.")
      void itThrowNotFoundException() {
        //given
        final long userId = 1;
        final UserUpdateRequest request = new UserUpdateRequest("test", "test");

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> defaultUserService.updateUser(userId, request))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("존재하는 유저와 유효한 userUpdateRequest를 인자로 받으면")
    class ContextReceiveValidUserAndUpdateRequest {

      @Test
      @DisplayName("해당 유저 정보를 update한다.")
      void itUpdateUser() {
        //given
        final long userId = 1;
        final UserUpdateRequest request = new UserUpdateRequest("update", "update");
        final User targetUser = new User("before",
                                         "before",
                                         "provider",
                                         "providerId",
                                         new Group());

        ReflectionTestUtils.setField(targetUser, "id", 1L);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(targetUser));

        defaultUserService.updateUser(userId, request);

        final String expected = "update";
        final String actualUsername = targetUser.getUsername();
        final String actualProfileImage = targetUser.getProfileImage();

        //then
        Assertions.assertThat(actualUsername).isEqualTo(expected);
        Assertions.assertThat(actualProfileImage).isEqualTo(expected);
      }
    }
  }

  @Nested
  @DisplayName("findAllUserProducts 메서드는")
  class DescribeFindAllUserProducts {

    @Nested
    @DisplayName("userId 가 양수가 아니면")
    class ContextWithNotPositive {

      @ParameterizedTest
      @ValueSource(longs = {0, -1L, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException(long src) {
        UserProductSelectRequest request = new UserProductSelectRequest(0, 1, END_DATE_ASC);

        assertThatThrownBy(() -> defaultUserService.findAllUserProducts(src, request))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("request 가 null이면")
    class ContextWithNullRequest {

      @ParameterizedTest
      @ValueSource(longs = {1, Long.MAX_VALUE})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItIllegalArgumentException(long src) {
        assertThatThrownBy(() -> defaultUserService.findAllUserProducts(src, null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidParameters {

      @Test
      @DisplayName("UserProductSelectResponse 를 반환한다")
      void ItResponseUserProductSelectResponse() {
        //given
        User writer = new User("아루루", "image", "google", "1234", new Group());

        UserProductSelectRequest request = new UserProductSelectRequest(0, 1, END_DATE_ASC);

        Product product = Product.builder()
                                 .title("감자 팜")
                                 .description("zz")
                                 .location("강원도")
                                 .category(ETC)
                                 .minimumPrice(15000)
                                 .images(List.of("testUrl"))
                                 .writer(writer)
                                 .build();
        long productId = 1L;
        ReflectionTestUtils.setField(product, "id", productId);

        given(productRepository.findAllUserProduct(any(UserProductSelectQueryParameter.class)))
            .willReturn(List.of(product));

        //when
        final List<UserProductSelectResponse> allUserProducts = defaultUserService.findAllUserProducts(
            1L, request);

        //then
        assertThat(allUserProducts.size()).isEqualTo(1);
        assertThat(allUserProducts.get(0).getId()).isEqualTo(productId);
      }
    }
  }

  @Nested
  @DisplayName("findAllUserBiddings 메서드는")
  class DescribeFindAllUserBiddings {

    @Nested
    @DisplayName("userId 가 양수가 아니면")
    class ContextWithNotPositive {

      @ParameterizedTest
      @ValueSource(longs = {0, -1L, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException(long src) {
        UserBiddingSelectRequest request = new UserBiddingSelectRequest(0, 1, END_DATE_ASC);

        assertThatThrownBy(() -> defaultUserService.findAllUserBiddings(src, request))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("request 가 null이면")
    class ContextWithNullRequest {

      @ParameterizedTest
      @ValueSource(longs = {1, Long.MAX_VALUE})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItIllegalArgumentException(long src) {
        assertThatThrownBy(() -> defaultUserService.findAllUserBiddings(src, null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidParameters {

      @Test
      @DisplayName("UserBiddingSelectResponse 를 반환한다")
      void ItResponseUserBiddingSelectResponse() {
        //given
        User writer = new User("아루루", "image", "google", "1234", new Group());

        UserBiddingSelectRequest request = new UserBiddingSelectRequest(0, 1, END_DATE_ASC);

        Product product = Product.builder()
                                 .title("감자 팜")
                                 .description("zz")
                                 .location("강원도")
                                 .category(ETC)
                                 .minimumPrice(15000)
                                 .images(List.of("testUrl"))
                                 .writer(writer)
                                 .build();
        long productId = 1L;
        ReflectionTestUtils.setField(product, "id", productId);

        User bidder = new User("제로", "image", "google", "1234", new Group());
        long userId = 1L;
        ReflectionTestUtils.setField(bidder, "id", userId);

        Bidding bidding = Bidding.builder()
                                 .product(product)
                                 .bidder(bidder)
                                 .biddingPrice(BiddingPrice.valueOf(20000))
                                 .build();

        given(biddingRepository.findAllUserBidding(anyLong(), any(UserBiddingSelectRequest.class)))
            .willReturn(List.of(bidding));

        //when
        List<UserBiddingSelectResponse> result = defaultUserService.findAllUserBiddings(1,
                                                                                        request);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(productId);
      }
    }
  }

  @Nested
  @DisplayName("toggleHeart 메서드는")
  class DescribeToggleHeart {

    @Nested
    @DisplayName("userId에 음수나 0이 들어오면")
    class ContextReceiveNegativeUserId {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException을 반환한다.")
      void itThrowIllegalArgumentException(long userId) {
        //then
        assertThatThrownBy(() -> defaultUserService.toggleHeart(userId, 1L))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("존재하지 않는 userId가 들어오면")
    class ContextNotExistUserId {

      @Test
      @DisplayName("NotFoundException을 반환한다.")
      void itResponseNotFoundException() {
        //given
        final long notExistUserId = 1;

        when(userRepository.findById(notExistUserId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> defaultUserService.toggleHeart(notExistUserId, 1L))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("productId에 음수나 0이 들어오면")
    class ContextReceiveNegativeProductId {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException을 반환한다.")
      void itThrowIllegalArgumentException(long productId) {
        //then
        assertThatThrownBy(() -> defaultUserService.toggleHeart(1L, productId))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("존재하지 않는 productId가 들어오면")
    class ContextNotExistProductId {

      @Test
      @DisplayName("NotFoundException을 반환한다.")
      void itResponseNotFoundException() {
        //given
        final long userId = 1L;
        final long notExistProductId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(User
                                                                         .builder()
                                                                         .username("레이")
                                                                         .profileImage("image")
                                                                         .provider("google")
                                                                         .providerId("123")
                                                                         .group(new Group())
                                                                         .build()));
        when(productRepository.findById(notExistProductId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> defaultUserService.toggleHeart(1L, notExistProductId))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidParameters {

      @Test
      @DisplayName("찜하기를 수행하고 UserHeartResponse 를 반환한다")
      void itCreateHeartAndResponseUserHeartResponse() {
        //given
        Long userId = 1L;
        Long productId = 1L;
        User user = User
            .builder()
            .username("레이")
            .profileImage("image")
            .provider("google")
            .providerId("123")
            .group(new Group())
            .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Product product = Product
            .builder()
            .title("책 팔아요")
            .writer(user)
            .description("깨끗해요")
            .images(List.of("image"))
            .category(CHILDREN_BOOK)
            .minimumPrice(10000)
            .location("직거래 안해요")
            .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //when
        UserHeartResponse userHeartResponse = defaultUserService.toggleHeart(userId, productId);

        //then
        verify(userRepository).findById(anyLong());
        verify(productRepository).findById(anyLong());
        assertThat(user.getHearts().getHearts()).hasSize(1);
        assertThat(userHeartResponse.isHeart()).isTrue();
      }

      @Test
      @DisplayName("찜하기를 수행하고 UserHeartResponse 를 반환한다")
      void itDeleteHeartAndResponseUserHeartResponse() {
        //given
        Long userId = 1L;
        Long productId = 1L;
        User user = User
            .builder()
            .username("레이")
            .profileImage("image")
            .provider("google")
            .providerId("123")
            .group(new Group())
            .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Product product = Product
            .builder()
            .title("책 팔아요")
            .writer(user)
            .description("깨끗해요")
            .images(List.of("image"))
            .category(CHILDREN_BOOK)
            .minimumPrice(10000)
            .location("직거래 안해요")
            .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        Heart heart = Heart
            .builder()
            .user(user)
            .product(product)
            .build();
        ReflectionTestUtils.setField(heart, "id", 1L);

        user.toggleHeart(heart);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //when
        UserHeartResponse userHeartResponse = defaultUserService.toggleHeart(userId, productId);

        //then
        verify(userRepository).findById(anyLong());
        verify(productRepository).findById(anyLong());
        assertThat(user.getHearts().getHearts()).hasSize(0);
        assertThat(userHeartResponse.isHeart()).isFalse();
      }
    }
  }
}
