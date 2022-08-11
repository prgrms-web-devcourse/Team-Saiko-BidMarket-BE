package com.saiko.bidmarket.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.saiko.bidmarket.heart.entity.Heart;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;

public class UserTest {

  @Nested
  @DisplayName("toggleHeart 메소드는")
  class DescribeToggleHeart {

    @Nested
    @DisplayName("heart가 Null이면")
    class ContextNullHeart {

      @Test
      @DisplayName("낙찰자를 null로 반환한다")
      void ItThrowsIllegalArgumentException() {
        //given
        User user = User
            .builder()
            .username("test")
            .profileImage("imageURL")
            .provider("provider")
            .providerId("providerId")
            .group(new Group())
            .build();

        //when, then
        assertThatThrownBy(() -> user.toggleHeart(null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("heart가 유효한 값이면")
    class ContextValidHeart {

      @Test
      @DisplayName("찜 여부를 반환한다.")
      void ItReturnHeartBoolean() {
        //given
        User user = User
            .builder()
            .username("test")
            .profileImage("imageURL")
            .provider("provider")
            .providerId("providerId")
            .group(new Group())
            .build();

        Product product = Product
            .builder()
            .title("title")
            .description("description")
            .minimumPrice(10000)
            .writer(user)
            .category(Category.ETC)
            .build();

        Heart heart = Heart.of(user, product);

        //when
        boolean result = user.toggleHeart(heart);

        // then
        assertThat(result).isTrue();
      }
    }
  }
}
