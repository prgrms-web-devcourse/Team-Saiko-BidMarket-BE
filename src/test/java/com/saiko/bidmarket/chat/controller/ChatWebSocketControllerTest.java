package com.saiko.bidmarket.chat.controller;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.controller.dto.ChatSendMessage;
import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.service.ChatMessageService;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

@ActiveProfiles("ws_test")
@SpringBootTest(webEnvironment = DEFINED_PORT)
class ChatWebSocketControllerTest {

  @MockBean
  ChatMessageService chatMessageService;

  BlockingQueue<Object> blockingQueue;
  WebSocketStompClient stompClient;
  WebSocketHttpHeaders handshakeHeaders;
  StompHeaders connectHeaders;
  StompSession session;

  String WS_URI = "ws://localhost:8080/ws-stomp";

  StompSessionHandlerAdapter getStompSessionHandlerAdapter() {
    return new StompSessionHandlerAdapter() {
    };
  }

  StompFrameHandler getStompFrameHandler(Class<?> type) {
    return new StompFrameHandler() {

      @Override
      public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
        return type;
      }

      @Override
      public void handleFrame(@NotNull StompHeaders headers, Object payload) {
        blockingQueue.add(payload);
      }
    };
  }

  @BeforeEach
  public void setup() throws Exception {
    blockingQueue = new LinkedBlockingDeque<>();
    stompClient = new WebSocketStompClient(
        new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    handshakeHeaders = new WebSocketHttpHeaders();
    connectHeaders = new StompHeaders();
    session = stompClient.connect(WS_URI, handshakeHeaders, connectHeaders,
                                  getStompSessionHandlerAdapter()).get(1, SECONDS);
  }

  private User getUser(long userId) {
    User user = User.builder()
                    .username("강철중")
                    .profileImage("https://naver.com/img1")
                    .provider("naver")
                    .group(new Group())
                    .providerId("1234")
                    .build();

    ReflectionTestUtils.setField(user, "id", userId);
    return user;
  }

  private ChatRoom getChatRoom(long roomId) {
    ChatRoom chatRoom = ChatRoom.builder()
                                .seller(getUser(1L))
                                .buyer(getUser(2L))
                                .build();
    ReflectionTestUtils.setField(chatRoom, "id", roomId);
    return chatRoom;
  }

  @Nested
  @DisplayName("send 메서드는")
  class DescribeSend {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidData {

      @Test
      @DisplayName("메시지를 발행한다")
      void ItPublishMessage() throws Exception {
        //given
        long userId = 1L;
        long roomId = 1L;

        String subUrl = MessageFormat.format("/chat/room/{0}", roomId);
        session.subscribe(subUrl, getStompFrameHandler(ChatPublishMessage.class));

        ChatMessage chatMessage = ChatMessage.builder()
                                             .message("Test content")
                                             .sender(getUser(userId))
                                             .chatRoom(getChatRoom(roomId))
                                             .build();

        ChatPublishMessage chatPublishMessage = ChatPublishMessage.of(chatMessage);
        given(chatMessageService.create(any(ChatMessageCreateParam.class)))
            .willReturn(chatPublishMessage);

        //when
        String pubUrl = MessageFormat.format("/message/room/{0}", roomId);
        session.send(pubUrl, new ChatSendMessage(userId, "Test content"));

        //then
        ChatPublishMessage publishMessage = (ChatPublishMessage)blockingQueue.poll(1, SECONDS);
        assertNotNull(publishMessage);
      }
    }
  }
}
