package com.saiko.bidmarket.product.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

public class ProductTest {
  @Nested
  @DisplayName("finish 메소드는")
  class DescribeFinish {

    @Nested
    @DisplayName("winningPrice가 null이면")
    class ContextNullWinningPrice {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        //given
        User writer = user();
        Product product = product(writer);

        //when, then
        assertThatCode(() -> product.finish(null)).isInstanceOf(
            IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("winningPrice가 유효한 값이면")
    class ContextValidWinningPrice {

      @Test
      @DisplayName("경매 종료 여부를 수정하고 낙찰가를 세팅한다.")
      void ItUpdateProgressedAndSetWinningPrice() {
        //given
        User writer = user();
        Product product = product(writer);
        Long winningPrice = 20000L;

        //when
        product.finish(winningPrice);

        //then
        assertThat(product).extracting("progressed").isEqualTo(false);
        assertThat(product).extracting("winningPrice").isEqualTo(winningPrice);
      }
    }
  }

  private User user() {
    return User.builder()
               .username("test")
               .profileImage("imageURL")
               .provider("provider")
               .providerId("providerId")
               .group(new Group())
               .build();
  }

  private Product product(User writer) {
    return Product.builder()
                  .title("title")
                  .description("description")
                  .minimumPrice(10000)
                  .writer(writer)
                  .category(Category.ETC)
                  .build();
  }
}
