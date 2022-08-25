package com.saiko.bidmarket.chat.repository;

import static java.lang.Thread.*;
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

import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.entity.UserRole;
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
  private UserRepository userRepository;

  @BeforeEach
  void deleteAll() {
    chatMessageRepository.deleteAll();
    chatRoomRepository.deleteAll();
    productRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Nested
  @DisplayName("findLastChatMessageOfChatRoom 메서드는")
  class DescribeFindLastChatMessageOfChatRoom {

    @Nested
    @DisplayName("호출되면")
    class ContextWithCall {

      @Test
      @DisplayName("해당 채팅방의 마지막 채팅 메시지를 반환한다")
      void It() throws InterruptedException {
        User seller = userRepository.save(getUser("1"));
        User winner = userRepository.save(getUser("2"));

        Product product = getProduct(seller);
        productRepository.save(product);

        ChatRoom chatRoom = getChatRoom(seller, winner, product);
        long chatRoomId = chatRoomRepository
            .save(chatRoom)
            .getId();

        ChatMessage sellerMsg1 = getChatMessage(chatRoom, seller, "팜");
        ChatMessage winnerMsg1 = getChatMessage(chatRoom, winner, "삼");
        chatMessageRepository.save(sellerMsg1);
        sleep(1000);
        chatMessageRepository.save(winnerMsg1);

        @SuppressWarnings("all")
        ChatMessage lastMessage = chatMessageRepository
            .findLastChatMessageOfChatRoom(chatRoomId)
            .get();
        assertThat(lastMessage.getMessage()).isEqualTo("삼");
      }
    }
  }

  @Nested
  @DisplayName("findAllChatMessage 메서드는")
  class DescribeFindAllChatMessage {

    @Nested
    @DisplayName("호출되면")
    class ContextWithCall {

      @Test
      @DisplayName("해당 채팅방의 메시지를 페이지 범위만큼 반환한다")
      void ItResponseChatMessages() throws InterruptedException {
        User seller = userRepository.save(getUser("1"));
        User winner = userRepository.save(getUser("2"));

        Product product = productRepository.save(getProduct(seller));
        ChatRoom chatRoom = chatRoomRepository.save(getChatRoom(seller, winner, product));

        generateAndSaveTestMessage(chatRoom, seller, winner);

        ChatMessageSelectRequest request = new ChatMessageSelectRequest(0, 10);

        //when
        List<ChatMessage> chatMessages = chatMessageRepository.findAllChatMessage(
            chatRoom.getId(),
            request
        );

        //then
        assertThat(chatMessages.size()).isEqualTo(10);
      }
    }
  }

  private User getUser(String providerId) {
    return User
        .builder()
        .username("제로")
        .userRole(UserRole.ROLE_USER)
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

  private List<String> getSellerChatMessages() {
    return List.of("팔게요", "어디심", "너무 멀어요", "역삼 어때요", "잠실은?", "낼 가능하신가요?");
  }

  private List<String> getWinnerChatMessages() {
    return List.of("살게요", "강남이요", "그럼 어디로", "음..", "좋습니다", "넵 2시에 뵙죠");
  }


  private void generateAndSaveTestMessage(
      ChatRoom chatRoom,
      User seller,
      User winner
  ) throws InterruptedException {
    List<String> sellerChatMessages = getSellerChatMessages();
    List<String> winnerChatMessages = getWinnerChatMessages();

    for (int i = 0; i < sellerChatMessages.size(); i++) {
      saveMessageWithTimeTerm(chatRoom, seller, sellerChatMessages.get(i));
      saveMessageWithTimeTerm(chatRoom, winner, winnerChatMessages.get(i));
    }
  }

  private void saveMessageWithTimeTerm(
      ChatRoom chatRoom,
      User user,
      String msg
  ) throws InterruptedException {
    chatMessageRepository.save(getChatMessage(chatRoom, user, msg));
    sleep(1000);
  }
}
