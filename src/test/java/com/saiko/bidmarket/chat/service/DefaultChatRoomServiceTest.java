package com.saiko.bidmarket.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatMessageRepository;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultChatRoomServiceTest {

  @Mock
  ChatRoomRepository chatRoomRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  ChatMessageRepository chatMessageRepository;

  @InjectMocks
  DefaultChatRoomService chatRoomService;

  @Nested
  @DisplayName("create 메서드는")
  class DescribeCreate {

    @Nested
    @DisplayName("파라미터가 null 이면")
    class ContextWith {

      @Test
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> chatRoomService.create(null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("파라미터가 유효하면")
    class ContextWithValid {

      @Test
      @DisplayName("생성된 채팅방 id를 반환한다")
      void It() {
        //given
        long sellerId = 1L;
        long winnerId = 2L;
        long productId = 1L;
        long chatRoomId = 1L;

        given(userRepository.findWinnerOfBiddingByProductId(anyLong()))
            .willReturn(Optional.of(getUser(winnerId)));

        given(chatRoomRepository.save(any(ChatRoom.class)))
            .willAnswer(methodInvokeMock -> {
              ChatRoom chatRoom = (ChatRoom)methodInvokeMock.getArguments()[0];
              ReflectionTestUtils.setField(chatRoom, "id", chatRoomId);
              return chatRoom;
            });

        //when
        chatRoomService.create(getProduct(productId, sellerId));

        //then
        verify(userRepository).findWinnerOfBiddingByProductId(anyLong());
      }
    }
  }

  @Nested
  @DisplayName("findAll메서드는")
  class DescribeFindAll {

    @Nested
    @DisplayName("user id 가 음수이면")
    class ContextWithNonPositiveUserId {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException(long userId) {
        //when, then
        ChatRoomSelectRequest request = new ChatRoomSelectRequest(1, 10);
        assertThatThrownBy(() -> chatRoomService.findAll(userId, request));
      }
    }

    @Nested
    @DisplayName("request 가 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> chatRoomService.findAll(1L, null));
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidParameter {

      @Test
      @DisplayName("해당 조건의 채팅방 목록을 반환한다")
      void ItResponseChatRoomList() {
        //given
        long chatRoomId = 1L;
        long sellerId = 1L;
        long winnerId = 2L;
        long productId = 1L;

        User seller = getUser(sellerId);
        User winner = getUser(winnerId);
        Product product = getProduct(productId, sellerId);
        ChatRoom chatRoom = getChatRoom(chatRoomId, seller, winner, product);

        ChatRoomSelectRequest request = new ChatRoomSelectRequest(0, 10);

        given(chatRoomRepository.findAllByUserId(anyLong(), any(ChatRoomSelectRequest.class)))
            .willReturn(List.of(chatRoom));

        given(chatMessageRepository.findLastChatMessageOfChatRoom(anyLong()))
            .willReturn(Optional.of(ChatMessage.getEmptyMessage()));

        //when
        List<ChatRoomSelectResponse> responses = chatRoomService.findAll(sellerId, request);

        //then
        assertThat(responses.size()).isEqualTo(1);
      }
    }
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
