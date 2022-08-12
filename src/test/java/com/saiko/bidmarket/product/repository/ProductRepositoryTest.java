package com.saiko.bidmarket.product.repository;

import static com.saiko.bidmarket.common.Sort.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.Sort;
import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.dto.UserProductSelectQueryParameter;
import com.saiko.bidmarket.user.controller.dto.UserProductSelectRequest;
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
  private BiddingRepository biddingRepository;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void deleteAll() {
    biddingRepository.deleteAll();
    productRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Nested
  @DisplayName("findAllProduct 메소드는")
  class DescribeFindAllProduct {

    @Nested
    @DisplayName("ProductSelectRequest 가 null 이라면")
    class ContextWithProductSelectRequestNull {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //when, then
        assertThatThrownBy(() -> productRepository.findAllProduct(null))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("상품 입찰 진행 여부가 true 라면")
    class ContextProgressedTrue {

      @Test
      @DisplayName("입찰 중인 상품 목록 리스트를 반환한다")
      void itReturnProgressProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             Sort.END_DATE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product progressProduct = productRepository.save(Product
                                                             .builder()
                                                             .title("노트북 팝니다1")
                                                             .description("싸요")
                                                             .category(Category.DIGITAL_DEVICE)
                                                             .minimumPrice(10000)
                                                             .images(List.of("image"))
                                                             .location(null)
                                                             .writer(writer)
                                                             .build());
        Product notInProgressProduct = productRepository.save(Product
                                                                  .builder()
                                                                  .title("노트북 팝니다2")
                                                                  .description("싸요")
                                                                  .category(
                                                                      Category.DIGITAL_DEVICE)
                                                                  .minimumPrice(10000)
                                                                  .images(List.of("image"))
                                                                  .location(null)
                                                                  .writer(writer)
                                                                  .build());
        ReflectionTestUtils.setField(notInProgressProduct, "progressed", false);

        // when
        List<Product> result = productRepository.findAllProduct(productSelectRequest);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(progressProduct);
      }
    }

    @Nested
    @DisplayName("상품 입찰 여부가 false 라면")
    class ContextProgressedFalse {

      @Test
      @DisplayName("입찰이 끝난 상품 목록 리스트를 반환한다")
      void itReturnNotInProgressProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "false", null, 0,
                                                                             2,
                                                                             Sort.END_DATE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product progressProduct = productRepository.save(Product
                                                             .builder()
                                                             .title("노트북 팝니다1")
                                                             .description("싸요")
                                                             .category(Category.DIGITAL_DEVICE)
                                                             .minimumPrice(10000)
                                                             .images(List.of("image"))
                                                             .location(null)
                                                             .writer(writer)
                                                             .build());
        Product notInProgressProduct = productRepository.save(Product
                                                                  .builder()
                                                                  .title("노트북 팝니다2")
                                                                  .description("싸요")
                                                                  .category(
                                                                      Category.DIGITAL_DEVICE)
                                                                  .minimumPrice(10000)
                                                                  .images(List.of("image"))
                                                                  .location(null)
                                                                  .writer(writer)
                                                                  .build());
        ReflectionTestUtils.setField(notInProgressProduct, "progressed", false);

        // when
        List<Product> result = productRepository.findAllProduct(productSelectRequest);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(notInProgressProduct);
      }
    }

    @Nested
    @DisplayName("정렬 조건이 최신순이라면")
    class ContextSortCreatedAtDesc {

      @Test
      @DisplayName("최신순으로 정렬된 상품 목록 리스트를 반환한다")
      void itReturnCreatedAtDescProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             CREATED_AT_DESC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        Product product2 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다2")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        ReflectionTestUtils.setField(product2, "createdAt",
                                     product1
                                         .getCreatedAt()
                                         .plusMinutes(10)
        );

        // when
        List<Product> result = productRepository.findAllProduct(productSelectRequest);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(product2);
        assertThat(result.get(1)).isEqualTo(product1);
      }
    }

    @Nested
    @DisplayName("정렬 조건이 시작가 오름차순 이라면")
    class ContextSortMinimumPriceAsc {

      @Test
      @DisplayName("시작가 오름차순으로 정렬된 상품 목록 리스트를 반환한다")
      void itReturnMinimumPriceAscProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             MINIMUM_PRICE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        Product product2 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다2")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000000)
                                                      .images(List.of("image"))
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
    @DisplayName("정렬 조건이 시작가 내림차순 이라면")
    class ContextSortMinimumPriceDesc {

      @Test
      @DisplayName("시작가 내림차순으로 정렬된 상품 목록 리스트를 반환한다")
      void itReturnMinimumPriceDescProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             MINIMUM_PRICE_DESC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        Product product2 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다2")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());

        // when
        List<Product> result = productRepository.findAllProduct(productSelectRequest);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(product2);
        assertThat(result.get(1)).isEqualTo(product1);
      }
    }

    @Nested
    @DisplayName("정렬 조건이 null 이라면")
    class ContextSortNull {

      @Test
      @DisplayName("종료 임박순으로 정렬된 상품 목록 리스트를 반환한다")
      void itReturnEndDateAscProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             null
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        Product product2 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다2")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
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
    @DisplayName("카테고리 정보가 안넘어온다면")
    class ContextWithNoCategory {

      @Test
      @DisplayName("페이징 처리된 전체 카테고리 상품 목록을 반환한다")
      void itReturnProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             Sort.END_DATE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        Product product2 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다2")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
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
    @DisplayName("카테고리가 ALL 이라면")
    class ContextWithCategoryAll {

      @Test
      @DisplayName("페이징 처리된 전체 카테고리 상품 목록을 반환한다")
      void itReturnProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             Sort.END_DATE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        Product product2 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다2")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
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
            null, "true", Category.DIGITAL_DEVICE, 0, 2,
            Sort.END_DATE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        productRepository.save(Product
                                   .builder()
                                   .title("화분")
                                   .description("예뻐요")
                                   .category(Category.PLANT)
                                   .minimumPrice(10000)
                                   .images(List.of("image"))
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

    @Nested
    @DisplayName("검색 제목 정보가 안넘어온다면")
    class ContextWithNoTitle {

      @Test
      @DisplayName("페이징 처리된 전체 상품 목록을 반환한다")
      void itReturnProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(null, "true", null, 0,
                                                                             2,
                                                                             Sort.END_DATE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다1")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        Product product2 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북 팝니다2")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
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
    @DisplayName("검색 제목 정보가 넘어온다면")
    class ContextWithTitleFilter {

      @Test
      @DisplayName("페이징 처리된 특정 제목의 상품 목록을 반환한다")
      void itReturnParticularTitleProductList() {
        // given
        ProductSelectRequest productSelectRequest = new ProductSelectRequest(
            "노트북", "true", Category.DIGITAL_DEVICE, 0, 2,
            Sort.END_DATE_ASC
        );
        Group group = groupRepository
            .findById(1L)
            .get();
        User writer = new User("제로", "image", "google", "123", group);
        writer = userRepository.save(writer);

        Product product1 = productRepository.save(Product
                                                      .builder()
                                                      .title("노트북! 팝니다")
                                                      .description("싸요")
                                                      .category(Category.DIGITAL_DEVICE)
                                                      .minimumPrice(10000)
                                                      .images(List.of("image"))
                                                      .location(null)
                                                      .writer(writer)
                                                      .build());
        productRepository.save(Product
                                   .builder()
                                   .title("화분")
                                   .description("예뻐요")
                                   .category(Category.PLANT)
                                   .minimumPrice(10000)
                                   .images(List.of("image"))
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

        Group group = groupRepository
            .findById(1L)
            .get();

        User writer = new User("박동철", "image", "google", "1234", group);
        writer = userRepository.save(writer);

        Product product = productRepository.save(
            Product
                .builder()
                .title("노트북 팝니다")
                .description("싸요")
                .category(Category.DIGITAL_DEVICE)
                .minimumPrice(10000)
                .images(List.of("image"))
                .location(null)
                .writer(writer)
                .build()
        );

        // when
        UserProductSelectRequest request = new UserProductSelectRequest(0, 1, END_DATE_ASC);
        UserProductSelectQueryParameter parameter = UserProductSelectQueryParameter.of(
            writer.getId(), request);
        List<Product> result = productRepository.findAllUserProduct(parameter);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(product);
      }
    }

    @Nested
    @DisplayName("UserProductSelectQueryParameter 가 null 이면")
    class ContextWithNullPageRequest {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //when, then
        assertThatThrownBy(() -> productRepository.findAllUserProduct(null))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }
  }

  @Nested
  @DisplayName("findByIdJoinWithUser 메서드는")
  class DescribeFindByIdJoinWithUser {
    @Nested
    @DisplayName("정상적인 값이 들어오면")
    class ContextValidData {

      @Test
      @DisplayName("아이디에 해당하는 상품을 유저와 조인하여 반환한다")
      void itReturnProduct() {
        // given

        Group group = groupRepository
            .findById(1L)
            .get();

        User writer = new User("박동철", "image", "google", "1234", group);
        writer = userRepository.save(writer);

        Product product = productRepository.save(
            Product
                .builder()
                .title("노트북 팝니다")
                .description("싸요")
                .category(Category.DIGITAL_DEVICE)
                .minimumPrice(10000)
                .images(List.of("image"))
                .location(null)
                .writer(writer)
                .build()
        );

        // when
        Product foundProduct = productRepository
            .findByIdJoinWithUser(
                product.getId())
            .get();

        // then
        assertThat(foundProduct).isEqualTo(product);
        assertThat(foundProduct
                       .getWriter()
                       .getClass()).isEqualTo(User.class);
      }
    }
  }

  @Nested
  @DisplayName("finishByUserId 메서드는")
  class DescribeFinishByUserId {

    @Nested
    @DisplayName("호출되면")
    class ContextCall {

      @Test
      @DisplayName("해당 userId 상품들의 progressed를 false로 변경한다.")
      void itUpdateProgressedToFalse() {
        //given
        Group group = groupRepository
            .findById(1L)
            .get();

        final User user = userRepository.save(
            User
                .builder()
                .username("test")
                .provider("test")
                .providerId("test")
                .group(group)
                .profileImage("test")
                .build()
        );

        final Product firstProduct = productRepository.save(
            Product
                .builder()
                .title("test")
                .category(Category.BEAUTY)
                .description("test")
                .minimumPrice(2000)
                .location("test")
                .writer(user)
                .images(List.of("ss"))
                .build()
        );

        //when
        productRepository.finishByUserId(user.getId());
        Product product = productRepository.findById(firstProduct.getId()).get();

        //then
        Assertions.assertThat(product.isProgressed()).isEqualTo(false);
      }
    }
  }
}
