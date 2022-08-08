package com.saiko.bidmarket.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.saiko.bidmarket.notification.NotificationType;
import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.NotificationRepository;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class DefaultNotificationServiceTest {

  @InjectMocks
  private DefaultNotificationService notificationService;

  @Mock
  private NotificationRepository notificationRepository;

  private static User userOne;

  private static User userTwo;

  private static Product product;

  @BeforeAll
  void setUpDomain() {
    userOne = new User("testOne",
                      "imageURl",
                      "provider",
                      "providerId",
                      new Group());

    userTwo = new User("testTwo",
                       "imageURl",
                       "provider",
                       "providerId",
                       new Group());

    product = Product.builder()
                     .title("title")
                     .description("description")
                     .writer(userOne)
                     .build();
  }

  @Nested
  @DisplayName("create 메소드는")
  class DescribeCreate {

    @Nested
    @DisplayName("user가 null이면")
    class ContextNullUser {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // when, then
        assertThatThrownBy(
            () -> notificationService.create(null, NotificationType.END_PRODUCT_FOR_BIDDER,
                                             product)).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("messageType가 null이면")
    class ContextNullMessageType {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // when, then
        assertThatThrownBy(
            () -> notificationService.create(userOne, null, product)).isInstanceOf(
            IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("product가 null이면")
    class ContextNullProduct {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // when, then
        assertThatThrownBy(
            () -> notificationService.create(userOne, NotificationType.END_PRODUCT_FOR_BIDDER,
                                             null)).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("정상적인 값이 들어오면")
    class ContextValidData {

      @Test
      @DisplayName("notification을 생성한다")
      void ItCreateNotification() {
        //when
        notificationService.create(userOne, NotificationType.END_PRODUCT_FOR_BIDDER, product);

        //then
        verify(notificationRepository).save(any(Notification.class));
      }
    }
  }
}
