package com.saiko.bidmarket.product.entity;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.entity.Role;

public class ProductTest {
  @Nested
  @DisplayName("finish 메소드는")
  class DescribeFinish {

    @Nested
    @DisplayName("입찰한 사람이 존재하지 않다면")
    class ContextNotBidder {

      @Test
      @DisplayName("낙찰자를 null로 반환한다")
      void ItResponseNull() {
        //given
        User writer = user("test");
        int minimumPrice = 10000;
        Product product = product(writer, minimumPrice);

        //when
        User winner = product.finish();

        //then
        assertThat(product)
            .extracting("progressed")
            .isEqualTo(false);
        assertThat(product)
            .extracting("winningPrice")
            .isNull();
        assertThat(winner).isNull();
      }
    }

    @Nested
    @DisplayName("입찰한 사람이 한 명이라면")
    class ContextOneBidder {

      @Test
      @DisplayName("최소 주문 금액을 낙찰가로 세팅하고 낙찰자를 반환한다")
      void ItSetWinningPriceToMinimumPriceAndResponseWinner() {
        //given
        User writer = user("writer");
        int minimumPrice = 10000;
        Product product = product(writer, minimumPrice);
        User bidder = user("bidder");
        Bidding bidding = bidding(20000L, bidder, product);
        ReflectionTestUtils.setField(product, "biddings", List.of(bidding));

        //when
        User winner = product.finish();

        //then
        assertThat(product)
            .extracting("progressed")
            .isEqualTo(false);
        assertThat(product)
            .extracting("winningPrice")
            .isEqualTo((long)minimumPrice);
        assertThat(bidding)
            .extracting("won")
            .isEqualTo(true);
        assertThat(winner)
            .usingRecursiveComparison()
            .isEqualTo(bidder);
      }
    }

    @Nested
    @DisplayName("입찰한 사람이 여러 명이라면")
    class ContextManyBidder {

      @Test
      @DisplayName("2등 입찰가 + 1000원을 낙찰가로 세팅하고 낙찰자를 반환한다")
      void ItSetWinningPriceAndResponseWinner() {
        //given
        User writer = user("writer");
        int minimumPrice = 10000;
        Product product = product(writer, minimumPrice);
        User bidderOne = user("bidderOne");
        User bidderTwo = user("bidderTwo");
        Bidding biddingOne = bidding(10000L, bidderOne, product);
        Bidding biddingTwo = bidding(20000L, bidderTwo, product);
        ReflectionTestUtils.setField(product, "biddings", List.of(biddingTwo, biddingOne));

        //when
        User winner = product.finish();

        //then
        assertThat(product)
            .extracting("progressed")
            .isEqualTo(false);
        assertThat(product)
            .extracting("winningPrice")
            .isEqualTo(11000L);
        assertThat(biddingTwo)
            .extracting("won")
            .isEqualTo(true);
        assertThat(biddingOne)
            .extracting("won")
            .isEqualTo(false);
        assertThat(winner)
            .usingRecursiveComparison()
            .isEqualTo(bidderTwo);
      }
    }
  }

  private User user(String name) {
    return User
        .builder()
        .username(name)
        .profileImage("imageURL")
        .provider("provider")
        .providerId("providerId")
        .role(Role.USER)
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

  private Bidding bidding(
      long biddingPrice,
      User bidder,
      Product product
  ) {
    return Bidding
        .builder()
        .biddingPrice(biddingPrice)
        .bidder(bidder)
        .product(product)
        .build();
  }
}
