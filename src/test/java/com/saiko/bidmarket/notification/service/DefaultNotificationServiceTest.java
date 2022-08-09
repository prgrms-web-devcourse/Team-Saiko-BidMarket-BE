package com.saiko.bidmarket.notification.service;

import static com.saiko.bidmarket.notification.NotificationType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.entity.UnsignedLong;
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

  @Nested
  @DisplayName("findAllNotifications 메서드는")
  class DescribeFindAllNotifications {

    @Nested
    @DisplayName("userId가 null이면")
    class ContextWithUserIdNull {

      @Test
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException() {
        //given
        NotificationSelectRequest request = new NotificationSelectRequest(0, 1);

        //when, then
        assertThatThrownBy(() -> notificationService.findAllNotifications(null, request))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("request가 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItIllegalArgumentException() {
        //when, then
        assertThatThrownBy(
            () -> notificationService.findAllNotifications(UnsignedLong.valueOf(1L), null))
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
        User user = User.builder()
                        .username("제로")
                        .profileImage("image")
                        .provider("google")
                        .providerId("123")
                        .group(new Group())
                        .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Product product = Product.builder()
                                 .title("노트북 팝니다1")
                                 .description("싸요")
                                 .category(Category.DIGITAL_DEVICE)
                                 .minimumPrice(10000)
                                 .images(List.of("thumbnailImage"))
                                 .location(null)
                                 .writer(user)
                                 .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        Notification notification = Notification.builder()
                                                .user(user)
                                                .product(product)
                                                .type(
                                                    END_PRODUCT_FOR_WRITER_WITH_WINNER)
                                                .build();
        ReflectionTestUtils.setField(notification, "id", 1L);

        NotificationRepoDto notificationRepoDto = new NotificationRepoDto(notification.getId(),
                                                                          product.getId(),
                                                                          product.getTitle(),
                                                                          product.getThumbnailImage(),
                                                                          notification.getType(),
                                                                          notification.getCreatedAt(),
                                                                          notification.getUpdatedAt());
        NotificationSelectResponse notificationSelectResponse = NotificationSelectResponse.from(
            notificationRepoDto);

        NotificationSelectRequest request = new NotificationSelectRequest(0, 1);

        given(notificationRepository.findAllNotification(any(UnsignedLong.class),
                                                         any(NotificationSelectRequest.class)))
            .willReturn(List.of(notificationRepoDto));

        //when
        final List<NotificationSelectResponse> notificationSelectResponses =
            notificationService.findAllNotifications(UnsignedLong.valueOf(user.getId()), request);

        //then
        assertThat(notificationSelectResponses.size()).isEqualTo(1);
        assertThat(notificationSelectResponses.get(0).getId()).isEqualTo(
            notificationSelectResponse.getId());
        assertThat(notificationSelectResponses.get(0).getProductId()).isEqualTo(
            notificationSelectResponse.getProductId());
        assertThat(notificationSelectResponses.get(0).getTitle()).isEqualTo(
            notificationSelectResponse.getTitle());
        assertThat(notificationSelectResponses.get(0).getThumbnailImage()).isEqualTo(
            notificationSelectResponse.getThumbnailImage());
        assertThat(notificationSelectResponses.get(0).getType()).isEqualTo(
            notificationSelectResponse.getType());
        assertThat(notificationSelectResponses.get(0).getContent()).isEqualTo(
            notificationSelectResponse.getContent());
      }
    }
  }
}
