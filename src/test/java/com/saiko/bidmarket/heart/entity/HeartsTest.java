package com.saiko.bidmarket.heart.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

public class HeartsTest {

  @Test
  @DisplayName("찜하기 누르기를 성공한다.")
  void toggleOnHeart() {
    //given
    User user = User
        .builder()
        .username("test")
        .profileImage("imageURL")
        .provider("provider")
        .providerId("providerId")
        .group(new Group())
        .build();
    ReflectionTestUtils.setField(user, "id", 1L);

    Product product = Product
        .builder()
        .title("title")
        .description("description")
        .minimumPrice(10000)
        .writer(user)
        .category(Category.ETC)
        .build();
    ReflectionTestUtils.setField(product, "id", 1L);

    Hearts hearts = new Hearts();
    Heart heart = Heart.of(user, product);

    //when
    boolean result = hearts.toggleHeart(heart);

    //then
    assertThat(result).isTrue();
    assertThat(hearts
                   .getHearts()
                   .get(0)
                   .getProduct())
        .extracting("id")
        .isEqualTo(product.getId());
    assertThat(hearts
                   .getHearts()
                   .get(0)
                   .getUser())
        .extracting("id")
        .isEqualTo(user.getId());
  }

  @Test
  @DisplayName("찜 취소하기 누르기를 성공한다.")
  void toggleOffHeart() {
    //given
    User user = User
        .builder()
        .username("test")
        .profileImage("imageURL")
        .provider("provider")
        .providerId("providerId")
        .group(new Group())
        .build();
    ReflectionTestUtils.setField(user, "id", 1L);

    Product product = Product
        .builder()
        .title("title")
        .description("description")
        .minimumPrice(10000)
        .writer(user)
        .category(Category.ETC)
        .build();
    ReflectionTestUtils.setField(product, "id", 1L);

    Hearts hearts = new Hearts();
    Heart heart = Heart.of(user, product);

    hearts.toggleHeart(heart);

    //when
    boolean result = hearts.toggleHeart(heart);

    //then
    assertThat(result).isFalse();
    assertThat(hearts.getHearts()).isEmpty();
  }
}
