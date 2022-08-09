package com.saiko.bidmarket.chat.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.service.ChatRoomService;
import com.saiko.bidmarket.chat.service.dto.ChatRoomSelectParam;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = ChatRoomApiController.class)
class ChatRoomApiControllerTest extends ControllerSetUp {

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  ChatRoomService chatRoomService;

  static final String REQUEST_URI = "/api/v1/chatRooms";

  @Nested
  @WithMockCustomLoginUser
  @DisplayName("findAll 메서드는")
  class DescribeFindAll {

    @Nested
    @DisplayName("유효한 요청 데이터가 전달되면")
    class ContextWithTokenAndQueryParamHasSameUserId {

      @Test
      @DisplayName("해당 유저의 채팅 목록을 응답한다")
      void ItResponseChatRoomList() throws Exception {
        //given
        long sellerId = 1L;
        long winnerId = 2L;
        long productId = 1L;
        long chatRoomId = 1L;
        long chatMessageId = 1L;

        User seller = getUser(sellerId);
        User winner = getUser(winnerId);
        Product product = getProduct(seller, productId);
        ChatRoom chatRoom = getChatRoom(seller, winner, product, chatRoomId);
        ChatMessage chatMessage = getChatMessage(winner, chatRoom, chatMessageId);

        given(chatRoomService.findAll(any(ChatRoomSelectParam.class)))
            .willReturn(List.of(ChatRoomSelectResponse.of(chatRoom, winner, chatMessage)));

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(REQUEST_URI)
            .queryParam("offset", "1")
            .queryParam("limit", "1");

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isOk())
                .andDo(document("List chat rooms",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                    parameterWithName("offset").description("채팅방 조회 시작 번호"),
                                    parameterWithName("limit").description("채팅방 조회 개수")
                                ),
                                responseFields(
                                    fieldWithPath("[].chatRoomId")
                                        .type(JsonFieldType.NUMBER).description("채팅방 번호"),
                                    fieldWithPath("[].productInfo.productId")
                                        .type(JsonFieldType.NUMBER).description("상품 번호"),
                                    fieldWithPath("[].productInfo.thumbnailImg")
                                        .type(JsonFieldType.STRING).description("상품 이미지"),
                                    fieldWithPath("[].opponentUserInfo.username")
                                        .type(JsonFieldType.STRING).description("상대방 유저명"),
                                    fieldWithPath("[].opponentUserInfo.profileImg")
                                        .type(JsonFieldType.STRING).description("상대방 유저 프로필"),
                                    fieldWithPath("[].lastMessage")
                                        .type(JsonFieldType.STRING).description("채팅 메시지"),
                                    fieldWithPath("[].lastMessageDate")
                                        .type(JsonFieldType.STRING).description("채팅 보낸 시간")
                                )));
      }
    }

    @Nested
    @DisplayName("offset 이 음수일 경우")
    class ContextWithNegativeOffset {

      @ParameterizedTest
      @ValueSource(longs = {-1, Long.MIN_VALUE})
      @DisplayName("404를 응답한다")
      void ItResponse404(long offset) throws Exception {
        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(REQUEST_URI)
            .queryParam("offset", String.valueOf(offset))
            .queryParam("limit", "1");

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("limit 양수가 아닐 경우")
    class ContextWithNonPositiveLimit {

      @ParameterizedTest
      @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
      @DisplayName("404를 응답한다")
      void ItResponse404(int limit) throws Exception {
        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(REQUEST_URI)
            .queryParam("offset", "1")
            .queryParam("limit", String.valueOf(limit));

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }

  }

  private User getUser(long userId) {
    User user = User.builder()
                    .username("test")
                    .profileImage("test")
                    .group(new Group())
                    .provider("test")
                    .providerId("test")
                    .build();
    ReflectionTestUtils.setField(user, "id", userId);
    return user;
  }

  private ChatRoom getChatRoom(User seller, User winner, Product product, long chatRoomId) {
    ChatRoom chatRoom = ChatRoom.builder()
                                .product(product)
                                .seller(seller)
                                .winner(winner)
                                .build();

    ReflectionTestUtils.setField(chatRoom, "id", chatRoomId);
    return chatRoom;
  }

  private Product getProduct(User seller, long productId) {
    Product product = Product.builder()
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

  private ChatMessage getChatMessage(User sender, ChatRoom chatRoom, long chatMessageId) {
    ChatMessage chatMessage = ChatMessage.builder()
                                         .message("test")
                                         .chatRoom(chatRoom)
                                         .sender(sender)
                                         .build();

    ReflectionTestUtils.setField(chatMessage, "id", chatMessageId);
    ReflectionTestUtils.setField(chatMessage, "createdAt", LocalDateTime.now());
    return chatMessage;
  }
}
