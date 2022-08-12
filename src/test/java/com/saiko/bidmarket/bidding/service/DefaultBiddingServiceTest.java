package com.saiko.bidmarket.bidding.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateRequest;
import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateResponse;
import com.saiko.bidmarket.bidding.controller.dto.BiddingPriceResponse;
import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultBiddingServiceTest {

  @InjectMocks
  private DefaultBiddingService biddingService;

  @Mock
  private BiddingRepository biddingRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProductRepository productRepository;

  private static User bidder = new User(
      "test",
      "imageURl",
      "provider",
      "providerId",
      new Group()
  );
  private static long bidderId = 1L;

  private static User writer = new User(
      "test",
      "imageURl",
      "provider",
      "providerId",
      new Group()
  );

  private static long writerId = 1L;

  private static Product product = Product
      .builder()
      .title("title")
      .description("description")
      .writer(writer)
      .build();

  private static long productId = 1L;

  @BeforeAll
  static void setUpDomain() {
    ReflectionTestUtils.setField(bidder, "id", bidderId);
    ReflectionTestUtils.setField(writer, "id", writerId);
    ReflectionTestUtils.setField(product, "id", productId);
  }

  @Nested
  @DisplayName("create 메소드는")
  class DescribeCreateMethod {

    @Nested
    @DisplayName("BiddingCreateDto의 productId에 해당하는 상품이 없으면")
    class ContextNotExistProduct {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        long biddingPrice = 1000L;
        long productId = product.getId();
        long bidderId = bidder.getId();

        BiddingCreateRequest createRequest = new BiddingCreateRequest(productId, biddingPrice);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(bidderId, createRequest))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("해당 상품의 작성자와 비더가 같다면")
    class ContextProductWriterAndBidderSame {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        long biddingPrice = 1000L;
        long productId = product.getId();
        long bidderId = bidder.getId();

        BiddingCreateRequest createRequest = new BiddingCreateRequest(productId, biddingPrice);

        ReflectionTestUtils.setField(product, "writer", bidder);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(bidderId, createRequest))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("해당 상품의 비딩이 종료되었다면")
    class ContextExpiredProduct {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        long biddingPrice = 1000L;
        long productId = product.getId();
        long bidderId = bidder.getId();
        BiddingCreateRequest createRequest = new BiddingCreateRequest(productId, biddingPrice);

        ReflectionTestUtils.setField(product, "progressed", false);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(bidderId, createRequest))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("해당 상품의 최소 금액보다 적게 비딩 금액을 입력하면")
    class ContextAmountSmallerThanMinPrice {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        long biddingPrice = 1000L;
        long productId = product.getId();
        long bidderId = bidder.getId();
        BiddingCreateRequest createRequest = new BiddingCreateRequest(productId, biddingPrice);

        ReflectionTestUtils.setField(product, "progressed", false);
        ReflectionTestUtils.setField(product, "minimumPrice", (int)biddingPrice * 2);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(bidderId, createRequest))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("BiddingCreateDto의 bidder Id에 해당하는 사용자가 없으면")
    class ContextNotExistBidder {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        long biddingPrice = 1000L;
        long productId = product.getId();
        long bidderId = bidder.getId();
        BiddingCreateRequest createRequest = new BiddingCreateRequest(productId, biddingPrice);

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(bidderId, createRequest))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("정상적인 BiddingCreateDto가 들어오면")
    class ContextValidBiddingCreateDto {

      @Test
      @DisplayName("생성된 Bidding의 Id를 반환한다")
      void ItThrowsNotFoundException() {
        // given
        long biddingPrice = 1000L;
        long productId = product.getId();
        long bidderId = bidder.getId();
        BiddingCreateRequest createRequest = new BiddingCreateRequest(productId, biddingPrice);

        ReflectionTestUtils.setField(product, "progressed", true);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        Bidding bidding = new Bidding(biddingPrice, bidder, product);

        long expectBiddingId = 1L;
        ReflectionTestUtils.setField(bidding, "id", expectBiddingId);

        given(biddingRepository.save(any())).willReturn(bidding);

        // when
        BiddingCreateResponse actualBiddingId = biddingService.create(bidderId, createRequest);

        // then
        assertThat(actualBiddingId.getId()).isEqualTo(expectBiddingId);
      }

    }

  }

  @Nested
  @DisplayName("findBiddingPriceByProductIdAndUserId 메소드는")
  class DescribeFindBiddingPriceByProductIdAndUserIdMethod {

    @Nested
    @DisplayName("bidding를 찾을 수 없다면")
    class ContextNotFoundBidding {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItThrowsNotFoundException() {
        // given
        given(biddingRepository.findByBidderIdAndProductId(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> biddingService.findBiddingPriceByProductIdAndUserId(
            productId,
            bidderId
        )).isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("정상적인 요청이 들어오면")
    class ContextValidRequest {

      @Test
      @DisplayName("BiddingPrice를 반환한다")
      void ItReturn() {
        // given
        long biddingPrice = 10000L;

        Bidding bidding = Bidding
            .builder()
            .product(product)
            .bidder(bidder)
            .biddingPrice(biddingPrice)
            .build();
        long biddingId = 1L;
        ReflectionTestUtils.setField(bidding, "id", biddingId);

        given(biddingRepository.findByBidderIdAndProductId(anyLong(), anyLong()))
            .willReturn(Optional.of(bidding));

        // when
        BiddingPriceResponse actualBiddingPrice = biddingService.findBiddingPriceByProductIdAndUserId(
            productId,
            biddingId
        );

        // then
        assertThat(actualBiddingPrice.getBiddingPrice()).isEqualTo(biddingPrice);

      }
    }
  }
}
