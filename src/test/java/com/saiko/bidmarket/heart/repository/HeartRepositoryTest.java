package com.saiko.bidmarket.heart.repository;

import static com.saiko.bidmarket.common.Sort.*;
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
import com.saiko.bidmarket.heart.entity.Heart;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.controller.dto.UserHeartSelectRequest;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.entity.UserRole;
import com.saiko.bidmarket.user.repository.UserRepository;

@DataJpaTest()
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QueryDslConfig.class)
public class HeartRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private HeartRepository heartRepository;

  private User getUser(String name, String providerId) {
    return User
        .builder()
        .username(name)
        .provider("test")
        .providerId(providerId)
        .profileImage("test")
        .userRole(UserRole.ROLE_USER)
        .build();
  }

  private Product getProduct(
      String title,
      User writer
  ) {
    return Product
        .builder()
        .title(title)
        .description("test")
        .images(List.of("image"))
        .writer(writer)
        .category(Category.BEAUTY)
        .build();
  }

  @Nested
  @DisplayName("findAllUserHeart 메소드는")
  class DescribeFindAllUserHeart {

    @Nested
    @DisplayName("UserHeartSelectRequest 가 null 이라면")
    class ContextWithUserUserHeartSelectRequestNull {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //when, then
        assertThatThrownBy(() -> heartRepository.findAllUserHeart(1, null))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("올바른 정보가 넘어온다면")
    class ContextWithValidData {

      @Test
      @DisplayName("페이징 처리된 입찰 상품 목록을 반환한다")
      void itReturnHeartProductList() {
        // given
        User user = userRepository.save(getUser("test1", "test1"));
        Product product = productRepository.save(getProduct("test", user));
        Heart heart = Heart.of(user, product);
        heart.toggle();
        Heart savedHeart = heartRepository.save(heart);

        UserHeartSelectRequest request = new UserHeartSelectRequest(0, 1, END_DATE_ASC);

        // when
        List<Heart> result = heartRepository.findAllUserHeart(user.getId(), request);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(savedHeart);
      }
    }
  }
}
