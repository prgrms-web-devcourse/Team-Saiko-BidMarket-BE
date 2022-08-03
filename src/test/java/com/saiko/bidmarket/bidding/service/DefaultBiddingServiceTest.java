package com.saiko.bidmarket.bidding.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.bidding.respository.BiddingRepository;
import com.saiko.bidmarket.bidding.service.dto.BiddingCreateDto;
import com.saiko.bidmarket.common.entity.UnsignedLong;
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

  private static final User bidder = new User("test",
                                              "imageURl",
                                              "provider",
                                              "providerId",
                                              new Group());

  private static final User writer = new User("test",
                                              "imageURl",
                                              "provider",
                                              "providerId",
                                              new Group());

  private static final Product product = Product.builder()
                                                .title("title")
                                                .description("description")
                                                .writer(writer)
                                                .build();

  @Nested
  @DisplayName("create 메소드는")
  class DescribeCreateMethod {

    @Nested
    @DisplayName("BiddingCreateDto가 null이면")
    class ContextNullOfBiddingCreateDto {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        BiddingCreateDto createDto = null;

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(createDto))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("BiddingCreateDto의 productId에 해당하는 상품이 없으면")
    class ContextNotExistProduct {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        BiddingPrice biddingPrice = BiddingPrice.valueOf(1000L);
        UnsignedLong productId = UnsignedLong.valueOf(1);
        UnsignedLong bidderId = UnsignedLong.valueOf(1);
        BiddingCreateDto createDto = BiddingCreateDto.builder()
                                                     .biddingPrice(biddingPrice)
                                                     .bidderId(bidderId)
                                                     .productId(productId)
                                                     .build();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(createDto))
            .isInstanceOf(NotFoundException.class);
      }
    }
    
    @Nested
    @DisplayName("해당 상품의 비딩이 종료되었다면")
    class ContextExpiredProduct {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        BiddingPrice biddingPrice = BiddingPrice.valueOf(1000L);
        UnsignedLong productId = UnsignedLong.valueOf(1);
        UnsignedLong bidderId = UnsignedLong.valueOf(1);
        BiddingCreateDto createDto = BiddingCreateDto.builder()
                                                     .biddingPrice(biddingPrice)
                                                     .bidderId(bidderId)
                                                     .productId(productId)
                                                     .build();

        ReflectionTestUtils.setField(product, "progressed", false);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(createDto))
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
        long priceValue = 1000L;
        BiddingPrice biddingPrice = BiddingPrice.valueOf(priceValue);
        UnsignedLong productId = UnsignedLong.valueOf(1);
        UnsignedLong bidderId = UnsignedLong.valueOf(1);
        BiddingCreateDto createDto = BiddingCreateDto.builder()
                                                     .biddingPrice(biddingPrice)
                                                     .bidderId(bidderId)
                                                     .productId(productId)
                                                     .build();

        ReflectionTestUtils.setField(product, "progressed", false);
        ReflectionTestUtils.setField(product, "minimumPrice", (int) priceValue * 2);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(createDto))
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
        BiddingPrice biddingPrice = BiddingPrice.valueOf(1000L);
        UnsignedLong productId = UnsignedLong.valueOf(1);
        UnsignedLong bidderId = UnsignedLong.valueOf(1);
        BiddingCreateDto createDto = BiddingCreateDto.builder()
                                                     .biddingPrice(biddingPrice)
                                                     .bidderId(bidderId)
                                                     .productId(productId)
                                                     .build();

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> biddingService.create(createDto))
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
        BiddingPrice biddingPrice = BiddingPrice.valueOf(1000L);
        UnsignedLong productId = UnsignedLong.valueOf(1);
        UnsignedLong bidderId = UnsignedLong.valueOf(1);
        BiddingCreateDto createDto = BiddingCreateDto.builder()
                                                     .biddingPrice(biddingPrice)
                                                     .bidderId(bidderId)
                                                     .productId(productId)
                                                     .build();

        ReflectionTestUtils.setField(product, "progressed", true);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(bidder));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        Bidding bidding = new Bidding(biddingPrice, bidder, product);
        long expectBiddingId = 1L;
        ReflectionTestUtils.setField(bidding, "id", expectBiddingId);

        given(biddingRepository.save(any())).willReturn(bidding);

        // when
        UnsignedLong actualBiddingId = biddingService.create(createDto);

        // then
        assertThat(actualBiddingId).isNotNull();
        assertThat(actualBiddingId.getValue()).isEqualTo(expectBiddingId);
      }

    }

  }

}
