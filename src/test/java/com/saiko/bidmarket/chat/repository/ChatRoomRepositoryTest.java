package com.saiko.bidmarket.chat.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.GroupRepository;
import com.saiko.bidmarket.user.repository.UserRepository;

@DataJpaTest()
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QueryDslConfig.class)
public class ChatRoomRepositoryTest {

  @Autowired
  private ChatRoomRepository chatRoomRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void deleteAll() {
    productRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Nested
  @DisplayName("findByProduct_IdAndSeller_Id 메소드는")
  class DescribeFindByProduct_IdAndSeller_Id {

    @Test
    @DisplayName("판매자와 낙찰자가 참여한 채팅방을 반환한다")
    void ItReturnChatRoom() {
      //given
      Group group = groupRepository
          .findById(1l)
          .get();
      User seller = userRepository.save(User
                                            .builder()
                                            .username("레이")
                                            .group(group)
                                            .profileImage("image")
                                            .provider("google")
                                            .providerId("123")
                                            .build());
      User bidder = userRepository.save(User
                                            .builder()
                                            .username("제로")
                                            .group(group)
                                            .profileImage("image")
                                            .provider("google")
                                            .providerId("1234")
                                            .build());
      Product product = productRepository.save(Product
                                                   .builder()
                                                   .title("코드 리뷰 해드려요")
                                                   .description("좋아요")
                                                   .category(Category.HOBBY)
                                                   .location("대면은 안해요")
                                                   .images(List.of("image"))
                                                   .minimumPrice(10000)
                                                   .writer(seller)
                                                   .build());
      ChatRoom chatRoom = chatRoomRepository.save(ChatRoom
                                                      .builder()
                                                      .seller(seller)
                                                      .product(product)
                                                      .winner(bidder)
                                                      .build());
      //when
      ChatRoom foundChatRoom = chatRoomRepository
          .findByProduct_IdAndSeller_Id(
              product.getId(),
              seller.getId()
          )
          .get();
      assertThat(foundChatRoom).isEqualTo(chatRoom);
    }
  }
}
