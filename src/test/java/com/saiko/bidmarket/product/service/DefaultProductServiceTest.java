package com.saiko.bidmarket.product.service;

import static com.saiko.bidmarket.product.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

  @InjectMocks
  private DefaultProductService productService;

  @Mock
  private ProductRepository productRepository;

  @Nested
  @DisplayName("findById 메소드는")
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
    @DisplayName("id에 음수가 들어온다면")
    class ContextNegativeNumberId {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다.")
      void itThrowsIllegalArgumentsException() {
        // given
        long negativeNumberId = -1;

        // when
        // then
        assertThatThrownBy(() -> productService.findById(negativeNumberId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(productRepository, never()).findById(anyLong());
      }
    }

    @Nested
    @DisplayName("정상적인 id가 들어온다면")
    class ContextValidId {

      @Test
      @DisplayName("해당 id를 가진 상품의 도메인 객체를 반환한다.")
      void itReturnProductDomainObjectHasInputId() throws Exception {
        // given
        long validId = 1;
        Class<Product> productClass = Product.class;
        Constructor<Product> productConstructor = productClass.getDeclaredConstructor();
        productConstructor.setAccessible(true);
        Product expectProduct = productConstructor.newInstance();

        ReflectionTestUtils.setField(expectProduct, "id", validId);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(expectProduct));

        // when
        Product actualProduct = productService.findById(validId);

        // then
        assertThat(actualProduct).isEqualTo(expectProduct);
        verify(productRepository, atLeastOnce()).findById(anyLong());
      }
    }
  }

  @Nested
  @DisplayName("create 메서드는")
  class DescribeCreate {

    @Test
    @DisplayName("상품을 저장하고 저장된 상품 객체를 반환한다")
    void ItSaveProductThenReturnProductId() {
      //given
      Long userId = 1L;
      User writer = new User("제로", "image", "google", "1234", new Group());
      ReflectionTestUtils.setField(writer, "id", userId);

      Product product = Product.builder()
                               .title("텀블러 팝니다")
                               .description("깨끗해요")
                               .minimumPrice(15000)
                               .category(ETC)
                               .location("강남")
                               .images(Arrays.asList("image1", "image2"))
                               .writer(writer)
                               .build();

      ReflectionTestUtils.setField(product, "id", 1L);
      given(productRepository.save(any(Product.class))).willReturn(product);

      //when
      Product savedProduct = productService.create(product);

      //then
      verify(productRepository).save(any(Product.class));
      assertThat(savedProduct.getId()).isEqualTo(product.getId());
    }

    @Nested
    @DisplayName("null 값이 전달되면")
    class ContextWithNullProduct {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(null));
      }
    }

  }

  @Nested
  @DisplayName("findAll 메서드는")
  class DescribeFindAll {

    @Test
    @DisplayName("전체 상품을 반환한다")
    void ItReturnProductList() {
      //given
      ProductSelectRequest productSelectRequest = new ProductSelectRequest(0, 2, null);
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

      given(productRepository.findAllProduct(any(PageRequest.class)))
          .willReturn(List.of(product));

      //when
      List<Product> result = productService.findAll(productSelectRequest);

      //then
      verify(productRepository).findAllProduct(any(PageRequest.class));
      assertThat(result.size()).isEqualTo(1);
      assertThat(result.get(0)).isEqualTo(product);
    }

    @Nested
    @DisplayName("productSelectRequest 파라미터에 null 값이 전달되면")
    class ContextWithProductSelectRequestNull {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException() {
        //given
        //when,then
        assertThrows(IllegalArgumentException.class,
                     () -> productService.findAll(null));
      }
    }

    @Nested
    @DisplayName("조회된 상품이 없다면")
    class ContextWithProductListZero {

      @Test
      @DisplayName("빈 리스트를 반환한다")
      void ItReturnEmptyList() {
        //given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(0, 2, null);

        given(productRepository.findAllProduct(any(PageRequest.class)))
            .willReturn(Collections.emptyList());

        //when
        List<Product> result = productService.findAll(productSelectRequest);

        //then
        assertThat(result.size()).isEqualTo(0);
      }
    }
  }
}
