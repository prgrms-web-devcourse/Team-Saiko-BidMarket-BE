package com.saiko.bidmarket.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.entity.Product;
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
    @DisplayName("상품을 저장하고 저장된 상품의 아이디를 반환한다")
    void ItSaveProductThenReturnProductId() {
      //given
      ProductCreateRequest productCreateRequest = new ProductCreateRequest("텀블러 팝니다",
                                                                           "깨끗해요",
                                                                           Arrays.asList("image1",
                                                                                         "image2"),
                                                                           Category.ETC,
                                                                           15000,
                                                                           "강남");
      User writer = new User("제로", "image", "google", "1234", new Group());

      Product product = Product.builder()
                               .title(productCreateRequest.getTitle())
                               .description(productCreateRequest.getDescription())
                               .minimumPrice(productCreateRequest.getMinimumPrice())
                               .category(productCreateRequest.getCategory())
                               .location(productCreateRequest.getLocation())
                               .writer(writer)
                               .images(productCreateRequest.getImages())
                               .build();
      ReflectionTestUtils.setField(product, "id", 1L);

      given(productRepository.save(any(Product.class)))
          .willReturn(product);

      //when
      long productId = productService.create(productCreateRequest, writer);

      //then
      verify(productRepository).save(any(Product.class));
      assertThat(productId).isEqualTo(product.getId());
    }

    @Nested
    @DisplayName("productCreateRequest 파라미터에 null 값이 전달되면")
    class ContextWithProductCreateRequestNull {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException() {
        //given
        User writer = new User("제로", "image", "google", "123", new Group());

        //when,then
        assertThrows(IllegalArgumentException.class,
                     () -> productService.create(null, writer));
      }
    }

    @Nested
    @DisplayName("writer 파라미터에 null 값이 전달되면")
    class ContextWithWriterNull {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException() {
        //given
        ProductCreateRequest productCreateRequest = new ProductCreateRequest("텀블러 팝니다",
                                                                             "깨끗해요",
                                                                             Arrays.asList("image1",
                                                                                           "image2"),
                                                                             Category.ETC,
                                                                             15000,
                                                                             "강남");
        //when,then
        assertThrows(IllegalArgumentException.class,
                     () -> productService.create(productCreateRequest, null));
      }
    }
  }
}
