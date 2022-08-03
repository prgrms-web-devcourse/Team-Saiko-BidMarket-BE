package com.saiko.bidmarket.product.service;

import static com.saiko.bidmarket.product.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.controller.dto.ProductDetailResponse;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

  @Mock
  ProductRepository productRepository;

  @Mock
  UserRepository userRepository;

  @InjectMocks
  DefaultProductService productService;

  @Nested
  @DisplayName("create 메서드는")
  class DescribeCreate {

    @Nested
    @DisplayName("Request 값이 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> productService.create(null, 1L))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("user id 값이 양수가 아니면")
    class ContextWithNotPositiveUserId {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException(long userId) {
        //given
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            "텀블러 팝니다",
            "깨끗해요",
            Arrays.asList("image1",
                          "image2"),
            ETC,
            15000,
            "강남"
        );

        //when, then
        assertThatThrownBy(() -> productService.create(productCreateRequest, userId))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArg {

      @Test
      @DisplayName("저장한 객체에 대한 정보를 담은 응답을 반환한다")
      void ItResponseProduct() {
        //given
        long userId = 1L;
        long productId = 1L;

        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            "텀블러 팝니다",
            "깨끗해요",
            Arrays.asList("image1",
                          "image2"),
            ETC,
            15000,
            "강남"
        );
        User writer = new User("제로", "image", "google", "1234", new Group());
        ReflectionTestUtils.setField(writer, "id", 1L);
        Product product = Product.builder()
                                 .title(productCreateRequest.getTitle())
                                 .description(productCreateRequest.getDescription())
                                 .location(productCreateRequest.getLocation())
                                 .category(productCreateRequest.getCategory())
                                 .minimumPrice(productCreateRequest.getMinimumPrice())
                                 .images(productCreateRequest.getImages())
                                 .writer(writer)
                                 .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        given(userRepository.findById(userId)).willReturn(Optional.of(writer));
        given(productRepository.save(any())).willReturn(product);

        //when
        ProductCreateResponse response = productService.create(productCreateRequest, userId);

        //then
        verify(productRepository).save(any(Product.class));
        assertThat(response.getId()).isEqualTo(product.getId());
      }
    }
  }

  @Nested
  @DisplayName("findAll 메서드는")
  class DescribeFindAll {

    @Nested
    @DisplayName("Request 가 null 이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> productService.findAll(null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArgument {

      @Test
      @DisplayName("요청에 해당하는 상품 리스트를 반환한다")
      void ItResponseProductList() {
        //given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, 0, 2, null);
        User writer = new User("제로", "image", "google", "1234", new Group());
        Product product = Product.builder()
                                 .title("세탁기 팔아요")
                                 .description("좋아요")
                                 .minimumPrice(100000)
                                 .category(HOUSEHOLD_APPLIANCE)
                                 .location("수원")
                                 .writer(writer)
                                 .images(null)
                                 .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        given(productRepository.findAllProduct(any(ProductSelectRequest.class)))
            .willReturn(List.of(product));

        //when
        List<ProductSelectResponse> result = productService.findAll(productSelectRequest);

        //then
        verify(productRepository).findAllProduct(any(ProductSelectRequest.class));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(product.getId());
      }
    }
  }

  @Nested
  @DisplayName("findById 메서드는")
  class DescribeFindById {

    @Nested
    @DisplayName("id에 해당하는 상품이 없다면")
    class ContextNotFoundProductById {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        long inputId = Long.MAX_VALUE;

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> productService.findById(inputId))
            .isInstanceOf(NotFoundException.class);
        verify(productRepository, atLeastOnce()).findById(anyLong());
      }
    }

    @Nested
    @DisplayName("id 값이 양수가 아니면")
    class ContextWith {

      @ParameterizedTest
      @ValueSource(longs = {0, -1L, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException(long src) {
        //when, then
        assertThatThrownBy(() -> productService.findById(src))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("양수값이 전달되면")
    class ContextWithPositiveId {

      @Test
      @DisplayName("찾은 객체에 대한 응답을 반환한다")
      void ItProduct() {
        //given
        User writer = new User("제로", "image", "google", "1234", new Group());
        Product product = Product.builder()
                                 .title("세탁기 팔아요")
                                 .description("좋아요")
                                 .minimumPrice(100000)
                                 .category(HOUSEHOLD_APPLIANCE)
                                 .location("수원")
                                 .writer(writer)
                                 .images(null)
                                 .build();
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        //when
        ProductDetailResponse response = productService.findById(1L);

        //then
        assertThat(response.getId()).isEqualTo(product.getId());
      }
    }
  }
}
