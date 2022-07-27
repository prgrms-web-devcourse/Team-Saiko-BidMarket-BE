package com.saiko.bidmarket.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Constructor;
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
import com.saiko.bidmarket.product.Repository.ProductRepository;
import com.saiko.bidmarket.product.entity.Product;

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
}