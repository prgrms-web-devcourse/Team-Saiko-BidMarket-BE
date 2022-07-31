package com.saiko.bidmarket.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
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

import com.saiko.bidmarket.common.exception.NotFoundException;
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
        Assertions.assertThatThrownBy(() -> defaultUserService.findById(id))
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
        Assertions.assertThatThrownBy(() -> defaultUserService.findById(notExistUserId))
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

        //when
        when(userRepository.findById(existUserId)).thenReturn(Optional.of(existUser));

        final User actualUser = defaultUserService.findById(existUserId);

        //then
        Assertions.assertThat(actualUser).isEqualTo(existUser);
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
        Assertions.assertThatThrownBy(() -> defaultUserService.updateUser(userId, request))
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
        Assertions.assertThatThrownBy(() -> defaultUserService.updateUser(userId, request))
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

}
