package com.saiko.bidmarket.notification.service;

import static com.saiko.bidmarket.notification.NotificationType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;
import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.NotificationRepository;
import com.saiko.bidmarket.notification.repository.dto.NotificationRepoDto;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class DefaultNotificationServiceTest {
  @Mock
  NotificationRepository notificationRepository;

  @InjectMocks
  DefaultNotificationService notificationService;

  private User user(String name) {
    return User
        .builder()
        .username(name)
        .profileImage("imageURL")
        .provider("provider")
        .providerId("providerId")
        .group(new Group())
        .build();
  }

  private Product product(
      User writer,
      int minimumPrice
  ) {
    return Product
        .builder()
        .title("title")
        .description("description")
        .minimumPrice(minimumPrice)
        .writer(writer)
        .category(Category.ETC)
        .build();
  }

  private Notification notification(User user, Product product) {
    return Notification
        .builder()
        .user(user)
        .product(product)
        .type(
            END_PRODUCT_FOR_WRITER_WITH_WINNER)
        .build();
  }

  @Nested
  @DisplayName("findAllNotifications 메서드는")
  class DescribeFindAllNotifications {

    @Nested
    @DisplayName("request가 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> notificationService.findAllNotifications(1L, null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidParameters {

      @Test
      @DisplayName("UserBiddingSelectResponse 를 반환한다")
      void ItResponseNotificationSelectResponseList() {
        //given
        User user = user("제로");
        ReflectionTestUtils.setField(user, "id", 1L);

        Product product = product(user, 1000);
        ReflectionTestUtils.setField(product, "id", 1L);

        Notification notification = notification(user, product);
        ReflectionTestUtils.setField(notification, "id", 1L);

        NotificationRepoDto notificationRepoDto = new NotificationRepoDto(
            notification.getId(),
            product.getId(),
            product.getTitle(),
            product.getThumbnailImage(),
            notification.getType(),
            notification.isChecked(),
            notification.getCreatedAt(),
            notification.getUpdatedAt()
        );
        NotificationSelectResponse notificationSelectResponse = NotificationSelectResponse.from(
            notificationRepoDto);

        NotificationSelectRequest request = new NotificationSelectRequest(0, 1);

        given(notificationRepository.findAllNotification(
            anyLong(),
            any(NotificationSelectRequest.class)
        )).willReturn(List.of(notificationRepoDto));

        //when
        List<NotificationSelectResponse> responses = notificationService.findAllNotifications(
            user.getId(),
            request
        );

        //then
        assertThat(responses.size())
            .isEqualTo(1);
        assertThat(responses.get(0).getId())
            .isEqualTo(notificationSelectResponse.getId());
        assertThat(responses.get(0).getProductId())
            .isEqualTo(notificationSelectResponse.getProductId());
        assertThat(responses.get(0).getTitle())
            .isEqualTo(notificationSelectResponse.getTitle());
        assertThat(responses.get(0).getThumbnailImage())
            .isEqualTo(notificationSelectResponse.getThumbnailImage());
        assertThat(responses.get(0).getType())
            .isEqualTo(notificationSelectResponse.getType());
        assertThat(responses.get(0).getContent())
            .isEqualTo(notificationSelectResponse.getContent());
        assertThat(responses.get(0).isChecked())
            .isEqualTo(notificationSelectResponse.isChecked());
      }
    }
  }

  @Nested
  @DisplayName("checkNotification 메서드는")
  class DescribeCheckNotification {

    @Nested
    @DisplayName("notificationId에 해당하는 알림이 없다면")
    class ContextNotFoundProductById {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        long userId = Long.MAX_VALUE;
        long notificationId = Long.MAX_VALUE;

        given(notificationRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> notificationService.checkNotification(userId, notificationId))
            .isInstanceOf(NotFoundException.class);
        verify(notificationRepository, atLeastOnce()).findById(anyLong());
      }
    }

    @Nested
    @DisplayName("확인하려는 userId와 알림의 userId가 일치하지 않으면")
    class ContextNotMatchNotificationByUserId {

      @Test
      @DisplayName("AuthorizationServiceException을 발생시킨다.")
      void ItThrowsAuthorizationServiceException을() {
        // given
        long userId = 1L;
        long notificationId = 1L;

        User user = user("제로");
        ReflectionTestUtils.setField(user, "id", userId);

        Product product = product(user, 1000);
        ReflectionTestUtils.setField(product, "id", 1L);

        Notification notification = notification(user, product);
        ReflectionTestUtils.setField(notification, "id", notificationId);

        given(notificationRepository.findById(anyLong())).willReturn(Optional.of(notification));

        // when
        // then
        assertThatThrownBy(() -> notificationService.checkNotification(2L, notificationId))
            .isInstanceOf(AuthorizationServiceException.class);
        verify(notificationRepository, atLeastOnce()).findById(anyLong());
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidParameters {

      @Test
      @DisplayName("유저의 알림 조회 상태를 변경한다.")
      void ItCheckNotification() {
        // given
        long userId = 1L;
        long notificationId = 1L;

        User user = user("제로");
        ReflectionTestUtils.setField(user, "id", userId);

        Product product = product(user, 1000);
        ReflectionTestUtils.setField(product, "id", 1L);

        Notification notification = notification(user, product);
        ReflectionTestUtils.setField(notification, "id", notificationId);

        given(notificationRepository.findById(anyLong())).willReturn(Optional.of(notification));

        // when
        notificationService.checkNotification(userId, notificationId);

        // then
        verify(notificationRepository).findById(anyLong());
        assertThat(notification.isChecked()).isTrue();
      }
    }
  }
}
