package com.saiko.bidmarket.chat.repository;

import static java.lang.Thread.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.chat.entity.ChatMessage;
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
public class ChatMessageRepositoryTest {

  @Autowired
  private ChatRoomRepository chatRoomRepository;

  @Autowired
  private ChatMessageRepository chatMessageRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void deleteAll() {
    chatMessageRepository.deleteAll();
    chatRoomRepository.deleteAll();
    productRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Nested
  @DisplayName("")
  class Describe {

    @Nested
    @DisplayName("")
    class ContextWith {

      @Test
      @DisplayName("해당 채팅방의 마지막 채팅 메시지를 반환한다")
      void It() throws InterruptedException {
        User seller = userRepository.save(getUser("1", getUserGroup()));
        User winner = userRepository.save(getUser("2", getUserGroup()));

        Product product = getProduct(seller);
        productRepository.save(product);

        ChatRoom chatRoom = getChatRoom(seller, winner, product);
        long chatRoomId = chatRoomRepository.save(chatRoom).getId();

        ChatMessage sellerMsg1 = getChatMessage(chatRoom, seller, "팜");
        ChatMessage winnerMsg1 = getChatMessage(chatRoom, winner, "삼");
        chatMessageRepository.save(sellerMsg1);

        ReflectionTestUtils.setField(sellerMsg1, "createdAt", LocalDateTime.of(2022, 2,2,2,2));
        ReflectionTestUtils.setField(winnerMsg1, "createdAt", LocalDateTime.of(2022, 2,2,2,30));
        chatMessageRepository.save(winnerMsg1);

        @SuppressWarnings("all")
        ChatMessage lastMessage = chatMessageRepository
            .findLastChatMessageOfChatRoom(chatRoomId)
            .get();
        assertThat(lastMessage.getMessage()).isEqualTo("삼");
      }
    }
  }

  @SuppressWarnings("all")
  private Group getUserGroup() {
    return groupRepository
        .findById(1L)
        .get();
  }

  private User getUser(
      String providerId,
      Group group
  ) {
    return User
        .builder()
        .username("제로")
        .group(group)
        .profileImage("image")
        .provider("google")
        .providerId(providerId)
        .build();
  }

  private Product getProduct(User seller) {
    return Product
        .builder()
        .title("코드 리뷰 해드려요")
        .description("좋아요")
        .category(Category.HOBBY)
        .location("대면은 안해요")
        .images(List.of("image"))
        .minimumPrice(10000)
        .writer(seller)
        .build();
  }

  private ChatRoom getChatRoom(
      User seller,
      User winner,
      Product product
  ) {
    return ChatRoom
        .builder()
        .seller(seller)
        .product(product)
        .winner(winner)
        .build();
  }

  private ChatMessage getChatMessage(
      ChatRoom chatRoom,
      User sender,
      String content
  ) {
    return ChatMessage
        .builder()
        .chatRoom(chatRoom)
        .sender(sender)
        .message(content)
        .build();
  }
}
