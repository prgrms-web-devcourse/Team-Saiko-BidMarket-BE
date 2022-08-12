package com.saiko.bidmarket.notification.repository;

import static com.saiko.bidmarket.notification.NotificationType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.dto.NotificationRepoDto;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.GroupRepository;
import com.saiko.bidmarket.user.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QueryDslConfig.class)
public class NotificationRepositoryTest {
  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private NotificationRepository notificationRepository;

  @Nested
  @DisplayName("findAllNotification 메소드는")
  class DescribeFindAllNotification {

    @DisplayName("userId 가 null 이라면")
    class ContextWithUserIdNull {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //given
        NotificationSelectRequest request = new NotificationSelectRequest(0, 1);

        //when, then
        assertThatThrownBy(
            () -> notificationRepository.findAllNotification(null, request))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("notificationSelectRequest 가 null 이라면")
    class ContextWithNotificationSelectRequestNull {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //when, then
        assertThatThrownBy(
            () -> notificationRepository.findAllNotification(UnsignedLong.valueOf(1L), null))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("올바른 정보가 넘어온다면")
    class ContextWithValidData {

      @Test
      @DisplayName("페이징 처리된 알림 전체 목록을 반환한다")
      void ItReturnNotificationList() {
        //given
        Group group = groupRepository.findById(1L).get();

        User user = userRepository.save(User.builder()
                                            .username("제로")
                                            .profileImage("image")
                                            .provider("google")
                                            .providerId("123")
                                            .group(group)
                                            .build());

        Product product = productRepository.save(Product.builder()
                                                        .title("노트북 팝니다1")
                                                        .description("싸요")
                                                        .category(Category.DIGITAL_DEVICE)
                                                        .minimumPrice(10000)
                                                        .images(List.of("thumbnailImage"))
                                                        .location(null)
                                                        .writer(user)
                                                        .build());

        Notification notification = notificationRepository.save(Notification.builder()
                                                                            .user(user)
                                                                            .product(product)
                                                                            .type(
                                                                                END_PRODUCT_FOR_WRITER_WITH_WINNER)
                                                                            .build());

        NotificationSelectRequest request = new NotificationSelectRequest(0, 1);

        //when
        List<NotificationRepoDto> result = notificationRepository.findAllNotification(
            UnsignedLong.valueOf(user.getId()), request);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(notification.getId());
        assertThat(result.get(0).getProductId()).isEqualTo(product.getId());
        assertThat(result.get(0).getTitle()).isEqualTo(product.getTitle());
        assertThat(result.get(0).getThumbnailImage()).isEqualTo(product.getThumbnailImage());
        assertThat(result.get(0).getType()).isEqualTo(notification.getType());
        assertThat(result.get(0).isChecked()).isFalse();
      }
    }
  }
}
