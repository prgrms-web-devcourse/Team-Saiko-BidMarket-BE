package com.saiko.bidmarket.product.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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
    @DisplayName("카테고리 정보가 안넘어온다면")
    class ContextValidData {

      @Test
      @DisplayName("페이징 처리된 전체 카테고리 상품 목록을 반환한다")
      void itReturnProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, 0, 2,
                                                                             com.saiko.bidmarket.product.Sort.END_DATE_ASC);
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
        List<Product> result = productRepository.findAllProduct(productSelectRequest);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(product1);
        assertThat(result.get(1)).isEqualTo(product2);
      }
    }

    @Nested
    @DisplayName("카테고리가 넘어온다면")
    class ContextWithCategoryFilter {

      @Test
      @DisplayName("페이징 처리된 특정 카테고리 상품 목록을 반환한다")
      void itReturnCategoryProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(
            Category.DIGITAL_DEVICE, 0, 2,
            com.saiko.bidmarket.product.Sort.END_DATE_ASC);
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
        productRepository.save(Product.builder()
                                      .title("화분")
                                      .description("예뻐요")
                                      .category(Category.PLANT)
                                      .minimumPrice(10000)
                                      .images(null)
                                      .location(null)
                                      .writer(writer)
                                      .build());

        // when
        List<Product> result = productRepository.findAllProduct(productSelectRequest);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(product1);
      }
    }
  }
}
