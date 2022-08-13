package com.saiko.bidmarket.chat.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectResponse;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.service.ChatMessageService;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = ChatMessageApiController.class)
class ChatMessageApiControllerTest extends ControllerSetUp {

  @MockBean
  ChatMessageService chatMessageService;

  @Nested
  @DisplayName("getAll 메서드는")
  class DescribeGetAll {

    @WithMockCustomLoginUser
    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithCall {

      @Test
      @DisplayName("해당 채팅방의 페이징된 메시지 리스트를 반환한다")
      void ItResponsePageListOfChatMessage() throws Exception {
        //given
        long chatRoomId = 1L;

        User seller = getUser(1);
        User winner = getUser(2);
        Product product = getProduct(seller, 1);
        ChatRoom chatRoom = getChatRoom(seller, winner, product, chatRoomId);
        ChatMessage chatMessage = getChatMessage(seller, chatRoom, 1);

        String requestUri = "/api/v1/chatRooms/{chatRoomId}/messages";
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(requestUri, chatRoomId)
            .queryParam("offset", "0")
            .queryParam("limit", "10");

        ChatMessageSelectResponse response = ChatMessageSelectResponse.from(chatMessage);

        given(chatMessageService.findAll(anyLong(), anyLong(), any(ChatMessageSelectRequest.class)))
            .willReturn(List.of(response));

        //when
        ResultActions perform = mockMvc.perform(request);

        //then
        perform
            .andExpect(status().isOk())
            .andDo(document(
                "Chat Message Select",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("chatRoomId").description("채팅방 번호")
                ),
                requestParameters(
                    parameterWithName("offset").description("메시지 조회 시작 번호"),
                    parameterWithName("limit").description("메시지 조회 개수")
                ),
                responseFields(
                    fieldWithPath("[].userInfo.userId")
                        .type(JsonFieldType.NUMBER)
                        .description("유저 번호"),
                    fieldWithPath("[].userInfo.username")
                        .type(JsonFieldType.STRING)
                        .description("유저 닉네임"),
                    fieldWithPath("[].userInfo.profileImage")
                        .type(JsonFieldType.STRING)
                        .description("유저 프로필 이미지"),
                    fieldWithPath("[].content")
                        .type(JsonFieldType.STRING)
                        .description("채팅 내용"),
                    fieldWithPath("[].createdAt")
                        .type(JsonFieldType.STRING)
                        .description("채팅 보낸 시간")
                )
            ));
      }
    }

    @WithMockCustomLoginUser
    @Nested
    @DisplayName("메시지 조회 시작번호 가 숫자가 아닌경우")
    class ContextWithNonNumberOffset {

      @Test
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest() throws Exception {
        //given
        String requestUri = "/api/v1/chatRooms/{chatRoomId}/messages";
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(requestUri, 1)
            .queryParam("offset", "Test")
            .queryParam("limit", "10");

        //when
        ResultActions perform = mockMvc.perform(request);

        //then
        perform.andExpect(status().isBadRequest());
      }
    }

    @WithMockCustomLoginUser
    @Nested
    @DisplayName("메시지 조회 시작번호가 음수인 경우")
    class ContextWithNegativeNumberOffset {

      @ParameterizedTest
      @ValueSource(longs = {-1, Long.MIN_VALUE})
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest(long offset) throws Exception {
        String requestUri = "/api/v1/chatRooms/{chatRoomId}/messages";
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(requestUri, 1)
            .queryParam("offset", String.valueOf(offset))
            .queryParam("limit", "10");

        //when
        ResultActions perform = mockMvc.perform(request);

        //then
        perform.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("메시지 조회 개수 가 숫자가 아닌경우")
    class ContextWithNonNumberLimit {

      @WithMockCustomLoginUser
      @Test
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest() throws Exception {
        String requestUri = "/api/v1/chatRooms/{chatRoomId}/messages";
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(requestUri, 1)
            .queryParam("offset", "0")
            .queryParam("limit", "Test");

        //when
        ResultActions perform = mockMvc.perform(request);

        //then
        perform.andExpect(status().isBadRequest());
      }
    }

    @WithMockCustomLoginUser
    @Nested
    @DisplayName("메시지 조회 개수가 양수가 아닌 경우")
    class ContextWithNonPositiveNumberLimit {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest(long limit) throws Exception {
        String requestUri = "/api/v1/chatRooms/{chatRoomId}/messages";
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(requestUri, 1)
            .queryParam("offset", "0")
            .queryParam("limit", String.valueOf(limit));

        //when
        ResultActions perform = mockMvc.perform(request);

        //then
        perform.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("유저가 권한이 없는 경우")
    class ContextWithNonAuthorizedMember {

      @Test
      @DisplayName("forbidden 을 응답한다")
      void ItResponseForbidden() throws Exception {
        String requestUri = "/api/v1/chatRooms/{chatRoomId}/messages";
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(requestUri, 1)
            .queryParam("offset", "0")
            .queryParam("limit", "10");

        //when
        ResultActions perform = mockMvc.perform(request);

        //then
        perform.andExpect(status().isForbidden());
      }
    }
  }

  private User getUser(long userId) {
    User user = User
        .builder()
        .username("test")
        .profileImage("test")
        .group(new Group())
        .provider("test")
        .providerId("test")
        .build();
    ReflectionTestUtils.setField(user, "id", userId);
    return user;
  }

  private ChatRoom getChatRoom(
      User seller,
      User winner,
      Product product,
      long chatRoomId
  ) {
    ChatRoom chatRoom = ChatRoom
        .builder()
        .product(product)
        .seller(seller)
        .winner(winner)
        .build();

    ReflectionTestUtils.setField(chatRoom, "id", chatRoomId);
    return chatRoom;
  }

  private Product getProduct(
      User seller,
      long productId
  ) {
    Product product = Product
        .builder()
        .title("test")
        .images(List.of("https://test.img"))
        .category(Category.ETC)
        .minimumPrice(10000)
        .writer(seller)
        .description("test")
        .location("test")
        .build();
    ReflectionTestUtils.setField(product, "id", productId);
    return product;
  }

  private ChatMessage getChatMessage(
      User sender,
      ChatRoom chatRoom,
      long chatMessageId
  ) {
    ChatMessage chatMessage = ChatMessage
        .builder()
        .message("test")
        .chatRoom(chatRoom)
        .sender(sender)
        .build();

    ReflectionTestUtils.setField(chatMessage, "id", chatMessageId);
    ReflectionTestUtils.setField(chatMessage, "createdAt", LocalDateTime.now());
    return chatMessage;
  }
}
