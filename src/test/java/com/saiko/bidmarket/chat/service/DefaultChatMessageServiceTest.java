package com.saiko.bidmarket.chat.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.controller.dto.ChatSendMessage;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatMessageRepository;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultChatMessageServiceTest {

  @Mock
  ChatMessageRepository chatMessageRepository;

  @Mock
  ChatRoomRepository chatRoomRepository;

  @Mock
  UserRepository userRepository;

  @InjectMocks
  DefaultChatMessageService defaultChatMessageService;

  private User getTestUser(long userId) {
    User user = User
        .builder()
        .username("test")
        .profileImage("test")
        .provider("test")
        .providerId("test")
        .group(new Group())
        .build();

    ReflectionTestUtils.setField(user, "id", userId);
    return user;
  }

  private ChatRoom getTestChatRoom(
      long roomId,
      long user1,
      long user2,
      long productId
  ) {
    ChatRoom chatRoom = ChatRoom
        .builder()
        .winner(getTestUser(user1))
        .seller(getTestUser(user2))
        .product(getTestProduct(productId, user2))
        .build();

    ReflectionTestUtils.setField(chatRoom, "id", roomId);
    return chatRoom;
  }

  private Product getTestProduct(
      long productId,
      long userId
  ) {
    Product product = Product
        .builder()
        .title("test")
        .images(Collections.emptyList())
        .writer(getTestUser(userId))
        .description("test")
        .minimumPrice(1000)
        .category(Category.BEAUTY)
        .build();

    ReflectionTestUtils.setField(product, "id", productId);
    return product;
  }

  @Nested
  @DisplayName("create 메서드는")
  class DescribeCreate {

    @Nested
    @DisplayName("createParam 이 null 이면")
    class ContextWithNullParam {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> defaultChatMessageService.create(null))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidParam {

      @Test
      @DisplayName("생성된 메시지에 대한 정보를 담은 DTO를 반환한다")
      void ItReturnDTO() {
        //given
        long roomId = 1L;
        long senderUserId = 1L;
        long receiverUserId = 2L;
        long productId = 1L;
        String content = "Test content";

        ChatSendMessage chatSendMessage = new ChatSendMessage(senderUserId, content);
        ChatMessageCreateParam createParam = ChatMessageCreateParam.of(roomId, chatSendMessage);

        given(chatRoomRepository.findById(anyLong()))
            .willReturn(Optional.of(getTestChatRoom(
                roomId,
                senderUserId,
                receiverUserId,
                productId
            )));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(getTestUser(senderUserId)));

        given(chatMessageRepository.save(any(ChatMessage.class)))
            .willAnswer(methodInvocationMock -> methodInvocationMock.getArguments()[0]);

        //when
        ChatPublishMessage chatPublishMessage = defaultChatMessageService.create(createParam);

        //then
        assertThat(chatPublishMessage.getContent()).isEqualTo(content);
      }
    }

  }
}
