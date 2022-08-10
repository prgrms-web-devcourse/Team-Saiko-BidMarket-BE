package com.saiko.bidmarket.user.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.entity.BiddingPrice;
import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

@DataJpaTest()
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QueryDslConfig.class)
class UserRepositoryTest {

  @Autowired
  EntityManagerFactory emf;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  BiddingRepository biddingRepository;

  @Autowired
  GroupRepository groupRepository;

  private User getUser(String name, Group group, String providerId) {
    return User.builder()
               .username(name)
               .provider("test")
               .providerId(providerId)
               .profileImage("test")
               .group(group)
               .build();
  }

  private Product getProduct(String title, User writer) {
    return Product.builder()
                  .title(title)
                  .description("test")
                  .images(List.of("image"))
                  .writer(writer)
                  .category(Category.BEAUTY)
                  .build();
  }

  private Bidding getBidding(User bidder, Product product) {
    return Bidding.builder()
                  .biddingPrice(BiddingPrice.valueOf(10000))
                  .bidder(bidder)
                  .product(product)
                  .build();
  }

  @Test
  @DisplayName("findWinnerOfBiddingByProductId 메서드 쿼리 테스트")
  public void findWinnerOfBiddingByProductId() {

    String bidderName = "testWinner";

    Group userGroup = groupRepository.findByName("USER_GROUP").get();

    User user1 = getUser("test1", userGroup, "test1");
    User user2 = getUser(bidderName, userGroup, "test2");
    User savedUser1 = userRepository.save(user1);
    User savedUser2 = userRepository.save(user2);

    Product product = getProduct("test", savedUser1);
    Product savedProduct = productRepository.save(product);

    Bidding bidding = getBidding(savedUser2, savedProduct);
    Bidding savedBidding = biddingRepository.save(bidding);

    savedBidding.win();

    User user = userRepository.findWinnerOfBiddingByProductId(product.getId()).get();

    assertThat(user.getUsername()).isEqualTo(bidderName);
  }
}
