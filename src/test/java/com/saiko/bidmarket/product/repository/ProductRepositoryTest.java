package com.saiko.bidmarket.product.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.GroupRepository;
import com.saiko.bidmarket.user.repository.UserRepository;

@DataJpaTest()
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QueryDslConfig.class)
public class ProductRepositoryTest {
  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @Nested
  @DisplayName("findAllProduct 메소드는")
  class DescribeFindAllProduct {

    @Nested
    @DisplayName("정상적인 값이 들어오면")
    class ContextValidData {

      @Test
      @DisplayName("페이징 처리된 상품 목록을 반환한다")
      void itReturnProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(0, 2,
                                                                             com.saiko.bidmarket.product.Sort.END_DATE_ASC);
        PageRequest pageRequest = PageRequest.of(productSelectRequest.getOffset(),
                                                 productSelectRequest.getLimit(),
                                                 Sort.Direction.valueOf(
                                                     productSelectRequest.getSort()
                                                                         .getOrder()
                                                                         .toString()),
                                                 productSelectRequest.getSort().getProperty());

        Group group = groupRepository.findById(1L).get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product.builder()
                                                         .title("노트북 팝니다1")
                                                         .description("싸요")
                                                         .category(Category.DIGITAL_DEVICE)
                                                         .minimumPrice(10000)
                                                         .images(null)
                                                         .location(null)
                                                         .writer(writer)
                                                         .build());
        Product product2 = productRepository.save(Product.builder()
                                                         .title("노트북 팝니다2")
                                                         .description("싸요")
                                                         .category(Category.DIGITAL_DEVICE)
                                                         .minimumPrice(10000)
                                                         .images(null)
                                                         .location(null)
                                                         .writer(writer)
                                                         .build());

        // when
        List<Product> result = productRepository.findAllProduct(pageRequest);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(product1);
        assertThat(result.get(1)).isEqualTo(product2);
      }
    }
  }

  @Nested
  @DisplayName("findAllUserProduct 메소드는")
  class DescribeFindAllUserProduct {

    @Nested
    @DisplayName("정상적인 값이 들어오면")
    class ContextValidData {

      @Test
      @DisplayName("해당 유저가 판매한, 페이징 처리된 상품 목록을 반환한다")
      void itReturnProductList() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.Direction.ASC, "expireAt");

        Group group = groupRepository.findById(1L).get();

        User writer1 = new User("제로", "image", "google", "123", group);
        User writer2 = new User("재이", "image", "google", "1234", group);

        writer1 = userRepository.save(writer1);
        writer2 = userRepository.save(writer2);

        Product product1 = productRepository.save(
            Product.builder()
                   .title("노트북 팝니다1")
                   .description("싸요")
                   .category(Category.DIGITAL_DEVICE)
                   .minimumPrice(10000)
                   .images(null)
                   .location(null)
                   .writer(writer1)
                   .build()
        );

        Product product2 = productRepository.save(
            Product.builder()
                   .title("노트북 팝니다2")
                   .description("싸요")
                   .category(Category.DIGITAL_DEVICE)
                   .minimumPrice(10000)
                   .images(null)
                   .location(null)
                   .writer(writer2)
                   .build()
        );

        // when
        List<Product> result = productRepository.findAllUserProduct(writer1.getId(), pageRequest);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(product1);
      }
    }

    @Nested
    @DisplayName("userId 가 양수가 아닌 값이 전달되면")
    class ContextWithNotPositiveUserId {

      @ParameterizedTest
      @ValueSource(longs = {0, -1L, Long.MIN_VALUE})
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException(long src) {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.Direction.ASC, "expireAt");
        //when, then
        assertThatThrownBy(() -> productRepository.findAllUserProduct(src, pageRequest))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("PageRequest 가 null 이면")
    class ContextWithNullPageRequest {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //when, then
        assertThatThrownBy(() -> productRepository.findAllUserProduct(1L, null))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }
  }
}
