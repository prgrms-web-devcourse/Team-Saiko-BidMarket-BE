package com.saiko.bidmarket.bidding.repository;

import static com.saiko.bidmarket.product.Sort.*;
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
import org.springframework.test.context.ActiveProfiles;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.bidding.respository.BiddingRepository;
import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.controller.dto.UserBiddingSelectRequest;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.GroupRepository;
import com.saiko.bidmarket.user.repository.UserRepository;

@DataJpaTest()
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QueryDslConfig.class)
public class BiddingRepositoryTest {
  @Autowired
  private BiddingRepository biddingRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Nested
  @DisplayName("findAllUserBidding 메소드는")
  class DescribeFindAllUserBidding {

    @Nested
    @DisplayName("UserBiddingSelectRequest 가 null 이라면")
    class ContextWithUserBiddingSelectRequestNull {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //when, then
        assertThatThrownBy(() -> biddingRepository.findAllUserBidding(1, null))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("userId 가 양수가 아니면")
    class ContextWithUserIdNotPositive {

      @ParameterizedTest
      @ValueSource(longs = {0, -1L, Long.MIN_VALUE})
      @DisplayName("InvalidDataAccessApiUsageException 예외를 던진다")
      void ItThrowsInvalidDataAccessApiUsageException(long src) {
        //given
        UserBiddingSelectRequest request = new UserBiddingSelectRequest(0, 1, END_DATE_ASC);

        //when, then
        assertThatThrownBy(() -> biddingRepository.findAllUserBidding(src, request))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("올바른 정보가 넘어온다면")
    class ContextWithValidData {

      @Test
      @DisplayName("페이징 처리된 입찰 상품 목록을 반환한다")
      void itReturnBiddingProductList() {
        // given
        Group group = groupRepository.findById(1L).get();

        User writer = userRepository.save(User.builder()
                                              .username("제로")
                                              .profileImage("image")
                                              .provider("google")
                                              .providerId("123")
                                              .group(group)
                                              .build());
        User bidder = userRepository.save(User.builder()
                                              .username("레이")
                                              .profileImage("image")
                                              .provider("google")
                                              .providerId("321")
                                              .group(group)
                                              .build());

        Product product = productRepository.save(Product.builder()
                                                        .title("노트북 팝니다1")
                                                        .description("싸요")
                                                        .category(Category.DIGITAL_DEVICE)
                                                        .minimumPrice(10000)
                                                        .images(null)
                                                        .location(null)
                                                        .writer(writer)
                                                        .build());

        Bidding bidding = biddingRepository.save(Bidding.builder()
                                                        .bidder(bidder)
                                                        .product(product)
                                                        .biddingPrice(BiddingPrice.valueOf(10000))
                                                        .build());

        UserBiddingSelectRequest userBiddingSelectRequest = new UserBiddingSelectRequest(0, 1,
                                                                                         END_DATE_ASC);
        // when
        List<Bidding> result = biddingRepository.findAllUserBidding(bidder.getId(),
                                                                    userBiddingSelectRequest);
        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(bidding);
      }
    }
  }
}
