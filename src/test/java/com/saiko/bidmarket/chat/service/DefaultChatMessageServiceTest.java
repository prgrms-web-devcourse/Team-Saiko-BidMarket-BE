package com.saiko.bidmarket.chat.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectResponse;
import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.controller.dto.ChatSendMessage;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatMessageRepository;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;
import com.saiko.bidmarket.common.exception.NotFoundException;
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
        User seller = getUser(1L);
        User winner = getUser(2L);
        Product product = getProduct(1L, seller.getId());
        ChatRoom chatRoom = getChatRoom(1L, seller, winner, product);

        String content = "Test content";

        ChatSendMessage chatSendMessage = new ChatSendMessage(seller.getId(), content);
        ChatMessageCreateParam createParam =
            ChatMessageCreateParam.of(chatRoom.getId(), chatSendMessage);

        given(chatRoomRepository.findById(anyLong()))
            .willReturn(Optional.of(chatRoom));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(seller));

        given(chatMessageRepository.save(any(ChatMessage.class)))
            .willAnswer(methodInvocationMock -> methodInvocationMock.getArguments()[0]);

        //when
        ChatPublishMessage chatPublishMessage = defaultChatMessageService.create(createParam);

        //then
        assertThat(chatPublishMessage).isNotNull();
      }
    }

  }

  @Nested
  @DisplayName("findAll 메서드는")
  class DescribeFindAll {

    @Nested
    @DisplayName("request 값이 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 던진다")
      void ItThrowsIllegalArgumentException() {
        //when, that
        assertThatThrownBy(() -> defaultChatMessageService.findAll(1, 1, null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("해당 채팅방이 존재하지 않으면")
    class ContextWithChatRoomNotExists {

      @Test
      @DisplayName("NotFoundException 에러를 던진다")
      void ItThrowNotFoundException() {
        //given
        ChatMessageSelectRequest request = new ChatMessageSelectRequest(0, 10);
        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> defaultChatMessageService.findAll(1L, 1L, request))
            .isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("user 가 해당 채팅방에 속해있지 않으면")
    class ContextWithUserIsNotParticipant {

      @Test
      @DisplayName("IllegalArgumentException 에러를 던진다")
      void ItThrowsIllegalArgumentException() {
        //given
        User seller = getUser(1);
        User winner = getUser(2);
        Product product = getProduct(1, 1);
        ChatRoom chatRoom = getChatRoom(1, seller, winner, product);
        ChatMessageSelectRequest request = new ChatMessageSelectRequest(0, 10);

        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.of(chatRoom));

        //when, then
        assertThatThrownBy(() -> defaultChatMessageService.findAll(3L, 1L, request))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValid {

      @Test
      @DisplayName("해당 채팅방의 채팅 메시지 리스트를 반환한다")
      void ItReturnChatMessages() {
        //given
        int messageNum = 10;
        User seller = getUser(1);
        User winner = getUser(2);
        Product product = getProduct(1, 1);
        ChatRoom chatRoom = getChatRoom(1, seller, winner, product);
        ChatMessageSelectRequest request = new ChatMessageSelectRequest(0, 10);

        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.of(chatRoom));
        given(chatMessageRepository.findAllChatMessage(
            anyLong(),
            any(ChatMessageSelectRequest.class)
        )).willReturn(getChatMessages(chatRoom, seller, messageNum));

        //when
        List<ChatMessageSelectResponse> responses =
            defaultChatMessageService.findAll(seller.getId(), 1L, request);

        //then
        assertThat(responses.size()).isEqualTo(messageNum);
      }
    }

  }

  private List<ChatMessage> getChatMessages(
      ChatRoom chatRoom,
      User sender,
      int count
  ) {
    List<ChatMessage> chatMessages = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      chatMessages.add(getChatMessage(chatRoom, sender));
    }
    return chatMessages;
  }

  private ChatMessage getChatMessage(
      ChatRoom chatRoom,
      User sender
  ) {
    return ChatMessage
        .builder()
        .chatRoom(chatRoom)
        .sender(sender)
        .message("test")
        .build();
  }

  private ChatRoom getChatRoom(
      long chatRoomId,
      User seller,
      User winner,
      Product product
  ) {
    ChatRoom chatRoom = ChatRoom
        .builder()
        .seller(seller)
        .winner(winner)
        .product(product)
        .build();
    ReflectionTestUtils.setField(chatRoom, "id", chatRoomId);
    return chatRoom;
  }

  private User getUser(long userId) {
    User user = User
        .builder()
        .username("test")
        .provider("test")
        .providerId("test")
        .profileImage("test")
        .group(new Group())
        .build();

    ReflectionTestUtils.setField(user, "id", userId);
    return user;
  }

  private Product getProduct(
      long productId,
      long userId
  ) {
    Product product = Product
        .builder()
        .title("test")
        .images(Collections.emptyList())
        .writer(getUser(userId))
        .description("test")
        .minimumPrice(1000)
        .category(Category.BEAUTY)
        .build();

    ReflectionTestUtils.setField(product, "id", productId);
    return product;
  }
}
