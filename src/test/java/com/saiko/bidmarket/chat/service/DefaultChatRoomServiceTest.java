package com.saiko.bidmarket.chat.service;

import static org.assertj.core.api.Assertions.*;
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

import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.chat.service.dto.ChatRoomCreateParam;
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
        Long sellerId = 1L;
        Long winnerId = 2L;
        Long productId = 1L;
        Long chatRoomId = 1L;

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(getUser(sellerId)));

        given(userRepository.findWinnerOfBiddingByProductId(anyLong()))
            .willReturn(Optional.of(getUser(winnerId)));

        given(chatRoomRepository.save(any(ChatRoom.class)))
            .willAnswer(methodInvokeMock -> {
              ChatRoom chatRoom = (ChatRoom)methodInvokeMock.getArguments()[0];
              ReflectionTestUtils.setField(chatRoom, "id", chatRoomId);
              return chatRoom;
            });

        //when
        long savedChatRoomId = chatRoomService.create(
            ChatRoomCreateParam.from(getProduct(productId, sellerId)));

        //then
        assertThat(savedChatRoomId).isEqualTo(chatRoomId);
      }
    }
  }

  private User getUser(long userId) {
    User user = User.builder()
                    .username("test")
                    .provider("test")
                    .providerId("test")
                    .profileImage("test")
                    .group(new Group())
                    .build();

    ReflectionTestUtils.setField(user, "id", userId);
    return user;
  }

  private Product getProduct(long productId, long userId) {
    Product product = Product.builder()
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
