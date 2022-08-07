package com.saiko.bidmarket.bidding.entity;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

public class BiddingsTest {

  @Nested
  @DisplayName("selectWinner 메소드는")
  class DescribeSelectWinner {

    @Nested
    @DisplayName("minimumPrice가 음수면")
    class ContextNegativeMinimumPrice {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        //given
        User writer = writer();
        Product product = product(writer);
        User bidder = bidder();
        BiddingPrice biddingPrice = BiddingPrice.valueOf(10000L);
        Bidding bidding = bidding(biddingPrice, bidder, product);
        Biddings biddings = new Biddings(List.of(bidding));
        int minimumPrice = -1;

        //when, then
        assertThatCode(() -> biddings.selectWinner(minimumPrice)).isInstanceOf(
            IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("비딩한 사람이 아무도 없다면")
    class ContextNotExistBidder {

      @Test
      @DisplayName("null을 반환한다.")
      void ItResponseNull() {
        //given
        Biddings biddings = new Biddings(Collections.emptyList());
        int minimumPrice = 10000;

        //when
        Long winningPrice = biddings.selectWinner(minimumPrice);

        //then
        assertThat(winningPrice).isNull();
      }
    }

    @Nested
    @DisplayName("비딩한 사람이 한 명이라면")
    class ContextOneBidder {

      @Test
      @DisplayName("최소 주문 금액을 반환한다")
      void ItResponseMinimumPrice() {
        //given
        User writer = writer();
        Product product = product(writer);
        User bidder = bidder();
        BiddingPrice biddingPrice = BiddingPrice.valueOf(10000L);
        Bidding bidding = bidding(biddingPrice, bidder, product);
        Biddings biddings = new Biddings(List.of(bidding));
        int minimumPrice = 10000;

        //when
        Long winningPrice = biddings.selectWinner(minimumPrice);

        //then
        assertThat(bidding).extracting("won").isEqualTo(true);
        assertThat(winningPrice).isEqualTo(minimumPrice);
      }
    }

    @Nested
    @DisplayName("비딩한 사람이 여러 명이라면")
    class ContextManyBidder {

      @Test
      @DisplayName("2등 입찰가 + 1000원을 반환한다.")
      void ItResponseMinimumPrice() {
        //given
        User writer = writer();
        Product product = product(writer);
        User bidderOne = bidder();
        User bidderTwo = bidder();
        BiddingPrice biddingPriceOne = BiddingPrice.valueOf(10000L);
        BiddingPrice biddingPriceTwo = BiddingPrice.valueOf(20000L);
        Bidding biddingOne = bidding(biddingPriceOne, bidderOne, product);
        Bidding biddingTwo = bidding(biddingPriceTwo, bidderTwo, product);
        Biddings biddings = new Biddings(List.of(biddingTwo, biddingOne));
        int minimumPrice = 10000;

        //when
        Long winningPrice = biddings.selectWinner(minimumPrice);

        //then
        assertThat(biddingTwo).extracting("won").isEqualTo(true);
        assertThat(biddingOne).extracting("won").isEqualTo(false);
        assertThat(winningPrice).isEqualTo(biddingOne.getBiddingPrice() + 1000L);
      }
    }
  }

  private User writer() {
    return User.builder()
               .username("testWriter")
               .profileImage("imageURL")
               .provider("provider")
               .providerId("providerId")
               .group(new Group())
               .build();
  }

    private User bidder() {
      return User.builder()
                 .username("testBidder")
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

  private Bidding bidding(BiddingPrice biddingPrice, User bidder, Product product) {
    return Bidding.builder()
        .biddingPrice(biddingPrice)
        .bidder(bidder)
        .product(product)
        .build();
  }
}
